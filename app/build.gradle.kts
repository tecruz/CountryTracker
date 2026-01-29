plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    jacoco
}

android {
    namespace = "com.tecruz.countrytracker"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.tecruz.countrytracker"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.tecruz.countrytracker.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        warningsAsErrors = false
        abortOnError = false
        xmlReport = true
        htmlReport = true
        htmlOutput = file("${project.layout.buildDirectory.get()}/reports/lint/lint-results.html")
        xmlOutput = file("${project.layout.buildDirectory.get()}/reports/lint/lint-results.xml")
        checkDependencies = true
        disable += listOf("ObsoleteLintCustomCheck")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    parallel = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

// Spotless Configuration
spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(
                mapOf(
                    "android" to "true",
                    "max_line_length" to "120",
                    "indent_size" to "4",
                    "continuation_indent_size" to "4",
                    "insert_final_newline" to "true",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                    "ktlint_standard_package-name" to "disabled",
                    "ktlint_standard_function-naming" to "disabled",
                    "ktlint_standard_backing-property-naming" to "disabled",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

// JaCoCo Configuration
jacoco {
    toolVersion = "0.8.12"
}

// Exclude generated files from coverage
val jacocoExcludes =
    listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        // Hilt generated
        "**/*_HiltModules*.*",
        "**/*_Factory*.*",
        "**/*_MembersInjector*.*",
        "**/Hilt_*.*",
        "**/*Module_*.*",
        "**/dagger/**",
        "**/hilt_aggregated_deps/**",
        // Room generated
        "**/*_Impl*.*",
        "**/dao/*Dao_Impl*.*",
        // Data classes and sealed classes
        "**/model/*.*",
        "**/entity/*.*",
        // Navigation
        "**/navigation/*.*",
        // Theme
        "**/theme/*.*",
    )

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates code coverage report for unit tests"

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(
            file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"),
        )
        html.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/html"))
    }

    val debugTree =
        fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(jacocoExcludes)
        }

    val mainSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        },
    )
}

tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    group = "verification"
    description = "Generates code coverage report for instrumented tests"

    dependsOn("connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(
            file(
                "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoAndroidTestReport/jacocoAndroidTestReport.xml",
            ),
        )
        html.outputLocation.set(
            file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoAndroidTestReport/html"),
        )
    }

    val debugTree =
        fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(jacocoExcludes)
        }

    val mainSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include("outputs/code_coverage/debugAndroidTest/connected/**/*.ec")
        },
    )
}

tasks.register<JacocoReport>("jacocoCombinedReport") {
    group = "verification"
    description = "Generates combined code coverage report for both unit and instrumented tests"

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(
            file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoCombinedReport/jacocoCombinedReport.xml"),
        )
        html.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoCombinedReport/html"))
    }

    val debugTree =
        fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(jacocoExcludes)
        }

    val mainSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
            )
        },
    )
}

// Coverage verification task
tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
    group = "verification"
    description = "Verifies code coverage meets minimum threshold"

    dependsOn("jacocoTestReport")

    val debugTree =
        fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(jacocoExcludes)
        }

    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        },
    )

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 50% minimum coverage
            }
        }
        rule {
            element = "CLASS"
            excludes =
                listOf(
                    "*.BuildConfig",
                    "*.R",
                    "*.*Module*",
                    "*.*Factory*",
                )
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.40".toBigDecimal()
            }
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Unit Testing
    testImplementation(libs.bundles.testing)

    // Android Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug
    debugImplementation(libs.bundles.compose.debug)
}
