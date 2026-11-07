# Country Tracker - Clean Architecture Implementation

## Architecture Overview

This app follows **Clean Architecture** principles with a **feature-based package structure** and **domain-driven design**.

### Technology Versions

<!-- versions:technology-versions:start -->
| Technology | Version |
|------------|---------|
| Kotlin | 2.4.21 |
| Jetpack Compose BOM | 2026.09.00 |
| Material 3 Expressive | 1.5.0-beta02 |
| Material 3 Adaptive | 1.3.0 |
| Room | 2.8.5 |
| Koin | 4.2.2 |
| Navigation Compose | 2.10.2 |
| Coroutines | 1.11.0 |
| AGP | 9.5.0 |
| KSP | 2.3.12 |
| Target SDK | 37 |
| Min SDK | 24 |
<!-- versions:technology-versions:end -->

## Toolchain compatibility

The version table above is generated from `gradle/libs.versions.toml`, the Gradle wrapper, and `app/build.gradle.kts`. The only catalog edit is Material 3 Adaptive, raised from 1.2.0 to 1.3.0 so the declaration matches Compose BOM `2026.09.00`. Gradle was already selecting 1.3.0. Newer releases were not applied only because they exist.

| Requirement | Declared | Compatibility |
|-------------|----------|----------------|
| Android Gradle Plugin | 9.5.0 | Minimum Gradle 9.6.0, minimum JDK 17, maximum API level 37 |
| Gradle wrapper | 9.8.2 | Newer than the AGP 9.5 minimum. CI and local builds use the wrapper |
| JDK | 17 | Bytecode target is Java 17. CI uses Temurin 17. AGP 9.5 runs on JDK 17 or newer |
| Kotlin / KSP | 2.4.21 / 2.3.12 | KSP 2.3.x is the plugin line that supports Kotlin 2.4. Room's compiler runs on this KSP version |
| Compose BOM | 2026.09.00 | Stable BOM. It would select Material 3 1.4.0; this app overrides that artifact |
| compileSdk / targetSdk | 37 | Final Android 17 base platform, API 37.0. AGP 9.5 supports API 37 |
| minSdk | 24 | Unchanged |

### SDK 37 and Google Play

API 37.0 is the stable Android 17 base SDK, released 16 June 2026. The installed platform reports `AndroidVersion.ApiLevel=37.0`, an empty codename, and `PreviewSdkInt=0`. Google Play's target API policy, in force since 31 August 2026, requires new apps and updates to target API 36 or higher, so target 37 is accepted. Play has not published a rule that rejects API 37. `minSdk` stays at 24; the API 37 behavior set does not require raising it.

Behavior changes that apply when an app targets API 37 were reviewed against this codebase:

- The app has no widgets, custom notification `RemoteViews`, SMS handling, local-network discovery, Bluetooth sockets, audio playback, or reflection on `MessageQueue` or `static final` fields.
- `AndroidManifest.xml` does not set `screenOrientation`, `resizeableActivity`, or aspect-ratio constraints. Android 17 ignores those opt-outs on large screens (smallest width above 600dp) for apps targeting API 37. Layouts already follow `WindowSizeClass`, and instrumented tests cover compact, medium, tablet, foldable, and orientation changes.
- The only permission is `INTERNET`. Country data is pre-populated; the app does not open its own network connections.

CI runs instrumented tests on an API 34 emulator (`.github/workflows/ci.yml`). An API 37 system image was not booted for this check. With OpenJDK 17.0.20 and Android SDK Platform 37.0, `assembleDebug`, `testDebugUnitTest` (214 tests), `lintDebug`, `detekt`, `spotlessCheck`, and `bundleRelease` succeeded. `bundleRelease` wrote an unsigned `app-release.aab`; the project has no signing config.

### Material 3 Expressive beta

`androidx.compose.material3:material3` stays on **1.5.0-beta02**. That pin is intentional.

Compose BOM `2026.09.00` manages stable Material 3 **1.4.0**. In 1.4.0, `MaterialExpressiveTheme` is not public, and `LoadingIndicator` / `ContainedLoadingIndicator` do not exist. `CountryTrackerTheme`, `CountryListScreen`, and `CountryDetailScreen` call those APIs. Stable 1.5.0 is not published. The newest pre-release on 4 November 2026 is `1.5.0-rc01`. It was not adopted: the beta already resolves, and the release candidate's fixes are slider and toggle-button bugs this app does not use.

Shipping the beta has these limits:

- `LoadingIndicator` and `ContainedLoadingIndicator` still need `@OptIn(ExperimentalMaterial3ExpressiveApi)`. Those APIs can change before 1.5.0 stable.
- `MaterialExpressiveTheme` is public in 1.5.0-beta02, but the artifact is a pre-release. Dependabot updates dependencies that are already pre-release, so it can propose `1.5.0-rc01` or stable `1.5.0` later.
- The `androidx.compose.material3` atomic group moves `material3-window-size-class` to the same `1.5.0-beta02`. The catalog pins that artifact explicitly so the request matches resolution. Compose UI stays on the stable BOM (`ui` 1.12.1).
- Material 3 Adaptive is the stable `1.3.0` from BOM `2026.09.00`. An older catalog pin of `1.2.0` did not stick: Gradle conflict resolution selected `1.3.0`.

`CountryListScreenComposableTest` and `CountryDetailScreenDialogsTest.countryDetailScreen_showsLoadingIndicator_whenUnvisitedAndSaving` cover the loading indicators. Screen tests run under `CountryTrackerTheme`.

Replace `1.5.0-beta02` with stable `1.5.0` when that release exists. Returning to BOM-managed 1.4.0 requires removing the expressive calls.

### Detekt

Detekt **1.23.8** is the latest stable release and is built against Kotlin 2.0.21. Detekt 2.0 is still alpha (`2.0.0-alpha.6` tracks Kotlin 2.4). This project's `detekt` task passes with `maxIssues: 0`, so the alpha was not adopted. If 1.23.8 starts failing on Kotlin 2.4 metadata, move to a stable Detekt 2.0.

### Dependabot

`.github/dependabot.yml` updates Gradle and GitHub Actions every Monday, with at most five open pull requests per ecosystem. Labels and commit prefixes match the existing history (`deps` / `ci`). There is no ignore rule for the Material 3 pre-release, so a later stable 1.5.0 can arrive as a normal Dependabot pull request.

Dependabot does not edit these documents. `scripts/sync-docs-versions.py` regenerates the version tables. Pull requests from people run that script with `--check`. Dependabot pull requests skip the check because they only change the catalog. After those merges, `.github/workflows/docs-sync.yml` opens a follow-up pull request. It does not push to `master`: repository rules require a pull request. The 2 November 2026 run (`41546768522`) failed with `GH013` for that reason, which left the AGP row at 9.4.1 after the 9.5.0 merge.

### Project Structure

```
app/src/main/kotlin/com/tecruz/countrytracker/
├── CountryTrackerApplication.kt        # Koin Application entry point
├── MainActivity.kt                     # Single Activity entry
│
├── core/                               # Shared infrastructure
│   ├── data/
│   │   ├── database/
│   │   │   ├── CountryDao.kt          # Room Data Access Object
│   │   │   ├── CountryDatabase.kt     # Room Database configuration
│   │   │   └── CountryEntity.kt       # Database entity
│   │   ├── datasource/
│   │   │   ├── CountryDataLoader.kt   # Pre-populates database
│   │   │   └── WorldMapPathData.kt    # Loads SVG path data from assets
│   │   ├── mapper/
│   │   │   └── CountryMapper.kt       # Entity-to-domain mapping
│   │   └── repository/
│   │       ├── CountryRepositoryImpl.kt
│   │       └── MapRepositoryImpl.kt
│   ├── designsystem/
│   │   ├── Color.kt
│   │   ├── Theme.kt                   # MaterialExpressiveTheme
│   │   ├── Type.kt
│   │   ├── component/
│   │   │   ├── AdaptiveGrid.kt        # Responsive grid/list component
│   │   │   ├── AdaptiveScaffold.kt    # Responsive scaffold with nav rail
│   │   │   └── ResponsiveCard.kt      # Size-adaptive card component
│   │   └── preview/
│   │       ├── DevicePreviews.kt      # Multi-device preview annotations
│   │       ├── ExcludeFromGeneratedCoverageReport.kt # Coverage exclusion annotation
│   │       ├── PreviewData.kt         # Sample data & parameter providers
│   │       └── PreviewUtil.kt         # Theme + WindowSizeClass wrapper
│   ├── di/
│   │   ├── CoreDataModule.kt          # Database, DAO, repositories, dispatchers
│   │   └── CorePresentationModule.kt  # MainViewModel
│   ├── domain/
│   │   ├── model/
│   │   │   └── Country.kt             # Shared country model
│   │   ├── repository/
│   │   │   ├── CountryRepository.kt
│   │   │   └── MapRepository.kt
│   │   ├── DataError.kt               # Domain error hierarchy
│   │   ├── Result.kt                  # Functional result wrapper
│   │   └── ResultExtensions.kt        # Convenience functions for Result
│   ├── navigation/
│   │   ├── CountryTrackerNavHost.kt
│   │   └── Screen.kt                  # Type-safe navigation routes
│   ├── presentation/
│   │   ├── DataErrorExt.kt            # Error-to-UiText mapping
│   │   ├── MainViewModel.kt           # Splash / map initialization
│   │   └── UiText.kt                  # String resource abstraction
│   └── util/
│       ├── DispatcherProvider.kt      # Coroutine dispatcher abstraction
│       ├── SvgPathParser.kt           # SVG path parsing for map
│       └── WindowSizeClassExt.kt      # WindowSizeClass extension functions
│
├── features/
│   ├── countrylist/                    # Country List feature
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── CountryListItem.kt
│   │   │   │   └── CountryStatistics.kt   # Statistics data (visited/total/percentage)
│   │   │   ├── di/
│   │   │   │   └── CountryListDomainModule.kt
│   │   │   ├── GetAllCountriesUseCase.kt
│   │   │   ├── GetAllRegionsUseCase.kt
│   │   │   ├── GetCountryStatisticsUseCase.kt
│   │   │   └── GetVisitedCountryCodesUseCase.kt
│   │   └── presentation/
│   │       ├── components/
│   │       │   ├── worldmap/              # World map rendering package
│   │       │   │   ├── WorldMapCanvas.kt  # SVG-based world map composable
│   │       │   │   └── model/
│   │       │   │       ├── CountryPathData.kt      # Parsed path with bounds
│   │       │   │       ├── TransformedCountry.kt   # Scaled fill/shadow paths
│   │       │   │       └── WorldMapPathCache.kt    # Process-level SVG cache
│   │       │   ├── CountryListItem.kt
│   │       │   ├── FilterChips.kt
│   │       │   ├── SearchBar.kt
│   │       │   └── StatsCard.kt
│   │       ├── model/
│   │       │   └── TabItem.kt             # Tab item data class
│   │       ├── di/
│   │       │   └── CountryListPresentationModule.kt
│   │       ├── CountryListAction.kt
│   │       ├── CountryListEvent.kt
│   │       ├── CountryListScreen.kt
│   │       ├── CountryListState.kt
│   │       ├── CountryListUiState.kt
│   │       └── CountryListViewModel.kt
│   │
│   └── countrydetail/                  # Country Detail feature
│       ├── domain/
│       │   ├── di/
│       │   │   └── CountryDetailDomainModule.kt
│       │   ├── GetCountryByCodeUseCase.kt
│       │   ├── MarkCountryAsVisitedUseCase.kt
│       │   ├── MarkCountryAsUnvisitedUseCase.kt
│       │   ├── UpdateCountryNotesUseCase.kt
│       │   └── UpdateCountryRatingUseCase.kt
│       └── presentation/
│           ├── components/
│           │   ├── HeroCard.kt
│           │   ├── NotesCard.kt
│           │   ├── NotesDialog.kt
│           │   ├── RatingCard.kt
│           │   ├── UnvisitedConfirmationDialog.kt
│           │   └── VisitStatusCard.kt
│           ├── di/
│           │   └── CountryDetailPresentationModule.kt
│           ├── model/
│           │   └── CountryDetailUi.kt     # Screen model mapped from Country
│           ├── CountryDetailAction.kt
│           ├── CountryDetailEvent.kt
│           ├── CountryDetailScreen.kt
│           ├── CountryDetailState.kt
│           ├── CountryDetailUiState.kt
│           └── CountryDetailViewModel.kt
```

## Layer Responsibilities

### 1. Domain Layer
**Purpose**: Contains business logic and is independent of frameworks

**Components**:
- **Models**: Pure Kotlin data classes (`Country`, `CountryListItem`, `CountryStatistics`). Country detail renders `CountryDetailUi`, a presentation model mapped from `Country`
- **Repository Interfaces**: Contracts for data operations (`CountryRepository`, `MapRepository`)
- **Use Cases**: Single-responsibility business operations
  - Country List: `GetAllCountriesUseCase`, `GetAllRegionsUseCase`, `GetCountryStatisticsUseCase`, `GetVisitedCountryCodesUseCase`
  - Country Detail: `GetCountryByCodeUseCase`, `MarkCountryAsVisitedUseCase`, `MarkCountryAsUnvisitedUseCase`, `UpdateCountryNotesUseCase`, `UpdateCountryRatingUseCase`

**Benefits**:
- Framework independent
- Easily testable
- Reusable across platforms

### 2. Data Layer
**Purpose**: Manages data sources and implements repositories

**Components**:
- **Entities**: Room database entities (`CountryEntity`)
- **DAOs**: Database access objects (`CountryDao`)
- **Database**: Room database configuration (`CountryDatabase`)
- **Mappers**: Entity-to-model conversion (`CountryMapper`)
- **Repository Implementations**: Concrete implementations of domain repositories (`CountryRepositoryImpl`, `MapRepositoryImpl`), both in `core/data`

**Key Features**:
- Converts between Entity (data) and Model (domain)
- Handles data persistence with Room
- Pre-populates database with 75+ countries

### 3. Presentation Layer
**Purpose**: UI and user interaction

**Components**:
- **Screens**: Composable screen functions
- **ViewModels**: State management and UI logic
- **Navigation**: Centralized navigation graph
- **Theme**: Material 3 theming (colors, typography, shapes)

**Feature-Based Structure**:
Each feature (Country List, Country Detail) has its own:
- Screen composable with UI components
- ViewModel with state management
- UI state sealed classes for loading, success, and error states

**Android Best Practices**:
- **Edge-to-Edge Display**: Content renders behind system bars for immersive UI
- **Process Death Handling**: UI state preserved using `SavedStateHandle` and `rememberSaveable`
- **Adaptive Layouts**: `WindowSizeClass` provided via `CompositionLocal` for responsive design
- **Themed Status Bar**: Status bar color matches app theme for visual consistency

## Dependency Flow

```
Presentation → Domain ← Data
     ↓           ↓         ↓
  (Views)    (Use Cases) (Repos)
```

**Key Principle**: Dependencies point inward
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on nothing

## Dependency Injection with Koin

### Modules:
1. **CoreDataModule**: Provides the Room database, DAO, `DispatcherProvider`, `CountryRepository`, and `MapRepository`.
2. **CorePresentationModule**: Provides `MainViewModel`.
3. **CountryListDomainModule**: Provides list use cases.
4. **CountryListPresentationModule**: Provides the list ViewModel.
5. **CountryDetailDomainModule**: Provides detail use cases.
6. **CountryDetailPresentationModule**: Provides the detail ViewModel.

`CountryTrackerApplication` registers those six modules. Feature packages do not have their own data modules.

### Scopes:
- Singletons: Database, DAO, and repositories, declared in `CoreDataModule`
- ViewModels: Declared in the presentation modules and injected via `koinViewModel()` or `viewModel()`

## Design Patterns

### 1. Repository Pattern
- Abstracts data sources from business logic
- Interface in domain, implementation in data
- Easy to swap data sources (Room → API → Mock)

### 2. Use Case Pattern
- Single-responsibility operations
- Encapsulates business rules
- Named with verb phrases (Get, Mark, Update)

### 3. MVVM Pattern
- Model: Domain models + Use cases
- View: Composable screens
- ViewModel: State management + event handling

### 4. Unidirectional Data Flow (UDF)
```
User Action → ViewModel → Use Case → Repository → Database
                ↓
            UI State
                ↓
            Recompose
```

## Data Flow Example

### Marking a Country as Visited:

1. **User taps "Mark as Visited"** (Presentation Layer)
   ```kotlin
   CountryDetailScreen: onClick -> viewModel.markAsVisited(...)
   ```

2. **ViewModel processes event** (Presentation Layer)
   ```kotlin
   CountryDetailViewModel: calls use case
   ```

3. **Use case executes business logic** (Domain Layer)
   ```kotlin
   MarkCountryAsVisitedUseCase: validates and calls repository
   ```

4. **Repository updates data** (Data Layer)
   ```kotlin
   CountryRepositoryImpl: updates via DAO
   ```

5. **DAO persists to database** (Data Layer)
   ```kotlin
   CountryDao: Room saves to SQLite
   ```

6. **Flow emits new data** (Data Layer)
   ```kotlin
   Database change → Flow<Country> emits
   ```

7. **ViewModel collects and updates state** (Presentation Layer)
   ```kotlin
   StateFlow updates → UI recomposes
   ```

## Android Guidelines Implementation

### Edge-to-Edge Display
The app uses modern edge-to-edge rendering where content draws behind system bars:

```kotlin
// MainActivity.kt
enableEdgeToEdge(
    statusBarStyle = SystemBarStyle.dark(PRIMARY_GREEN_ARGB),
    navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
)
```

Screens handle `WindowInsets` to ensure content doesn't overlap with system bars:
- `WindowInsets.statusBars` - Applied to top app bars
- `WindowInsets.navigationBars` - Applied to scrollable content

### Process Death Handling
State is preserved across process death using:

1. **SavedStateHandle in ViewModels**:
   ```kotlin
   private val _searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
   ```

2. **rememberSaveable in Composables**:
   ```kotlin
   var showDatePicker by rememberSaveable { mutableStateOf(false) }
   ```

### Adaptive Layouts
`WindowSizeClass` is provided via `CompositionLocal` for responsive design across three breakpoints:
- **Compact** (0-599dp): Phone - single column, bottom navigation, 48dp touch targets
- **Medium** (600-839dp): Tablet portrait / foldable - two-column grid, navigation rail, 52dp touch targets
- **Expanded** (840dp+): Tablet landscape / desktop - three-column grid, navigation rail, 56dp touch targets

```kotlin
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> { ... }

// In screens
val windowSizeClass = LocalWindowSizeClass.current
when (windowSizeClass.windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> // Phone layout
    WindowWidthSizeClass.MEDIUM -> // Tablet portrait
    WindowWidthSizeClass.EXPANDED -> // Tablet landscape / Desktop
}
```

#### Reusable Adaptive Components
- `AdaptiveScaffold` - Responsive scaffold with optional navigation rail for medium/expanded
- `ResponsiveCard` - Card with adaptive padding, corner radius, and elevation
- `AdaptiveGrid` - Switches between LazyColumn (compact) and LazyVerticalGrid (medium/expanded)
- `WindowSizeClassExt` - Extension functions for responsive padding, spacing, grid columns, and touch targets

#### Foldable Support
State is preserved across fold/unfold transitions via:
- `SavedStateHandle` in ViewModels for business state
- `rememberSaveable` in Composables for UI state (dialog visibility, tab selection)
- Keyed `LazyColumn`/`LazyVerticalGrid` items for stable recomposition

### Compose Previews

All public UI components include `@Preview` annotations rendered at the three supported screen sizes (Phone 360dp, Foldable 700dp, Tablet 1100dp).

#### Preview Infrastructure (`core/designsystem/preview/`)
- **`@DevicePreviews`** - Multi-preview annotation generating Phone, Foldable, and Tablet previews
- **`@DeviceThemePreviews`** - Generates all device sizes in both light and dark themes
- **`PreviewWrapper`** - Composable that provides `CountryTrackerTheme` and `LocalWindowSizeClass` computed from preview dimensions
- **`PreviewData`** - Sample `CountryListItem` and `CountryDetailUi` instances plus `PreviewParameterProvider` implementations

#### Previewed Components
| Screen / Package | Components |
|------------------|------------|
| Design System | `ResponsiveCard`, `AdaptiveScaffold`, `AdaptiveGrid` |
| Country List | `StatsCard`, `StatItem`, `SearchBar`, `FilterChips`, `CountryListItem` (visited/unvisited/long name) |
| Country Detail | `HeroCard`, `VisitStatusCard`, `RatingCard`, `NotesCard` (with content/empty) |
| World Map | `WorldMapCanvas` |

## Benefits of This Architecture

### 1. Separation of Concerns
- Each layer has clear responsibility
- Changes in one layer don't affect others
- Easy to understand and maintain

### 2. Testability
- Domain layer is pure Kotlin (easy unit tests)
- Can mock repositories for ViewModel tests
- Can test use cases independently

### 3. Scalability
- Easy to add new features (create new feature folder)
- Easy to add new data sources (implement interface)
- Easy to add new business rules (new use cases)

### 4. Maintainability
- Feature-based structure makes navigation easy
- Clear boundaries between layers
- Consistent patterns throughout

### 5. Flexibility
- Can replace Room with API easily
- Can add caching layer without changing domain
- Can create multiplatform shared domain layer

## Testing Strategy

### Test Structure

```
app/src/test/kotlin/com/tecruz/countrytracker/     # Unit tests (29 files)
├── core/
│   ├── data/repository/
│   │   └── CountryRepositoryImplTest.kt
│   ├── designsystem/component/
│   │   ├── AdaptiveGridTest.kt
│   │   ├── AdaptiveScaffoldTest.kt
│   │   └── ResponsiveCardTest.kt
│   ├── domain/
│   │   ├── DataErrorTest.kt
│   │   └── ResultExtensionsTest.kt
│   ├── navigation/
│   │   └── ScreenTest.kt
│   ├── presentation/
│   │   ├── DataErrorExtTest.kt
│   │   ├── MainViewModelTest.kt
│   │   └── UiTextTest.kt
│   └── util/
│       ├── DispatcherProviderTest.kt
│       └── WindowSizeClassExtTest.kt
├── features/
│   ├── countrylist/
│   │   ├── domain/
│   │   │   ├── GetAllCountriesUseCaseTest.kt
│   │   │   ├── GetCountryStatisticsUseCaseTest.kt
│   │   │   └── model/
│   │   │       ├── CountryListItemTest.kt
│   │   │       └── CountryStatisticsTest.kt
│   │   └── presentation/
│   │       ├── CountryListScreenComposableTest.kt  # Composable UI tests (Robolectric)
│   │       ├── CountryListRowResponsiveTest.kt
│   │       ├── CountryListUiStateTest.kt
│   │       └── CountryListViewModelTest.kt
│   └── countrydetail/
│       ├── domain/
│       │   ├── GetCountryByCodeUseCaseTest.kt
│       │   ├── MarkCountryAsVisitedUseCaseTest.kt
│       │   ├── MarkCountryAsUnvisitedUseCaseTest.kt
│       │   ├── UpdateCountryNotesUseCaseTest.kt
│       │   └── UpdateCountryRatingUseCaseTest.kt
│       └── presentation/
│           ├── CountryDetailUiStateTest.kt
│           └── CountryDetailViewModelTest.kt
└── test/
    ├── KoinResetRule.kt
    └── KoinTestHelper.kt

app/src/androidTest/kotlin/com/tecruz/countrytracker/ # Instrumented tests (25 files)
├── core/
│   ├── data/
│   │   ├── database/
│   │   │   └── CountryDaoTest.kt
│   │   └── datasource/
│   │       └── CountryDataLoaderInstrumentedTest.kt
│   └── designsystem/
│       ├── ThemeInstrumentedTest.kt
│       └── component/
│           ├── AdaptiveGridInstrumentedTest.kt
│           ├── AdaptiveScaffoldInstrumentedTest.kt
│           └── ResponsiveCardInstrumentedTest.kt
├── features/
│   ├── countrylist/
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   └── WorldMapPathDataTest.kt
│   │   │   └── repository/
│   │   │       └── CountryListRepositoryIntegrationTest.kt
│   │   └── presentation/
│   │       ├── CountryListScreenCompactTest.kt
│   │       ├── CountryListScreenMediumTest.kt
│   │       ├── CountryListScreenStatesInstrumentedTest.kt
│   │       ├── WorldMapCanvasCompactTest.kt
│   │       └── WorldMapCanvasTabletTest.kt
│   └── countrydetail/
│       ├── data/repository/
│       │   └── CountryDetailRepositoryIntegrationTest.kt
│       └── presentation/
│           ├── components/
│           │   └── HeroCardInstrumentedTest.kt
│           ├── CountryDetailScreenCompactTest.kt
│           ├── CountryDetailScreenDialogsTest.kt  # Includes the expressive loading indicator
│           └── CountryDetailScreenTabletTest.kt
├── AdaptiveLayoutE2ETest.kt
├── CountryDetailScreenTest.kt
├── CountryListScreenTest.kt
├── CountryTrackerE2ETest.kt
├── FoldableTransitionTest.kt
├── OrientationChangeTest.kt
└── WorldMapColoringTest.kt
```

Instrumented tests use `androidx.test.runner.AndroidJUnitRunner`.

### Testing Libraries

<!-- versions:testing-libraries:start -->
| Library | Purpose |
|---------|---------|
| JUnit 4.13.2 | Unit test framework |
| MockK 1.14.11 | Mocking library |
| Turbine 1.2.1 | Flow testing |
| Coroutines Test 1.11.0 | Coroutine testing |
| Arch Core Testing 2.2.0 | Architecture component testing |
| Robolectric 4.17 | JVM-based Android framework simulation |
| Compose UI Test (BOM 2026.09.00) | Compose UI testing |
| Room Testing 2.8.5 | Room in-memory database testing |
<!-- versions:testing-libraries:end -->

### Running Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires connected device or emulator)
./gradlew connectedDebugAndroidTest

# Unit test coverage (Kover - Compose-aware)
./gradlew koverHtmlReport

# Instrumented test coverage (JaCoCo)
./gradlew jacocoAndroidTestReport

# Combined coverage report (JaCoCo - unit + instrumented, requires device)
./gradlew jacocoCombinedReport
```

### Code Coverage

The project uses a hybrid coverage setup:

<!-- versions:coverage-tools:start -->
- **Kover 0.9.11** for unit test coverage (Compose-aware instrumentation, accurate for `@Composable` functions)
- **JaCoCo 0.8.14** for instrumented test coverage and combined reports
<!-- versions:coverage-tools:end -->

| Task | Engine | Scope |
|------|--------|-------|
| `koverHtmlReport` / `koverXmlReport` | Kover | Unit tests (Compose-aware) |
| `jacocoAndroidTestReport` | JaCoCo | Instrumented tests only |
| `jacocoCombinedReport` | JaCoCo | Unit + instrumented (merged `.exec` + `.ec`) |

Both Kover and JaCoCo reports are uploaded to Codecov. Codecov merges them and picks the highest coverage per line, giving accurate numbers for Compose code (from Kover) alongside instrumented test coverage (from JaCoCo).

### CI Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) runs parallel jobs after the initial build:

| Job | Description |
|-----|-------------|
| **Build & Unit Tests** | Compiles debug APK, runs unit tests, generates Kover coverage report, uploads to Codecov |
| **Code Coverage** | Boots an API 34 emulator, runs `jacocoCombinedReport` (unit + instrumented via JaCoCo), uploads to Codecov |
| **Android Lint** | Static analysis via `lintDebug` |
| **Detekt Analysis** | Kotlin static analysis |
| **Code Formatting** | Checks formatting via Spotless |
| **Documentation Versions** | On non-Dependabot pull requests, checks that README and this file match the version catalog |

The **Build Release APK** job runs only on pushes to `main`/`master` after build, lint, detekt, and spotless pass.

## Naming Conventions

### Packages:
- Lowercase, no underscores
- Feature-based in presentation layer

### Classes:
- **Entities**: `CountryEntity`
- **Models**: `Country`
- **Use Cases**: `VerbNounUseCase` (e.g., `GetAllCountriesUseCase`)
- **ViewModels**: `FeatureViewModel` (e.g., `CountryListViewModel`)
- **Screens**: `FeatureScreen` (e.g., `CountryListScreen`)

### Files:
- One class per file
- File name matches class name

## Key Technologies

<!-- versions:key-technologies:start -->
| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.4.21 | Primary language |
| Jetpack Compose (BOM) | 2026.09.00 | Declarative UI framework |
| Material 3 Expressive | 1.5.0-beta02 | Design system |
| Material 3 Adaptive | 1.3.0 | Responsive layouts for all screen sizes |
| Room | 2.8.5 | Local SQLite database |
| Koin | 4.2.2 | Dependency injection |
| Coroutines | 1.11.0 | Asynchronous operations |
| Flow | (with Coroutines) | Reactive data streams |
| Navigation Compose | 2.10.2 | Screen navigation |
| DataStore | 1.3.0 | Preferences storage |
| Splash Screen | 1.2.0 | Native splash screen API |
| SavedStateHandle | (with Lifecycle 2.11.0) | Process death survival |
<!-- versions:key-technologies:end -->

## Future Enhancements

### Potential Additions:
1. **Remote Data Source**
   - Add API module in data layer
   - Implement RemoteDataSource
   - Update repository to handle both local and remote

2. **Caching Strategy**
   - Add cache layer between repository and data sources
   - Implement cache invalidation logic

3. **Offline-First**
   - Repository checks local first
   - Syncs with remote when online
   - Handles conflicts

4. **Multiplatform**
   - Extract domain layer to shared module
   - Platform-specific data and presentation layers

5. **Analytics**
   - Add analytics use cases in domain
   - Inject analytics service in data layer

6. **Testing**
   - Expand UI test coverage with Compose Testing
   - Add screenshot testing

## Code Style Guidelines

- Use explicit types for public APIs
- Prefer immutability (val over var, data classes)
- Use meaningful names (no abbreviations)
- Keep functions small and focused
- Document complex logic with comments
- Follow Kotlin coding conventions

---

This architecture provides a solid foundation for a scalable, maintainable, and testable Android application following industry best practices.
