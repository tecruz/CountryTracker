# Country Tracker

[![CI](https://github.com/tecruz/CountryTracker/actions/workflows/ci.yml/badge.svg)](https://github.com/tecruz/CountryTracker/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/tecruz/CountryTracker/branch/master/graph/badge.svg?token=jT9FMf2Q4Y)](https://codecov.io/gh/tecruz/CountryTracker)

A modern Android application for tracking countries you've visited around the world. Built with Jetpack Compose, Clean Architecture, and Material 3.

## Screenshots

| Country List | World Map | Country Detail |  
|:---:|:---:|:---:|
| <img width="540" height="1200" alt="image" src="https://github.com/user-attachments/assets/e3ed1c94-36dd-4df2-b418-550722f5eb15" /> | <img width="540" height="1200" alt="image" src="https://github.com/user-attachments/assets/744f6c5d-2845-4425-8a24-2a13decb9eea" /> | <img width="540" height="1200" alt="image" src="https://github.com/user-attachments/assets/96902965-f22d-43d0-ab06-e2fdfae7d05d" /> |

| Tablet portrait | Tablet landscape |
|:---:|:---:|
|<img width="800" height="1280" alt="image" src="https://github.com/user-attachments/assets/5bb68a63-d862-47d2-9d86-eee6ea428dba" /> | <img width="1280" height="800" alt="image" src="https://github.com/user-attachments/assets/7ce985f5-dc1e-480b-bb31-3e77d13cb374" />

| Foldable portrait | Foldable landscape |
|:---:|:---:|
|<img width="1104" height="920" alt="image" src="https://github.com/user-attachments/assets/115fca64-d317-44d5-a57c-efdb10eb1955" /> | <img width="920" height="1104" alt="image" src="https://github.com/user-attachments/assets/cafea5bf-e0be-4b2e-8849-31479764e34f" />



## Features

- **Interactive World Map** - SVG-based world map canvas with visited countries highlighted in green
- **Statistics Dashboard** - View visited count, total countries, and completion percentage
- **Search & Filter** - Find countries by name, filter by region and visited status
- **Visit Tracking** - Mark countries as visited with date picker
- **5-Star Rating System** - Rate your travel experiences
- **Travel Notes** - Add personal notes up to 500 characters per country
- **75+ Countries** - Pre-populated database across 6 continents
- **Dark/Light Theme** - Material 3 dynamic theming support
- **Splash Screen** - Native splash screen with background data loading
- **Edge-to-Edge Display** - Modern immersive UI with themed status bar
- **Process Death Handling** - Filter states and UI preserved across configuration changes
- **Adaptive Layouts** - WindowSizeClass support for responsive tablet/foldable experiences

## Prerequisites

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 36 (compileSdk)
- Minimum device: Android 7.0 (API 24)

## Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/tecruz/CountryTracker.git
   ```
2. Open in Android Studio
3. Wait for Gradle sync to complete
4. Run on emulator or device

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| Language | Kotlin | 2.3.10 |
| UI Framework | Jetpack Compose (BOM) | 2026.02.00 |
| Design System | Material 3 Expressive | 1.5.0-alpha14 |
| Adaptive UI | Material 3 Adaptive | 1.2.0 |
| Architecture | Clean Architecture + MVVM | - |
| DI | Hilt | 2.59.1 |
| Database | Room | 2.8.4 |
| Async | Coroutines + Flow | 1.10.2 |
| Navigation | Navigation Compose | 2.9.7 |
| Code Formatting | Spotless + ktlint | 8.2.1 / 1.5.0 |
| Static Analysis | Detekt | 1.23.8 |
| Code Coverage | Kover + JaCoCo | 0.9.7 / 0.8.12 |
| Build System | Gradle (Kotlin DSL) | 9.0.1 |
| Annotation Processing | KSP | 2.3.5 |

## Project Structure

```
app/src/main/kotlin/com/tecruz/countrytracker/
├── CountryTrackerApplication.kt
├── MainActivity.kt
├── core/
│   ├── data/
│   │   ├── database/              # Room database, DAO, Entity
│   │   └── datasource/            # Data loaders
│   ├── designsystem/              # Theme, Colors, Typography, Previews
│   ├── di/                        # Core DI modules
│   ├── navigation/                # Navigation graph
│   └── util/                      # Shared utilities (SVG parser, dispatchers, window size)
└── features/
    ├── countrydetail/
    │   ├── data/                  # Repository impl, mappers, DI
    │   ├── domain/                # Use cases, models, repository interface
    │   └── presentation/          # Screen, ViewModel, UI model
    └── countrylist/
        ├── data/                  # Repository impl, mappers, datasource, DI
        ├── domain/                # Use cases, models (CountryListItem, CountryStatistics)
        └── presentation/          # Screen, ViewModel, tab model, world map components
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed architecture documentation.

## Quality

### Code Formatting

```bash
# Check formatting
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply
```

### Static Analysis

```bash
./gradlew detekt
```

### Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedDebugAndroidTest

# Unit test coverage (Kover - Compose-aware)
./gradlew koverHtmlReport

# Instrumented test coverage (JaCoCo)
./gradlew jacocoAndroidTestReport

# Combined coverage (JaCoCo - unit + instrumented)
./gradlew jacocoCombinedReport
```

### Test Coverage

| Layer | Tests |
|-------|-------|
| **Data** | `CountryListRepositoryImplTest`, `CountryDetailRepositoryImplTest` |
| **Domain** | `GetAllCountriesUseCaseTest`, `GetCountryStatisticsUseCaseTest`, `MarkCountryAsVisitedUseCaseTest`, `MarkCountryAsUnvisitedUseCaseTest`, `UpdateCountryNotesUseCaseTest`, `UpdateCountryRatingUseCaseTest`, `GetCountryByCodeUseCaseTest` |
| **Presentation** | `CountryListViewModelTest`, `CountryDetailViewModelTest`, `CountryListScreenComposableTest`, `CountryListRowResponsiveTest` |
| **Design System** | `AdaptiveScaffoldTest`, `AdaptiveGridTest`, `ResponsiveCardTest`, `WindowSizeClassExtTest` |
| **UI (Instrumented)** | `CountryListScreenTest`, `CountryListScreenCompactTest`, `CountryListScreenMediumTest`, `CountryDetailScreenTest`, `CountryDetailScreenCompactTest`, `CountryDetailScreenTabletTest`, `CountryDetailScreenDialogsTest` |
| **World Map** | `WorldMapColoringTest`, `WorldMapCanvasCompactTest`, `WorldMapCanvasTabletTest`, `WorldMapPathDataTest` |
| **Integration** | `CountryDaoTest`, `CountryListRepositoryIntegrationTest`, `CountryDetailRepositoryIntegrationTest` |
| **E2E & Adaptive** | `CountryTrackerE2ETest`, `AdaptiveLayoutE2ETest`, `OrientationChangeTest`, `FoldableTransitionTest` |

Testing libraries: JUnit 4, MockK, Turbine, Coroutines Test, Compose UI Test, Robolectric

### CI Pipeline

The CI pipeline runs on every push and pull request:

| Job | Description |
|-----|-------------|
| **Build & Unit Tests** | Compiles debug APK, runs unit tests, generates Kover coverage report |
| **Code Formatting** | Verifies code style with Spotless + ktlint |
| **Android Lint** | Runs Android lint checks |
| **Detekt Analysis** | Static code analysis |
| **Code Coverage** | Boots emulator, runs combined JaCoCo report (unit + instrumented), uploads to Codecov |
| **Build Release APK** | Builds release APK (main/master only) |

## Build Variants

- **Debug** - Full logging, test coverage enabled
- **Release** - Minification with R8/ProGuard

## License

This project is available for educational purposes.
