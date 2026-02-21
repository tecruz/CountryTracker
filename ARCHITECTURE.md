# Country Tracker - Clean Architecture Implementation

## Architecture Overview

This app follows **Clean Architecture** principles with a **feature-based package structure** and **domain-driven design**.

### Technology Versions

| Technology | Version |
|------------|---------|
| Kotlin | 2.3.10 |
| Jetpack Compose BOM | 2026.02.00 |
| Material 3 Expressive | 1.5.0-alpha14 |
| Material 3 Adaptive | 1.2.0 |
| Room | 2.8.4 |
| Hilt | 2.59.1 |
| Navigation Compose | 2.9.7 |
| Coroutines | 1.10.2 |
| AGP | 9.0.1 |
| KSP | 2.3.5 |
| Target SDK | 36 |
| Min SDK | 24 |

### Project Structure

```
app/src/main/kotlin/com/tecruz/countrytracker/
├── CountryTrackerApplication.kt        # Hilt Application entry point
├── MainActivity.kt                     # Single Activity entry
│
├── core/                               # Shared infrastructure
│   ├── data/
│   │   ├── database/
│   │   │   ├── CountryDao.kt          # Room Data Access Object
│   │   │   ├── CountryDatabase.kt     # Room Database configuration
│   │   │   └── CountryEntity.kt       # Database entity
│   │   └── datasource/
│   │       └── CountryDataLoader.kt   # Pre-populates database
│   ├── designsystem/
│   │   ├── Color.kt
│   │   ├── Theme.kt
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
│   │   └── DatabaseModule.kt          # Database + DAO providers
│   ├── navigation/
│   │   ├── CountryTrackerNavHost.kt
│   │   └── Screen.kt                  # Type-safe navigation routes
│   └── util/
│       ├── DispatcherProvider.kt      # Coroutine dispatcher abstraction
│       ├── SvgPathParser.kt           # SVG path parsing for map
│       └── WindowSizeClassExt.kt      # WindowSizeClass extension functions
│
├── features/
│   ├── countrylist/                    # Country List feature
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   └── WorldMapPathData.kt
│   │   │   ├── di/
│   │   │   │   └── CountryListDataModule.kt
│   │   │   ├── mapper/
│   │   │   │   └── CountryListMapper.kt
│   │   │   └── repository/
│   │   │       └── CountryListRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── CountryListItem.kt
│   │   │   │   └── CountryStatistics.kt   # Statistics data (visited/total/percentage)
│   │   │   ├── repository/
│   │   │   │   └── CountryListRepository.kt
│   │   │   ├── GetAllCountriesUseCase.kt
│   │   │   └── GetCountryStatisticsUseCase.kt
│   │   └── presentation/
│   │       ├── components/
│   │       │   └── worldmap/              # World map rendering package
│   │       │       ├── WorldMapCanvas.kt  # SVG-based world map composable
│   │       │       └── model/
│   │       │           ├── CountryPathData.kt      # Parsed path with bounds
│   │       │           ├── TransformedCountry.kt   # Scaled fill/shadow paths
│   │       │           └── WorldMapPathCache.kt    # Process-level SVG cache
│   │       ├── model/
│   │       │   └── TabItem.kt             # Tab item data class
│   │       ├── CountryListScreen.kt
│   │       └── CountryListViewModel.kt
│   │
│   └── countrydetail/                  # Country Detail feature
│       ├── data/
│       │   ├── di/
│       │   │   └── CountryDetailDataModule.kt
│       │   ├── mapper/
│       │   │   └── CountryDetailMapper.kt
│       │   └── repository/
│       │       └── CountryDetailRepositoryImpl.kt
│       ├── domain/
│       │   ├── model/
│       │   │   └── CountryDetail.kt
│       │   ├── repository/
│       │   │   └── CountryDetailRepository.kt
│       │   ├── GetCountryByCodeUseCase.kt
│       │   ├── MarkCountryAsVisitedUseCase.kt
│       │   ├── MarkCountryAsUnvisitedUseCase.kt
│       │   ├── UpdateCountryNotesUseCase.kt
│       │   └── UpdateCountryRatingUseCase.kt
│       └── presentation/
│           ├── model/
│           │   └── CountryDetailUi.kt
│           ├── CountryDetailScreen.kt
│           └── CountryDetailViewModel.kt
```

## Layer Responsibilities

### 1. Domain Layer
**Purpose**: Contains business logic and is independent of frameworks

**Components**:
- **Models**: Pure Kotlin data classes (`CountryListItem`, `CountryStatistics`, `CountryDetail`)
- **Repository Interfaces**: Contracts for data operations (`CountryListRepository`, `CountryDetailRepository`)
- **Use Cases**: Single-responsibility business operations
  - Country List: `GetAllCountriesUseCase`, `GetCountryStatisticsUseCase`
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
- **Mappers**: Entity-to-model conversion (`CountryListMapper`, `CountryDetailMapper`)
- **Repository Implementations**: Concrete implementations of domain repositories (`CountryListRepositoryImpl`, `CountryDetailRepositoryImpl`)

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

## Dependency Injection with Hilt

### Modules:
1. **DatabaseModule** (core): Provides Room database and DAO
2. **CountryListDataModule**: Binds `CountryListRepository` to `CountryListRepositoryImpl`
3. **CountryDetailDataModule**: Binds `CountryDetailRepository` to `CountryDetailRepositoryImpl`

### Scopes:
- `@Singleton`: Database, DAOs, Repositories
- `@ViewModelScoped`: ViewModels (via Hilt injection)

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
    statusBarStyle = SystemBarStyle.dark(PRIMARY_GREEN),
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
app/src/test/kotlin/com/tecruz/countrytracker/     # Unit tests (17 files)
├── core/
│   ├── designsystem/component/
│   │   ├── AdaptiveGridTest.kt                # Adaptive grid component tests
│   │   ├── AdaptiveScaffoldTest.kt            # Adaptive scaffold component tests
│   │   └── ResponsiveCardTest.kt              # Responsive card component tests
│   └── util/
│       └── WindowSizeClassExtTest.kt          # Window size class extension tests
├── features/
│   ├── countrylist/
│   │   ├── data/repository/
│   │   │   └── CountryListRepositoryImplTest.kt
│   │   ├── domain/
│   │   │   ├── GetAllCountriesUseCaseTest.kt
│   │   │   └── GetCountryStatisticsUseCaseTest.kt
│   │   └── presentation/
│   │       ├── CountryListScreenComposableTest.kt  # Composable UI tests (Robolectric)
│   │       ├── CountryListRowResponsiveTest.kt     # Responsive row layout tests
│   │       └── CountryListViewModelTest.kt
│   └── countrydetail/
│       ├── data/repository/
│       │   └── CountryDetailRepositoryImplTest.kt
│       ├── domain/
│       │   ├── GetCountryByCodeUseCaseTest.kt
│       │   ├── MarkCountryAsVisitedUseCaseTest.kt
│       │   ├── MarkCountryAsUnvisitedUseCaseTest.kt
│       │   ├── UpdateCountryNotesUseCaseTest.kt
│       │   └── UpdateCountryRatingUseCaseTest.kt
│       └── presentation/
│           └── CountryDetailViewModelTest.kt

app/src/androidTest/kotlin/com/tecruz/countrytracker/ # Instrumented tests (19 files)
├── core/data/database/
│   └── CountryDaoTest.kt                      # DAO integration tests
├── features/
│   ├── countrylist/
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   └── WorldMapPathDataTest.kt    # SVG path data loading tests
│   │   │   └── repository/
│   │   │       └── CountryListRepositoryIntegrationTest.kt
│   │   └── presentation/
│   │       ├── CountryListScreenCompactTest.kt    # Compact layout UI tests
│   │       ├── CountryListScreenMediumTest.kt     # Medium layout UI tests
│   │       ├── WorldMapCanvasCompactTest.kt       # World map compact tests
│   │       └── WorldMapCanvasTabletTest.kt        # World map tablet tests
│   └── countrydetail/
│       ├── data/repository/
│       │   └── CountryDetailRepositoryIntegrationTest.kt
│       └── presentation/
│           ├── CountryDetailScreenCompactTest.kt  # Detail screen compact tests
│           ├── CountryDetailScreenDialogsTest.kt  # Dialog interaction tests
│           └── CountryDetailScreenTabletTest.kt   # Detail screen tablet tests
├── CountryListScreenTest.kt                   # UI tests
├── CountryDetailScreenTest.kt
├── CountryTrackerE2ETest.kt                   # End-to-end tests
├── AdaptiveLayoutE2ETest.kt                   # Adaptive layout E2E tests
├── OrientationChangeTest.kt                   # Orientation change tests
├── FoldableTransitionTest.kt                  # Foldable transition tests
├── WorldMapColoringTest.kt
└── HiltTestRunner.kt                          # Custom test runner
```

### Testing Libraries

| Library | Purpose |
|---------|---------|
| JUnit 4.13.2 | Unit test framework |
| MockK 1.14.9 | Mocking library |
| Turbine 1.2.1 | Flow testing |
| Coroutines Test 1.10.2 | Coroutine testing |
| Arch Core Testing 2.2.0 | Architecture component testing |
| Robolectric 4.16.1 | JVM-based Android framework simulation |
| Compose UI Test | Compose UI testing |
| Room Testing | Room in-memory database testing |

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

- **Kover 0.9.7** for unit test coverage (Compose-aware instrumentation, accurate for `@Composable` functions)
- **JaCoCo 0.8.12** for instrumented test coverage and combined reports

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
| **Code Coverage** | Boots an API 30 emulator, runs `jacocoCombinedReport` (unit + instrumented via JaCoCo), uploads to Codecov |
| **Android Lint** | Static analysis via `lintDebug` |
| **Detekt Analysis** | Kotlin static analysis |
| **Code Formatting** | Checks formatting via Spotless |

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

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.3.10 | Primary language |
| Jetpack Compose (BOM) | 2026.02.00 | Declarative UI framework |
| Material 3 Expressive | 1.5.0-alpha14 | Design system |
| Material 3 Adaptive | 1.2.0 | Responsive layouts for all screen sizes |
| Room | 2.8.4 | Local SQLite database |
| Hilt | 2.59.1 | Dependency injection |
| Coroutines | 1.10.2 | Asynchronous operations |
| Flow | (with Coroutines) | Reactive data streams |
| Navigation Compose | 2.9.7 | Screen navigation |
| DataStore | 1.2.0 | Preferences storage |
| Splash Screen | 1.0.1 | Native splash screen API |
| SavedStateHandle | (with Lifecycle) | Process death survival |

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
