# Use Case Implementation Guide

This guide walks through how to implement a new use case in the AMTS backend. The project follows Clean Architecture, which means code is organized in concentric layers where inner layers know nothing about outer layers. Before writing any code, it helps to understand where each piece lives and why.

---

## Architecture Overview

The backend is divided into four layers, from innermost to outermost:

**Domain** (`org.amts.domain`) — Pure business logic. No framework dependencies. Contains entities (e.g. `User`, `Booking`, `Show`) and value objects (e.g. `Money`). Nothing in here imports Spring, jOOQ, or anything external.

**Application** (`org.amts.application`) — Defines *what* the system can do. Contains use case interfaces and exception types. Still framework-free. This layer speaks only in domain types.

**Adapters** (`org.amts.adapters`) — Implements the application interfaces. This is where jOOQ queries live (`UserPersistenceImpl`) and where business logic implementations live (`UserRoleManagementImpl`). Also contains HTTP controllers and DTOs.

**Infrastructure** (`org.amts.infrastructure`) — Spring configuration. Wires everything together via `@Configuration` classes. This is the only place where `@Bean` definitions appear.

The dependency rule: arrows always point inward. Adapters depend on Application. Application depends on Domain. Domain depends on nothing.

---

## The Anatomy of an Existing Use Case

Before implementing something new, trace how the existing user role management use case works end-to-end. This pattern repeats for every feature.

### 1. Domain Entity

`User.java` holds the state and invariants. It exposes helpers like `hasRole()` and enforces rules in its constructor (e.g. a user must have at least one role). The domain entity should be the first thing you write — it defines what is true about the concept.

### 2. Application Interface

`UserPersistenceUseCase.java` and `UserRoleManagementUseCase.java` declare the operations the system supports. These are plain Java interfaces with no implementation. They use only domain types in their signatures.

```java
public interface UserRoleManagementUseCase {
    void addRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles);
    void removeRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles);
}
```

### 3. Application Facade

`UserUseCases.java` is a thin facade that composes multiple use case interfaces into a single object. Controllers talk to this class rather than to individual use cases directly. This keeps controllers simple and gives you one place to see everything a feature area can do.

```java
public class UserUseCases {
    private final UserPersistenceUseCase persistence;
    private final UserRoleManagementUseCase assignUserRoles;
    // delegates to both...
}
```

### 4. Adapter Implementations

`UserRoleManagementImpl.java` contains the business logic for role assignment — permission checks, invariant enforcement, orchestration. It calls into `UserPersistenceUseCase` to load and save data.

`UserPersistenceImpl.java` contains the jOOQ queries. It maps database records back to domain entities and handles the translation between `Roleenum` (jOOQ-generated) and `Role` (domain).

### 5. HTTP Layer

`RolesController.java` and `UserController.java` define the REST endpoints. They accept request records, call the `UserUseCases` facade, and return DTOs. They do not contain business logic.

`UserDto.java` is the outbound DTO. It converts a domain `User` into a JSON-serializable shape via a static `from()` factory method.

`GlobalExceptionHandler.java` maps domain exceptions to HTTP status codes. When you create a new exception type in the application layer, register it here.

### 6. Infrastructure Wiring

`UserConfiguration.java` creates all the Spring beans and wires the dependency graph:

```java
@Bean
public UserPersistenceUseCase userPersistence(DSLContext dslContext) {
    return new UserPersistenceImpl(dslContext);
}

@Bean
public UserRoleManagementUseCase assignUserRolesUseCase(UserPersistenceUseCase userPersistence) {
    return new UserRoleManagementImpl(userPersistence);
}

@Bean
public UserUseCases userUseCases(...) {
    return new UserUseCases(persistence, assignUserRoles);
}
```

Notice that the implementation classes (`UserPersistenceImpl`, `UserRoleManagementImpl`) are never annotated with `@Component` or `@Service`. All Spring wiring is explicit, centralized in the configuration class, and the implementations themselves remain framework-free.

---

## Step-by-Step: Implementing a New Use Case

The following steps use a hypothetical **"Create Show"** use case as a running example.

### Step 1 — Start in the Domain

Ask: what are the rules and invariants for this concept?

If you're working with an existing entity (e.g. `Show`), confirm it already captures everything you need. If you're adding behavior or a new entity, add it here first. Keep domain classes immutable where possible and validate eagerly in constructors.

```java
// Already exists — confirm Show enforces:
// - name is not blank
// - endingAt is after startingAt
// - seat counts are non-negative
// - prices are non-negative
```

If you need a new exception type, add it under `org.amts.application.exceptions`. Mirror the existing hierarchy — persistence failures extend `PersistenceException`, permission failures extend `PermissionException`.

### Step 2 — Define the Application Interface

Create an interface in `org.amts.application.usecases.<feature>`. One interface per responsibility — separate persistence from business logic commands.

```java
// ShowPersistenceUseCase.java
public interface ShowPersistenceUseCase {
    void saveShow(Show show);
    Optional<Show> getShowById(UUID showId);
}

// ShowManagementUseCase.java
public interface ShowManagementUseCase {
    void createShow(UUID actorUserId, UUID eventId, ShowDetails details);
}
```

Keep signatures in terms of domain types only. If a use case needs multiple pieces of data (like `ShowDetails`), define a simple record or value object in the application layer to hold them.

### Step 3 — Create the Application Facade

Add a facade class (or extend an existing one if it belongs to the same bounded context) in `org.amts.application.usecases.<feature>`:

```java
public class ShowUseCases {
    private final ShowPersistenceUseCase persistence;
    private final ShowManagementUseCase showManagement;

    public ShowUseCases(ShowPersistenceUseCase persistence, ShowManagementUseCase showManagement) {
        this.persistence = persistence;
        this.showManagement = showManagement;
    }

    public void createShow(UUID actorUserId, UUID eventId, ShowDetails details) {
        showManagement.createShow(actorUserId, eventId, details);
    }

    public Optional<Show> getShowById(UUID showId) {
        return persistence.getShowById(showId);
    }
}
```

### Step 4 — Implement the Business Logic (Adapter Layer)

Create the implementation in `org.amts.adapters.usecases.<feature>`. This class:

- Takes use case interfaces as constructor parameters (not concrete classes)
- Implements the orchestration and permission rules
- Does not import Spring annotations
- Throws application-layer exceptions, not raw `RuntimeException`

```java
public class ShowManagementImpl implements ShowManagementUseCase {

    private final UserPersistenceUseCase userPersistence;
    private final ShowPersistenceUseCase showPersistence;

    public ShowManagementImpl(
            UserPersistenceUseCase userPersistence,
            ShowPersistenceUseCase showPersistence) {
        this.userPersistence = userPersistence;
        this.showPersistence = showPersistence;
    }

    @Override
    public void createShow(UUID actorUserId, UUID eventId, ShowDetails details) {
        User actor = userPersistence.getUserById(actorUserId)
                .orElseThrow(() -> new UserNotFoundException(actorUserId));

        if (!actor.hasRolesAny(Role.SHOW_MANAGER, Role.AUDITORIUM_SECRETARY)) {
            throw new UnauthorizedShowCreationException(actorUserId);
        }

        Show show = new Show(
            UUID.randomUUID(),
            eventId,
            actorUserId,
            details.name(),
            // ... rest of fields from ShowDetails
        );

        showPersistence.saveShow(show);
    }
}
```

### Step 5 — Implement Persistence (Adapter Layer)

Create the persistence implementation in the same package. Use jOOQ via `DSLContext`. Map between jOOQ-generated types and domain types explicitly — never leak generated record types across the boundary.

```java
public class ShowPersistenceImpl implements ShowPersistenceUseCase {

    private final DSLContext dsl;

    public ShowPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void saveShow(Show show) {
        dsl.insertInto(SHOW)
                .set(SHOW.ID, show.getId())
                .set(SHOW.EVENT_ID, show.getEventId())
                .set(SHOW.NAME, show.getName())
                // ... remaining fields
                .execute();
    }

    @Override
    public Optional<Show> getShowById(UUID showId) {
        var record = dsl.selectFrom(SHOW)
                .where(SHOW.ID.eq(showId))
                .fetchOne();

        return Optional.ofNullable(record).map(this::mapToShow);
    }

    private Show mapToShow(ShowRecord r) {
        return new Show(
            r.getId(),
            r.getEventId(),
            // ...
        );
    }
}
```

### Step 6 — Add the Controller

Create a controller in `org.amts.adapters.http.controllers`. Define request/response records in the same file if they are small, or in `org.amts.adapters.http.dto` if they are reused.

```java
record CreateShowRequest(UUID actorUserId, UUID eventId, String name, ...) {}

@RestController
@RequestMapping("/api/show")
public class ShowController {

    private final ShowUseCases showUseCases;

    public ShowController(ShowUseCases showUseCases) {
        this.showUseCases = showUseCases;
    }

    @PostMapping
    public ResponseEntity<Void> createShow(@RequestBody CreateShowRequest request) {
        showUseCases.createShow(
            request.actorUserId(),
            request.eventId(),
            new ShowDetails(request.name(), ...)
        );
        return ResponseEntity.ok().build();
    }
}
```

If the use case can throw a new exception type, register it in `GlobalExceptionHandler`:

```java
@ExceptionHandler(UnauthorizedShowCreationException.class)
public ResponseEntity<Map<String, String>> handleUnauthorizedShowCreation(
        UnauthorizedShowCreationException ex) {
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", ex.getMessage()));
}
```

### Step 7 — Wire It Together in Infrastructure

Create (or extend) a `@Configuration` class in `org.amts.infrastructure.config`:

```java
@Configuration
public class ShowConfiguration {

    @Bean
    public ShowPersistenceUseCase showPersistence(DSLContext dslContext) {
        return new ShowPersistenceImpl(dslContext);
    }

    @Bean
    public ShowManagementUseCase showManagement(
            UserPersistenceUseCase userPersistence,
            ShowPersistenceUseCase showPersistence) {
        return new ShowManagementImpl(userPersistence, showPersistence);
    }

    @Bean
    public ShowUseCases showUseCases(
            ShowPersistenceUseCase showPersistence,
            ShowManagementUseCase showManagement) {
        return new ShowUseCases(showPersistence, showManagement);
    }
}
```

Spring will resolve cross-configuration bean dependencies automatically (e.g. `UserPersistenceUseCase` defined in `UserConfiguration` can be injected into a bean in `ShowConfiguration`).

### Step 8 — Write the Tests

Three test files mirror the three testable layers:

**Domain tests** (`org.amts.domain.entities.<feature>`) — Unit test entity construction, invariants, and helper methods with no mocking.

**Use case tests** (`org.amts.application.usecases.<feature>`) — Mock the use case interfaces, verify the facade delegates correctly. These are very thin.

**Adapter tests** (`org.amts.adapters.usecases.<feature>`) — Two test classes:
- The business logic implementation (`ShowManagementImplTest`) uses Mockito to mock the persistence interface and asserts that the correct exceptions are thrown and `updateX` methods are called with the right arguments.
- The persistence implementation (`ShowPersistenceImplTest`) uses jOOQ's `MockConnection` / `MockDataProvider` to simulate database responses without a live DB.

---

## Quick Reference: Where Does Each Thing Go?

| Concern | Package | Notes |
|---|---|---|
| Entity / Value Object | `org.amts.domain.entities` | No framework imports |
| New exception type | `org.amts.application.exceptions` | Extend existing base exceptions |
| Use case interface | `org.amts.application.usecases.<feature>` | Domain types only in signatures |
| Application facade | `org.amts.application.usecases.<feature>` | Composes interfaces, no logic |
| Business logic impl | `org.amts.adapters.usecases.<feature>` | Implements use case interface |
| Persistence impl | `org.amts.adapters.usecases.<feature>` | jOOQ queries, maps to domain |
| Controller | `org.amts.adapters.http.controllers` | Calls facade, no business logic |
| DTOs | `org.amts.adapters.http.dto` | Static `from()` factory pattern |
| Exception → HTTP mapping | `GlobalExceptionHandler` | One handler per exception type |
| Bean wiring | `org.amts.infrastructure.config` | Only place for `@Bean` / `@Configuration` |

---

## Key Rules to Keep in Mind

**Inner layers never import outer layers.** Domain imports nothing. Application imports only domain. Adapters import application and domain. Infrastructure imports everything.

**No `@Component` or `@Service` on implementation classes.** All Spring wiring is explicit and lives in `org.amts.infrastructure.config`. This makes the dependency graph readable in one place and keeps implementations testable without a Spring context.

**Persistence implementations never return jOOQ record types.** Always map to domain entities before returning. The domain must not know jOOQ exists.

**Business logic never lives in controllers.** Controllers translate HTTP to method calls and method results to HTTP responses. That is their entire job.

**Exceptions are domain/application concepts.** Define them in `org.amts.application.exceptions`, throw them from adapter implementations, and map them to HTTP responses in `GlobalExceptionHandler`. Never throw `ResponseStatusException` from business logic code.