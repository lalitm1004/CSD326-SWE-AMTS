package org.amts.adapters.persistence;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.jooq.enums.Roleenum;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.amts.jooq.Tables.USER_PROFILE;
import static org.amts.jooq.Tables.USER_ROLE_ASSIGNMENT;
import static org.junit.jupiter.api.Assertions.*;

class UserPersistenceImplTest {

    @Test
    void getUserById_returnsUserAndRoles_whenRecordExists() throws SQLException {
        UUID expectedId = UUID.randomUUID();
        String expectedEmail = "test@example.com";
        LocalDateTime expectedTime = LocalDateTime.now();

        MockDataProvider provider = new MockDataProvider() {
            @Override
            public MockResult[] execute(MockExecuteContext ctx) {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                
                var resultType = dsl.newResult(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                );
                
                var record1 = dsl.newRecord(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                );
                record1.values(expectedId, expectedEmail, expectedTime, Roleenum.SHOW_MANAGER);
                
                var record2 = dsl.newRecord(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                );
                record2.values(expectedId, expectedEmail, expectedTime, Roleenum.SPECTATOR);
                
                resultType.add(record1);
                resultType.add(record2);
                
                return new MockResult[]{new MockResult(2, resultType)};
            }
        };
        
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        UserPersistenceImpl persistence = new UserPersistenceImpl(mockDsl);

        Optional<User> result = persistence.getUserById(expectedId);

        assertTrue(result.isPresent(), "User should be present");
        User user = result.get();
        assertEquals(expectedId, user.getId());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedTime, user.getCreatedAt());
        assertEquals(Set.of(Role.SHOW_MANAGER, Role.SPECTATOR), user.getRoles(), "Roles were not parsed correctly");
    }

    @Test
    void getUserById_returnsEmpty_whenNotFound() {
        MockDataProvider provider = new MockDataProvider() {
            @Override
            public MockResult[] execute(MockExecuteContext ctx) {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var emptyResult = dsl.newResult(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                ); 
                return new MockResult[]{new MockResult(0, emptyResult)};
            }
        };

        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        UserPersistenceImpl persistence = new UserPersistenceImpl(mockDsl);

        Optional<User> result = persistence.getUserById(UUID.randomUUID());

        assertTrue(result.isEmpty(), "User should be empty");
    }

    @Test
    void getUserByEmail_returnsUser_whenFound() throws SQLException {
        UUID expectedId = UUID.randomUUID();
        String expectedEmail = "test@example.com";
        LocalDateTime expectedTime = LocalDateTime.now();

        MockDataProvider provider = new MockDataProvider() {
            @Override
            public MockResult[] execute(MockExecuteContext ctx) {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

                var resultType = dsl.newResult(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                );

                var record1 = dsl.newRecord(
                        USER_PROFILE.ID,
                        USER_PROFILE.EMAIL,
                        USER_PROFILE.CREATED_AT,
                        USER_ROLE_ASSIGNMENT.ROLE
                );
                // What if a user has no roles in the DB? Our mapToUser logic defaults to SPECTATOR
                record1.values(expectedId, expectedEmail, expectedTime, null);

                resultType.add(record1);

                return new MockResult[]{new MockResult(1, resultType)};
            }
        };

        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        UserPersistenceImpl persistence = new UserPersistenceImpl(mockDsl);

        Optional<User> result = persistence.getUserByEmail(expectedEmail);

        assertTrue(result.isPresent(), "User should be present");
        User user = result.get();
        assertEquals(expectedId, user.getId());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(Set.of(Role.SPECTATOR), user.getRoles(), "Default role spectator not functioning");
    }
}
