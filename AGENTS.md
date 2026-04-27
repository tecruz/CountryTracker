# AGENTS.md

## Build Commands

```bash
# Format check/fix
./gradlew spotlessCheck
./gradlew spotlessApply

# Static analysis
./gradlew detekt

# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires emulator)
./gradlew connectedDebugAndroidTest

# Coverage
./gradlew koverHtmlReport        # Unit tests (Compose-aware)
./gradlew jacocoAndroidTestReport  # Instrumented tests
./gradlew jacocoCombinedReport  # Unit + instrumented

# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Lint
./gradlew lintDebug
```

## Testing Stack

- JUnit 4, MockK, Turbine for unit tests
- Robolectric for Compose UI tests on JVM
- Kover for unit test coverage (accurate for @Composable)
- JaCoCo for instrumented test coverage

## Architecture Notes

- **Clean Architecture** with feature-based package structure (`features/countrylist/`, `features/countrydetail/`)
- **Koin** for DI (not Hilt) — modules defined per layer in each feature
- **MVI** pattern: `*State`, `*Action`, `*Event`, `*ViewModel`
- **Room** database with 75+ pre-populated countries to track visits

## Adaptive Layouts

Uses `WindowSizeClass` (Compact/Medium/Expanded) for responsive design across phones, foldables, tablets.

## CI Pipeline

Runs on every push/PR to `main`/`master`/`develop`:
1. Build + Unit Tests + Kover coverage
2. Code Coverage (requires emulator boot) — JaCoCo combined
3. Android Lint
4. Detekt
5. Spotless formatting check
6. Release APK (main/master only)

## Key Files

- `gradle/libs.versions.toml` — Version catalog
- `app/build.gradle.kts` — App config, Kover/JaCoCo exclusions, lint settings
- `config/detekt/detekt.yml` — Static analysis rules
- `.github/workflows/ci.yml` — CI pipeline