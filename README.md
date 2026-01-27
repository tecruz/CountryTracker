# Country Tracker

A modern Android application for tracking countries you've visited around the world. Built with Jetpack Compose, Clean Architecture, and Material 3.

## Features

- **Statistics Dashboard** - View visited count, total countries, and completion percentage
- **Search & Filter** - Find countries by name and filter by region (Africa, Asia, Europe, North America, South America, Oceania)
- **Visit Tracking** - Mark countries as visited with date picker
- **5-Star Rating System** - Rate your travel experiences
- **Travel Notes** - Add personal notes up to 500 characters per country
- **75+ Countries** - Pre-populated database across 6 continents
- **Dark/Light Theme** - Material 3 dynamic theming support
- **Smooth Animations** - Polished UI transitions and interactions

## Prerequisites

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 36 (compileSdk)
- Minimum device: Android 7.0 (API 24)

## Getting Started

1. Clone or download the project
2. Open in Android Studio
3. Wait for Gradle sync to complete
4. Run on emulator or device (Shift+F10)

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| Language | Kotlin | 2.3.0 |
| UI Framework | Jetpack Compose | 2026.01.00 |
| Design System | Material 3 | Latest |
| Architecture | Clean Architecture + MVVM | - |
| DI | Hilt | 2.59 |
| Database | Room | 2.8.4 |
| Async | Coroutines + Flow | 1.10.2 |
| Navigation | Navigation Compose | 2.9.6 |
| Build System | Gradle (Kotlin DSL) | - |
| Annotation Processing | KSP | 2.3.4 |

## Project Structure

```
app/src/main/kotlin/com/example/countrytracker/
├── CountryTrackerApplication.kt     # Hilt Application
├── data/                            # Data Layer
│   ├── local/
│   │   ├── dao/CountryDao.kt
│   │   ├── database/CountryDatabase.kt
│   │   └── entity/CountryEntity.kt
│   └── repository/CountryRepositoryImpl.kt
├── domain/                          # Domain Layer
│   ├── model/Country.kt
│   ├── repository/CountryRepository.kt
│   └── usecase/
│       ├── GetAllCountriesUseCase.kt
│       ├── GetCountryStatisticsUseCase.kt
│       └── MarkCountryAsVisitedUseCase.kt
├── presentation/                    # Presentation Layer
│   ├── MainActivity.kt
│   ├── navigation/CountryTrackerNavHost.kt
│   ├── countrylist/
│   │   ├── CountryListScreen.kt
│   │   └── CountryListViewModel.kt
│   ├── countrydetail/
│   │   ├── CountryDetailScreen.kt
│   │   └── CountryDetailViewModel.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── di/                              # Dependency Injection
    ├── DatabaseModule.kt
    └── RepositoryModule.kt
```

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

### Test Coverage

- **Use Cases**: `GetAllCountriesUseCaseTest`, `GetCountryStatisticsUseCaseTest`
- **Repository**: `CountryRepositoryImplTest`
- **ViewModels**: `CountryListViewModelTest`, `CountryDetailViewModelTest`

Testing libraries: JUnit 4, MockK, Turbine, Coroutines Test

## Architecture

This project follows Clean Architecture with three layers:

- **Domain** - Pure Kotlin business logic, framework-independent
- **Data** - Room database persistence, repository implementations
- **Presentation** - Jetpack Compose UI, MVVM pattern with ViewModels

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed documentation.

## Build Variants

- **Debug** - Full logging and debugging enabled
- **Release** - Minification with ProGuard/R8

## License

This project is available for educational purposes.
