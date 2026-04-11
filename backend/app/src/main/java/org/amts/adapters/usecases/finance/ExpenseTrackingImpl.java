package org.amts.adapters.usecases.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.finance.ExpenseTrackingUseCase;
import org.amts.application.usecases.finance.rawinterfaces.FinancePersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.finance.Expense;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.domain.valueobjects.Money;

public class ExpenseTrackingImpl implements ExpenseTrackingUseCase {

    private final FinancePersistenceUseCase persistence;
    private final UserPersistenceUseCase userPersistence;

    public ExpenseTrackingImpl(
            FinancePersistenceUseCase persistence,
            UserPersistenceUseCase userPersistence) {
        this.persistence = persistence;
        this.userPersistence = userPersistence;
    }

    private User getClerk(UUID userId) {
        User user = userPersistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.hasRolesAny(Role.FINANCIAL_CLERK)) {
            throw new PermissionException("User does not have Financial Clerk permission");
        }
        return user;
    }

    private User getReadAuthorizedUser(UUID userId) {
        User user = userPersistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.hasRolesAny(Role.FINANCIAL_CLERK, Role.PRESIDENT)) {
            throw new PermissionException("User does not have permission to view financial records");
        }
        return user;
    }

    @Override
    public void createExpense(UUID clerkUserId, UUID showId, String name, String description, double amount) {
        getClerk(clerkUserId);

        BalanceSheet sheet = persistence.findBalanceSheetByShow(showId)
                .orElseGet(() -> persistence.createBalanceSheet(clerkUserId, showId));

        Expense expense = new Expense(
                UUID.randomUUID(),
                clerkUserId,
                sheet.getId(),
                name,
                description,
                Money.of(amount),
                LocalDateTime.now()
        );

        persistence.saveExpense(expense);
    }

    @Override
    public void updateExpenseName(UUID clerkUserId, UUID expenseId, String newName) {
        getClerk(clerkUserId);
        persistence.updateExpenseName(expenseId, newName);
    }

    @Override
    public void updateExpenseDescription(UUID clerkUserId, UUID expenseId, String newDescription) {
        getClerk(clerkUserId);
        persistence.updateExpenseDescription(expenseId, newDescription);
    }

    @Override
    public void updateExpenseAmount(UUID clerkUserId, UUID expenseId, double newAmount) {
        getClerk(clerkUserId);
        persistence.updateExpenseAmount(expenseId, newAmount);
    }

    @Override
    public void deleteExpense(UUID clerkUserId, UUID expenseId) {
        getClerk(clerkUserId);
        persistence.deleteExpense(expenseId);
    }

    @Override
    public Optional<BalanceSheet> getBalanceSheetByShow(UUID userId, UUID showId) {
        getReadAuthorizedUser(userId);
        return persistence.findBalanceSheetByShow(showId);
    }

    @Override
    public List<BalanceSheet> getBalanceSheetsByEvent(UUID userId, UUID eventId) {
        getReadAuthorizedUser(userId);
        return persistence.findBalanceSheetsByEvent(eventId);
    }
}
