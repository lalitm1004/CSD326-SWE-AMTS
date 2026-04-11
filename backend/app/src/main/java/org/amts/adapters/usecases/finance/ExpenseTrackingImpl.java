package org.amts.adapters.usecases.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.finance.ExpenseTrackingUseCase;
import org.amts.application.usecases.finance.rawinterfaces.FinancePersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.finance.Expense;
import org.amts.domain.entities.user.Role;
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

    @Override
    public void createExpense(UUID clerkUserId, UUID showId, String name, String description, double amount) {
        AuthorizationHelper.getAuthorizedUser(clerkUserId, userPersistence, Role.FINANCIAL_CLERK);

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
        AuthorizationHelper.getAuthorizedUser(clerkUserId, userPersistence, Role.FINANCIAL_CLERK);
        persistence.updateExpenseName(expenseId, newName);
    }

    @Override
    public void updateExpenseDescription(UUID clerkUserId, UUID expenseId, String newDescription) {
        AuthorizationHelper.getAuthorizedUser(clerkUserId, userPersistence, Role.FINANCIAL_CLERK);
        persistence.updateExpenseDescription(expenseId, newDescription);
    }

    @Override
    public void updateExpenseAmount(UUID clerkUserId, UUID expenseId, double newAmount) {
        AuthorizationHelper.getAuthorizedUser(clerkUserId, userPersistence, Role.FINANCIAL_CLERK);
        persistence.updateExpenseAmount(expenseId, newAmount);
    }

    @Override
    public void deleteExpense(UUID clerkUserId, UUID expenseId) {
        AuthorizationHelper.getAuthorizedUser(clerkUserId, userPersistence, Role.FINANCIAL_CLERK);
        persistence.deleteExpense(expenseId);
    }

    @Override
    public Optional<BalanceSheet> getBalanceSheetByShow(UUID userId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.FINANCIAL_CLERK, Role.PRESIDENT);
        return persistence.findBalanceSheetByShow(showId);
    }

    @Override
    public List<BalanceSheet> getBalanceSheetsByEvent(UUID userId, UUID eventId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.FINANCIAL_CLERK, Role.PRESIDENT);
        return persistence.findBalanceSheetsByEvent(eventId);
    }
}