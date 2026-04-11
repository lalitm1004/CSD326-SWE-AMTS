package org.amts.adapters.usecases.finance;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.finance.rawinterfaces.FinancePersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseTrackingImpl Tests")
class ExpenseTrackingImplTest {

    @Mock
    private FinancePersistenceUseCase persistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    @InjectMocks
    private ExpenseTrackingImpl expenseTracking;

    private UUID clerkId;
    private UUID presidentId;
    private UUID spectatorId;
    private UUID showId;
    private UUID expenseId;
    private User clerkUser;
    private User presidentUser;
    private User spectatorUser;
    private BalanceSheet existingSheet;

    @BeforeEach
    void setUp() {
        clerkId = UUID.randomUUID();
        presidentId = UUID.randomUUID();
        spectatorId = UUID.randomUUID();
        showId = UUID.randomUUID();
        expenseId = UUID.randomUUID();

        clerkUser = new User(clerkId, "clerk@test.com", Set.of(Role.FINANCIAL_CLERK, Role.SPECTATOR), LocalDateTime.now());
        presidentUser = new User(presidentId, "president@test.com", Set.of(Role.PRESIDENT, Role.SPECTATOR), LocalDateTime.now());
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());

        existingSheet = new BalanceSheet(UUID.randomUUID(), clerkId, showId, List.of(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Expense Tests")
    class CreateExpenseTests {

        @Test
        @DisplayName("createExpense - Auto-creates BalanceSheet when none exists for show")
        void createExpense_ClerkRole_CreatesBalanceSheetIfAbsent() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.findBalanceSheetByShow(showId)).thenReturn(Optional.empty());
            when(persistence.createBalanceSheet(clerkId, showId)).thenReturn(existingSheet);

            assertDoesNotThrow(() ->
                    expenseTracking.createExpense(clerkId, showId, "Lighting", "Stage lights", 5000.0));

            verify(persistence).createBalanceSheet(eq(clerkId), eq(showId));
            verify(persistence).saveExpense(any());
        }

        @Test
        @DisplayName("createExpense - Uses existing BalanceSheet when already present")
        void createExpense_ClerkRole_UsesExistingBalanceSheet() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.findBalanceSheetByShow(showId)).thenReturn(Optional.of(existingSheet));

            assertDoesNotThrow(() ->
                    expenseTracking.createExpense(clerkId, showId, "Sound", null, 2000.0));

            verify(persistence, never()).createBalanceSheet(any(), any());
            verify(persistence).saveExpense(any());
        }

        @Test
        @DisplayName("createExpense - Throws PermissionException for non-clerk user")
        void createExpense_NotClerk_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    expenseTracking.createExpense(spectatorId, showId, "Lighting", null, 5000.0));

            verify(persistence, never()).saveExpense(any());
        }

        @Test
        @DisplayName("createExpense - Throws UserNotFoundException when user missing")
        void createExpense_UserNotFound_ThrowsUserNotFoundException() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    expenseTracking.createExpense(clerkId, showId, "Lighting", null, 5000.0));
        }
    }

    @Nested
    @DisplayName("Update Expense Tests")
    class UpdateExpenseTests {

        @Test
        @DisplayName("updateExpenseName - Succeeds for clerk")
        void updateExpenseName_ClerkRole_Succeeds() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));

            assertDoesNotThrow(() ->
                    expenseTracking.updateExpenseName(clerkId, expenseId, "New Name"));

            verify(persistence).updateExpenseName(eq(expenseId), eq("New Name"));
        }

        @Test
        @DisplayName("updateExpenseName - Throws PermissionException for non-clerk")
        void updateExpenseName_NotClerk_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    expenseTracking.updateExpenseName(spectatorId, expenseId, "New Name"));

            verify(persistence, never()).updateExpenseName(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Expense Tests")
    class DeleteExpenseTests {

        @Test
        @DisplayName("deleteExpense - Succeeds for clerk")
        void deleteExpense_ClerkRole_Succeeds() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));

            assertDoesNotThrow(() -> expenseTracking.deleteExpense(clerkId, expenseId));

            verify(persistence).deleteExpense(eq(expenseId));
        }

        @Test
        @DisplayName("deleteExpense - Throws PermissionException for non-clerk")
        void deleteExpense_NotClerk_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    expenseTracking.deleteExpense(spectatorId, expenseId));

            verify(persistence, never()).deleteExpense(any());
        }
    }

    @Nested
    @DisplayName("Query Tests")
    class QueryTests {

        @Test
        @DisplayName("getBalanceSheetByShow - Succeeds for Financial Clerk")
        void getBalanceSheetByShow_ClerkRole_ReturnsResult() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.findBalanceSheetByShow(showId)).thenReturn(Optional.of(existingSheet));

            Optional<BalanceSheet> result = expenseTracking.getBalanceSheetByShow(clerkId, showId);

            assertTrue(result.isPresent());
            assertEquals(existingSheet, result.get());
        }

        @Test
        @DisplayName("getBalanceSheetByShow - Succeeds for President")
        void getBalanceSheetByShow_PresidentRole_ReturnsResult() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(persistence.findBalanceSheetByShow(showId)).thenReturn(Optional.of(existingSheet));

            Optional<BalanceSheet> result = expenseTracking.getBalanceSheetByShow(presidentId, showId);

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("getBalanceSheetByShow - Throws PermissionException for Spectator")
        void getBalanceSheetByShow_UnauthorizedRole_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    expenseTracking.getBalanceSheetByShow(spectatorId, showId));

            verify(persistence, never()).findBalanceSheetByShow(any());
        }

        @Test
        @DisplayName("getBalanceSheetsByEvent - Returns list for authorized user")
        void getBalanceSheetsByEvent_ClerkOrPresident_ReturnsResult() {
            UUID eventId = UUID.randomUUID();
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.findBalanceSheetsByEvent(eventId)).thenReturn(List.of(existingSheet));

            List<BalanceSheet> result = expenseTracking.getBalanceSheetsByEvent(clerkId, eventId);

            assertEquals(1, result.size());
            assertEquals(existingSheet, result.get(0));
        }
    }
}
