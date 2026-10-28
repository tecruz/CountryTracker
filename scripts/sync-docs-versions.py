#!/usr/bin/env python3
"""Synchronize dependency versions in documentation with the single source of truth."""

from __future__ import annotations

import argparse
import re
import sys
import tomllib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERSION_CATALOG = ROOT / "gradle" / "libs.versions.toml"
GRADLE_WRAPPER = ROOT / "gradle" / "wrapper" / "gradle-wrapper.properties"
APP_BUILD = ROOT / "app" / "build.gradle.kts"


def read_version_catalog() -> dict[str, str]:
    with VERSION_CATALOG.open("rb") as stream:
        versions = tomllib.load(stream)["versions"]
    return {key: str(value) for key, value in versions.items()}


def read_match(path: Path, pattern: str, label: str) -> str:
    text = path.read_text(encoding="utf-8")
    match = re.search(pattern, text, re.MULTILINE)
    if match is None:
        raise ValueError(f"could not determine {label} from {path.relative_to(ROOT)}")
    return match.group(1)


def _read_jacoco_version(build_file: Path, versions: dict[str, str]) -> str:
    """Read JaCoCo version from build.gradle.kts.

    Supports both a hardcoded string (toolVersion = "0.8.14") and a version
    catalog reference (toolVersion = libs.versions.jacoco.get()).
    """
    text = build_file.read_text(encoding="utf-8")
    # Try hardcoded string first
    match = re.search(
        r'^jacoco\s*\{[^}]*?toolVersion\s*=\s*"([^"]+)"',
        text,
        re.MULTILINE,
    )
    if match:
        return match.group(1)
    # Fall back to version catalog reference
    match = re.search(
        r'^jacoco\s*\{[^}]*?toolVersion\s*=\s*libs\.versions\.(\w+)\.get\(\)',
        text,
        re.MULTILINE,
    )
    if match:
        catalog_key = match.group(1)
        if catalog_key in versions:
            return versions[catalog_key]
        raise ValueError(
            f"could not resolve JaCoCo version catalog key '{catalog_key}' "
            f"from {build_file.relative_to(ROOT)}"
        )
    raise ValueError(f"could not determine JaCoCo version from {build_file.relative_to(ROOT)}")


def load_versions() -> dict[str, str]:
    versions = read_version_catalog()
    versions["gradle"] = read_match(
        GRADLE_WRAPPER,
        r"^distributionUrl=.*?gradle-([0-9][0-9.]*)-bin\.zip$",
        "Gradle version",
    )
    versions["jacoco"] = _read_jacoco_version(APP_BUILD, versions)
    versions["java"] = read_match(
        APP_BUILD,
        r"sourceCompatibility\s*=\s*JavaVersion\.VERSION_(\d+)",
        "Java version",
    )
    return versions


def table(headers: tuple[str, ...], rows: tuple[tuple[str, ...], ...]) -> str:
    lines = [
        "| " + " | ".join(headers) + " |",
        "|" + "|".join("-" * (len(header) + 2) for header in headers) + "|",
    ]
    lines.extend("| " + " | ".join(row) + " |" for row in rows)
    return "\n".join(lines)


def prerequisites(versions: dict[str, str]) -> str:
    return "\n".join(
        (
            "- Android Studio Ladybug or newer",
            f"- JDK {versions['java']}",
            f"- Android SDK {versions['compileSdk']} (compileSdk)",
            f"- Minimum device: Android 7.0 (API {versions['minSdk']})",
        )
    )


def readme_tech_stack(versions: dict[str, str]) -> str:
    return table(
        ("Category", "Technology", "Version"),
        (
            ("Language", "Kotlin", versions["kotlin"]),
            ("UI Framework", "Jetpack Compose (BOM)", versions["composeBom"]),
            ("Design System", "Material 3 Expressive", versions["material3Expressive"]),
            ("Adaptive UI", "Material 3 Adaptive", versions["material3Adaptive"]),
            ("Architecture", "Clean Architecture + MVVM", "-"),
            ("DI", "Koin", versions["koin"]),
            ("Database", "Room", versions["room"]),
            ("Async", "Coroutines + Flow", versions["coroutines"]),
            ("Navigation", "Navigation Compose", versions["navigationCompose"]),
            ("Code Formatting", "Spotless + ktlint", f"{versions['spotless']} / {versions['ktlint']}"),
            ("Static Analysis", "Detekt", versions["detekt"]),
            ("Code Coverage", "Kover + JaCoCo", f"{versions['kover']} / {versions['jacoco']}"),
            ("Build System", "Gradle (Kotlin DSL)", versions["gradle"]),
            ("Annotation Processing", "KSP", versions["ksp"]),
        ),
    )


def architecture_technology_versions(versions: dict[str, str]) -> str:
    return table(
        ("Technology", "Version"),
        (
            ("Kotlin", versions["kotlin"]),
            ("Jetpack Compose BOM", versions["composeBom"]),
            ("Material 3 Expressive", versions["material3Expressive"]),
            ("Material 3 Adaptive", versions["material3Adaptive"]),
            ("Room", versions["room"]),
            ("Koin", versions["koin"]),
            ("Navigation Compose", versions["navigationCompose"]),
            ("Coroutines", versions["coroutines"]),
            ("AGP", versions["agp"]),
            ("KSP", versions["ksp"]),
            ("Target SDK", versions["targetSdk"]),
            ("Min SDK", versions["minSdk"]),
        ),
    )


def architecture_testing_libraries(versions: dict[str, str]) -> str:
    return table(
        ("Library", "Purpose"),
        (
            (f"JUnit {versions['junit']}", "Unit test framework"),
            (f"MockK {versions['mockk']}", "Mocking library"),
            (f"Turbine {versions['turbine']}", "Flow testing"),
            (f"Coroutines Test {versions['coroutinesTest']}", "Coroutine testing"),
            (f"Arch Core Testing {versions['archCoreTesting']}", "Architecture component testing"),
            (f"Robolectric {versions['robolectric']}", "JVM-based Android framework simulation"),
            (f"Compose UI Test (BOM {versions['composeBom']})", "Compose UI testing"),
            (f"Room Testing {versions['room']}", "Room in-memory database testing"),
        ),
    )


def architecture_coverage_tools(versions: dict[str, str]) -> str:
    return "\n".join(
        (
            f"- **Kover {versions['kover']}** for unit test coverage "
            "(Compose-aware instrumentation, accurate for `@Composable` functions)",
            f"- **JaCoCo {versions['jacoco']}** for instrumented test coverage and combined reports",
        )
    )


def architecture_key_technologies(versions: dict[str, str]) -> str:
    return table(
        ("Technology", "Version", "Purpose"),
        (
            ("Kotlin", versions["kotlin"], "Primary language"),
            ("Jetpack Compose (BOM)", versions["composeBom"], "Declarative UI framework"),
            ("Material 3 Expressive", versions["material3Expressive"], "Design system"),
            ("Material 3 Adaptive", versions["material3Adaptive"], "Responsive layouts for all screen sizes"),
            ("Room", versions["room"], "Local SQLite database"),
            ("Koin", versions["koin"], "Dependency injection"),
            ("Coroutines", versions["coroutines"], "Asynchronous operations"),
            ("Flow", "(with Coroutines)", "Reactive data streams"),
            ("Navigation Compose", versions["navigationCompose"], "Screen navigation"),
            ("DataStore", versions["datastore"], "Preferences storage"),
            ("Splash Screen", versions["splashscreen"], "Native splash screen API"),
            ("SavedStateHandle", f"(with Lifecycle {versions['lifecycleRuntime']})", "Process death survival"),
        ),
    )


DOCUMENTS = {
    ROOT / "README.md": {
        "prerequisites": prerequisites,
        "tech-stack": readme_tech_stack,
    },
    ROOT / "ARCHITECTURE.md": {
        "technology-versions": architecture_technology_versions,
        "testing-libraries": architecture_testing_libraries,
        "coverage-tools": architecture_coverage_tools,
        "key-technologies": architecture_key_technologies,
    },
}


def render_block(name: str, body: str) -> str:
    return f"<!-- versions:{name}:start -->\n{body}\n<!-- versions:{name}:end -->"


def sync_document(path: Path, blocks: dict[str, str], versions: dict[str, str]) -> bool:
    text = path.read_text(encoding="utf-8")
    updated = text
    for name, render in blocks.items():
        start = f"<!-- versions:{name}:start -->"
        end = f"<!-- versions:{name}:end -->"
        pattern = re.compile(re.escape(start) + r".*?" + re.escape(end), re.DOTALL)
        if len(pattern.findall(updated)) != 1:
            raise ValueError(
                f"expected exactly one {start} ... {end} block in {path.relative_to(ROOT)}"
            )
        updated = pattern.sub(lambda _match, n=name, b=render(versions): render_block(n, b), updated)
    if updated == text:
        return False
    path.write_text(updated, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Update documentation versions from Gradle configuration."
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="exit with status 1 when documentation is out of date instead of writing changes",
    )
    args = parser.parse_args()

    try:
        versions = load_versions()
        changed = [
            path for path, blocks in DOCUMENTS.items() if sync_document(path, blocks, versions)
        ]
    except (OSError, KeyError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        return 2

    if not changed:
        print("Documentation versions are up to date.")
        return 0

    names = ", ".join(str(path.relative_to(ROOT)) for path in changed)
    if args.check:
        print(f"Documentation versions are out of date: {names}", file=sys.stderr)
        print("Run: python scripts/sync-docs-versions.py", file=sys.stderr)
        return 1

    print(f"Updated documentation versions: {names}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
