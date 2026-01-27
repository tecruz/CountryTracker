# Country Tracker - Clean Architecture Implementation

## Architecture Overview

This app follows **Clean Architecture** principles with a **feature-based package structure** and **domain-driven design**.

### Technology Versions

| Technology | Version |
|------------|---------|
| Kotlin | 2.3.0 |
| Jetpack Compose BOM | 2026.01.00 |
| Room | 2.8.4 |
| Hilt | 2.59 |
| Navigation Compose | 2.9.6 |
| Coroutines | 1.10.2 |
| Target SDK | 36 |
| Min SDK | 24 |

### Project Structure

```
app/src/main/kotlin/com/example/countrytracker/
├── CountryTrackerApplication.kt    # Hilt Application entry point
│
├── data/                           # Data Layer
│   ├── local/
│   │   ├── dao/
│   │   │   └── CountryDao.kt      # Room Data Access Object
│   │   ├── database/
│   │   │   └── CountryDatabase.kt # Room Database configuration
│   │   └── entity/
│   │       └── CountryEntity.kt   # Database entity + converters
│   └── repository/
│       └── CountryRepositoryImpl.kt
│
├── domain/                         # Domain Layer (Business Logic)
│   ├── model/
│   │   └── Country.kt             # Domain model
│   ├── repository/
│   │   └── CountryRepository.kt   # Repository interface
│   └── usecase/
│       ├── GetAllCountriesUseCase.kt
│       ├── GetCountryStatisticsUseCase.kt
│       └── MarkCountryAsVisitedUseCase.kt
│
├── presentation/                   # Presentation Layer (UI)
│   ├── MainActivity.kt            # Single Activity entry
│   ├── navigation/
│   │   └── CountryTrackerNavHost.kt
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
│
└── di/                            # Dependency Injection
    ├── DatabaseModule.kt          # Database + DAO providers
    └── RepositoryModule.kt        # Repository bindings
```

## Layer Responsibilities

### 1. Domain Layer
**Purpose**: Contains business logic and is independent of frameworks

**Components**:
- **Models**: Pure Kotlin data classes (Country)
- **Repository Interfaces**: Contracts for data operations
- **Use Cases**: Single-responsibility business operations
  - `GetAllCountriesUseCase`
  - `MarkCountryAsVisitedUseCase`
  - `GetCountryStatisticsUseCase`

**Benefits**:
- Framework independent
- Easily testable
- Reusable across platforms

### 2. Data Layer
**Purpose**: Manages data sources and implements repositories

**Components**:
- **Entities**: Room database entities
- **DAOs**: Database access objects
- **Database**: Room database configuration
- **Repository Implementations**: Concrete implementations of domain repositories

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
1. **DatabaseModule**: Provides Room database and DAO
2. **RepositoryModule**: Binds repository interfaces to implementations

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
app/src/test/kotlin/com/example/countrytracker/
├── data/repository/
│   └── CountryRepositoryImplTest.kt    # Repository tests
├── domain/usecase/
│   ├── GetAllCountriesUseCaseTest.kt   # Use case tests
│   └── GetCountryStatisticsUseCaseTest.kt
└── presentation/
    ├── countrylist/
    │   └── CountryListViewModelTest.kt # ViewModel tests
    └── countrydetail/
        └── CountryDetailViewModelTest.kt
```

### Testing Libraries

| Library | Purpose |
|---------|---------|
| JUnit 4.13.2 | Unit test framework |
| MockK 1.14.7 | Mocking library |
| Turbine 1.2.1 | Flow testing |
| Coroutines Test 1.10.2 | Coroutine testing |
| Arch Core Testing 2.2.0 | Architecture component testing |

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

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
| Room | 2.8.4 | Local SQLite database |
| Hilt | 2.59 | Dependency injection |
| Coroutines | 1.10.2 | Asynchronous operations |
| Flow | (with Coroutines) | Reactive data streams |
| Material 3 | Latest | Design system |
| Navigation Compose | 2.9.6 | Screen navigation |
| DataStore | 1.2.0 | Preferences storage |

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
   - Add comprehensive unit tests
   - Add instrumented tests
   - Add UI tests with Compose Testing

## Code Style Guidelines

- Use explicit types for public APIs
- Prefer immutability (val over var, data classes)
- Use meaningful names (no abbreviations)
- Keep functions small and focused
- Document complex logic with comments
- Follow Kotlin coding conventions

---

This architecture provides a solid foundation for a scalable, maintainable, and testable Android application following industry best practices.
