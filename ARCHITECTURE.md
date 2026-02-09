# Country Tracker - Clean Architecture Implementation

## Architecture Overview

This app follows **Clean Architecture** principles with a **feature-based package structure** and **domain-driven design**.

### Technology Versions

| Technology | Version |
|------------|---------|
| Kotlin | 2.3.0 |
| Jetpack Compose BOM | 2026.01.00 |
| Material 3 Adaptive | 1.1.0 |
| Room | 2.8.4 |
| Hilt | 2.59 |
| Navigation Compose | 2.9.6 |
| Coroutines | 1.10.2 |
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
│   │   └── component/
│   │       ├── AdaptiveGrid.kt        # Responsive grid/list component
│   │       ├── AdaptiveScaffold.kt    # Responsive scaffold with nav rail
│   │       └── ResponsiveCard.kt      # Size-adaptive card component
│   ├── di/
│   │   └── DatabaseModule.kt          # Database + DAO providers
│   ├── navigation/
│   │   ├── CountryTrackerNavHost.kt
│   │   └── Screen.kt                  # Type-safe navigation routes
│   └── util/
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
│   │   │   │   └── CountryListItem.kt
│   │   │   ├── repository/
│   │   │   │   └── CountryListRepository.kt
│   │   │   ├── GetAllCountriesUseCase.kt
│   │   │   └── GetCountryStatisticsUseCase.kt
│   │   └── presentation/
│   │       ├── components/
│   │       │   └── WorldMapCanvas.kt
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
- **Models**: Pure Kotlin data classes (`CountryListItem`, `CountryDetail`)
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
app/src/test/kotlin/com/tecruz/countrytracker/     # Unit tests
├── features/
│   ├── countrylist/
│   │   ├── data/repository/
│   │   │   └── CountryListRepositoryImplTest.kt
│   │   ├── domain/
│   │   │   ├── GetAllCountriesUseCaseTest.kt
│   │   │   └── GetCountryStatisticsUseCaseTest.kt
│   │   └── presentation/
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

app/src/androidTest/kotlin/com/tecruz/countrytracker/ # Instrumented tests
├── core/data/database/
│   └── CountryDaoTest.kt                      # DAO integration tests
├── features/
│   ├── countrylist/data/repository/
│   │   └── CountryListRepositoryIntegrationTest.kt
│   └── countrydetail/data/repository/
│       └── CountryDetailRepositoryIntegrationTest.kt
├── CountryListScreenTest.kt                   # UI tests
├── CountryDetailScreenTest.kt
├── CountryTrackerE2ETest.kt                   # End-to-end tests
├── WorldMapColoringTest.kt
└── HiltTestRunner.kt                          # Custom test runner
```

### Testing Libraries

| Library | Purpose |
|---------|---------|
| JUnit 4.13.2 | Unit test framework |
| MockK 1.14.7 | Mocking library |
| Turbine 1.2.1 | Flow testing |
| Coroutines Test 1.10.2 | Coroutine testing |
| Arch Core Testing 2.2.0 | Architecture component testing |
| Compose UI Test | Compose UI testing |
| Room Testing | Room in-memory database testing |

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Combined coverage report (unit + instrumented, requires device)
./gradlew jacocoCombinedReport
```

### Code Coverage

Coverage is collected with JaCoCo 0.8.12 and reported via three Gradle tasks:

| Task | Scope | Execution data |
|------|-------|----------------|
| `jacocoTestReport` | Unit tests only | `.exec` |
| `jacocoAndroidTestReport` | Instrumented tests only | `.ec` |
| `jacocoCombinedReport` | Both | `.exec` + `.ec` |

`jacocoCoverageVerification` enforces a minimum of 80% overall line coverage and 40% per-class.

### CI Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) runs five parallel jobs after the initial build:

| Job | Description |
|-----|-------------|
| **Build & Unit Tests** | Compiles debug APK and runs unit tests |
| **Code Coverage** | Boots an API 30 emulator, runs `jacocoCombinedReport` (unit + instrumented), uploads to Codecov |
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
| Kotlin | 2.3.0 | Primary language |
| Jetpack Compose | 2026.01.00 | Declarative UI framework |
| Material 3 Adaptive | 1.1.0 | Responsive layouts for all screen sizes |
| Room | 2.8.4 | Local SQLite database |
| Hilt | 2.59 | Dependency injection |
| Coroutines | 1.10.2 | Asynchronous operations |
| Flow | (with Coroutines) | Reactive data streams |
| Material 3 | Latest | Design system |
| Navigation Compose | 2.9.6 | Screen navigation |
| DataStore | 1.2.0 | Preferences storage |
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
