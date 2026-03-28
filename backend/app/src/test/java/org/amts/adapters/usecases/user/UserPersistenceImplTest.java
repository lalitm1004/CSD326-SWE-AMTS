package org.amts.adapters.usecases.user;

import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.jooq.enums.Roleenum;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.amts.jooq.Tables.USER_PROFILE;
import static org.amts.jooq.Tables.USER_ROLE_ASSIGNMENT;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserPersistenceImpl Integration Tests")
class UserPersistenceImplTest {

    @Nested
    @DisplayName("User Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("getUserById - Returns user and roles when record exists in DB")
        void getUserById_Found() throws SQLException {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            String expectedEmail = "test@example.com";
            LocalDateTime expectedTime = LocalDateTime.now();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

                var resultType = dsl.newResult(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE);

                // Simulate multiple rolls for the same user (since we join with roles)
                var record1 = dsl.newRecord(USER_PROFILE.ID, USER_PROFILE.EMAIL, USER_PROFILE.CREATED_AT, USER_ROLE_ASSIGNMENT.ROLE);
                record1.values(expectedId, expectedEmail, expectedTime, Roleenum.SHOW_MANAGER);

                var record2 = dsl.newRecord(USER_PROFILE.ID, USER_PROFILE.EMAIL, USER_PROFILE.CREATED_AT, USER_ROLE_ASSIGNMENT.ROLE);
                record2.values(expectedId, expectedEmail, expectedTime, Roleenum.SPECTATOR);

                resultType.add(record1);
                resultType.add(record2);

                return new MockResult[] { new MockResult(2, resultType) };
            };

            UserPersistenceImpl persistence = createPersistence(provider);

            // Act
            Optional<User> result = persistence.getUserById(expectedId);

            // Assert
            assertTrue(result.isPresent(), "User should be present");
            User user = result.get();
            assertAll(
                "Verify user fields",
                () -> assertEquals(expectedId, user.getId()),
                () -> assertEquals(expectedEmail, user.getEmail()),
                () -> assertEquals(expectedTime, user.getCreatedAt()),
                () -> assertEquals(Set.of(Role.SHOW_MANAGER, Role.SPECTATOR), user.getRoles(), "Roles were not parsed correctly")
            );
        }

        @Test
        @DisplayName("getUserById - Returns empty Optional when user ID not found")
        void getUserById_NotFound() {
            // Arrange
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var emptyResult = dsl.newResult(USER_PROFILE.ID, USER_PROFILE.EMAIL, USER_PROFILE.CREATED_AT, USER_ROLE_ASSIGNMENT.ROLE);
                return new MockResult[] { new MockResult(0, emptyResult) };
            };
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act
            Optional<User> result = persistence.getUserById(UUID.randomUUID());

            // Assert
            assertTrue(result.isEmpty(), "User should be empty");
        }

        @Test
        @DisplayName("getUserByEmail - Returns user when email exists")
        void getUserByEmail_Found() throws SQLException {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            String expectedEmail = "test@example.com";
            LocalDateTime expectedTime = LocalDateTime.now();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var resultType = dsl.newResult(USER_PROFILE.ID, USER_PROFILE.EMAIL, USER_PROFILE.CREATED_AT, USER_ROLE_ASSIGNMENT.ROLE);

                var record = dsl.newRecord(USER_PROFILE.ID, USER_PROFILE.EMAIL, USER_PROFILE.CREATED_AT, USER_ROLE_ASSIGNMENT.ROLE);
                // If role is null in DB, it should default to SPECTATOR in the domain entity
                record.values(expectedId, expectedEmail, expectedTime, null);
                resultType.add(record);

                return new MockResult[] { new MockResult(1, resultType) };
            };
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act
            Optional<User> result = persistence.getUserByEmail(expectedEmail);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(Set.of(Role.SPECTATOR), result.get().getRoles(), "Default role spectator not functioning");
        }
    }

    @Nested
    @DisplayName("User Mutation Tests")
    class MutationTests {

        @Test
        @DisplayName("updateUserEmail - Updates email when user exists")
        void updateUserEmail_Success() {
            // Arrange
            MockDataProvider provider = ctx -> new MockResult[] { new MockResult(1, null) }; // 1 row affected
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act & Assert
            assertDoesNotThrow(() -> persistence.updateUserEmail(UUID.randomUUID(), "new@example.com"));
        }

        @Test
        @DisplayName("updateUserEmail - Throws UserNotFoundException when user record missing")
        void updateUserEmail_UserNotFound() {
            // Arrange
            MockDataProvider provider = ctx -> new MockResult[] { new MockResult(0, null) }; // 0 rows affected
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> persistence.updateUserEmail(UUID.randomUUID(), "test@test.com"));
        }

        @Test
        @DisplayName("updateUserRoles - Replaces existing roles when user exists")
        void updateUserRoles_Success() {
            // Arrange
            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

                if (sql.contains("exists")) {
                    var field = DSL.field("exists", Boolean.class);
                    var result = dsl.newResult(field);
                    var record = dsl.newRecord(field);
                    record.set(field, true);
                    result.add(record);
                    return new MockResult[] { new MockResult(1, result) };
                }
                return new MockResult[] { new MockResult(1, null) };
            };
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act & Assert
            assertDoesNotThrow(() -> persistence.updateUserRoles(UUID.randomUUID(), Set.of(Role.SHOW_MANAGER)));
        }

        @Test
        @DisplayName("updateUserRoles - Throws UserNotFoundException if user doesn't exist")
        void updateUserRoles_UserNotFound() {
            // Arrange
            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

                if (sql.contains("exists")) {
                    var field = DSL.field("exists", Boolean.class);
                    var result = dsl.newResult(field);
                    var record = dsl.newRecord(field);
                    record.set(field, false);
                    result.add(record);
                    return new MockResult[] { new MockResult(1, result) };
                }
                return new MockResult[] { new MockResult(0, null) };
            };
            UserPersistenceImpl persistence = createPersistence(provider);

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> persistence.updateUserRoles(UUID.randomUUID(), Set.of(Role.SHOW_MANAGER)));
        }
    }

    /**
     * Helper method to create UserPersistenceImpl with a mocked JOOQ DSLContext.
     */
    private UserPersistenceImpl createPersistence(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new UserPersistenceImpl(mockDsl);
    }
}
