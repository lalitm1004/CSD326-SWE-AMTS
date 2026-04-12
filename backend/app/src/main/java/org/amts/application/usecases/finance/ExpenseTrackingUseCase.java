package org.amts.application.usecases.finance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.finance.BalanceSheet;

public interface ExpenseTrackingUseCase {

    void createExpense(UUID clerkUserId, UUID showId, String name, String description, double amount);

    void updateExpenseName(UUID clerkUserId, UUID expenseId, String newName);

    void updateExpenseDescription(UUID clerkUserId, UUID expenseId, String newDescription);

    void updateExpenseAmount(UUID clerkUserId, UUID expenseId, double newAmount);

    void deleteExpense(UUID clerkUserId, UUID expenseId);

    Optional<BalanceSheet> getBalanceSheetByShow(UUID userId, UUID showId);

    List<BalanceSheet> getBalanceSheetsByEvent(UUID userId, UUID eventId);

    List<BalanceSheet> getBalanceSheetsByYear(UUID userId, int year);
}
