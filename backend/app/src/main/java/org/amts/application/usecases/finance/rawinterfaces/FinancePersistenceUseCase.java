package org.amts.application.usecases.finance.rawinterfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.finance.Expense;

public interface FinancePersistenceUseCase {

    Optional<BalanceSheet> findBalanceSheetByShow(UUID showId);

    BalanceSheet createBalanceSheet(UUID clerkUserId, UUID showId);

    List<BalanceSheet> findBalanceSheetsByEvent(UUID eventId);

    void saveExpense(Expense expense);

    Optional<Expense> findExpenseById(UUID expenseId);

    void updateExpenseName(UUID expenseId, String newName);

    void updateExpenseDescription(UUID expenseId, String newDescription);

    void updateExpenseAmount(UUID expenseId, double newAmount);

    void deleteExpense(UUID expenseId);

    List<Expense> findExpensesByBalanceSheet(UUID balanceSheetId);
}
