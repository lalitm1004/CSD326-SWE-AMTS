package org.amts.adapters.usecases.finance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.finance.rawinterfaces.FinancePersistenceUseCase;
import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.finance.Expense;
import org.amts.domain.valueobjects.Money;
import org.amts.jooq.Tables;
import org.amts.jooq.tables.records.BalanceSheetRecord;
import org.amts.jooq.tables.records.ExpenseRecord;
import org.jooq.DSLContext;

public class FinancePersistenceImpl implements FinancePersistenceUseCase {

    private final DSLContext dsl;

    public FinancePersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private Expense toExpense(ExpenseRecord r) {
        return new Expense(
                r.getId(),
                r.getFinancialClerkUserId(),
                r.getBalanceSheetId(),
                r.getName(),
                r.getDescription(),
                Money.of(r.getAmount()),
                r.getCreatedAt()
        );
    }

    private BalanceSheet toBalanceSheet(BalanceSheetRecord r) {
        List<Expense> expenses = findExpensesByBalanceSheet(r.getId());
        return new BalanceSheet(
                r.getId(),
                r.getFinancialClerkUserId(),
                r.getShowId(),
                expenses,
                r.getCreatedAt()
        );
    }

    @Override
    public Optional<BalanceSheet> findBalanceSheetByShow(UUID showId) {
        return dsl.selectFrom(Tables.BALANCE_SHEET)
                .where(Tables.BALANCE_SHEET.SHOW_ID.eq(showId))
                .fetchOptional()
                .map(this::toBalanceSheet);
    }

    @Override
    public BalanceSheet createBalanceSheet(UUID clerkUserId, UUID showId) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        BalanceSheetRecord record = dsl.newRecord(Tables.BALANCE_SHEET);
        record.setId(id);
        record.setFinancialClerkUserId(clerkUserId);
        record.setShowId(showId);
        record.setCreatedAt(now);
        record.store();

        return new BalanceSheet(id, clerkUserId, showId, List.of(), now);
    }

    @Override
    public List<BalanceSheet> findBalanceSheetsByEvent(UUID eventId) {
        var records = dsl.selectFrom(Tables.BALANCE_SHEET)
                .where(Tables.BALANCE_SHEET.SHOW_ID.in(
                        dsl.select(Tables.SHOW.ID)
                                .from(Tables.SHOW)
                                .where(Tables.SHOW.EVENT_ID.eq(eventId))
                ))
                .fetch();

        List<BalanceSheet> result = new ArrayList<>();
        for (BalanceSheetRecord r : records) {
            result.add(toBalanceSheet(r));
        }
        return result;
    }

    @Override
    public List<BalanceSheet> findBalanceSheetsByYear(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime startOfNextYear = startOfYear.plusYears(1);

        var records = dsl.selectFrom(Tables.BALANCE_SHEET)
                .where(Tables.BALANCE_SHEET.CREATED_AT.ge(startOfYear))
                .and(Tables.BALANCE_SHEET.CREATED_AT.lt(startOfNextYear))
                .fetch();

        List<BalanceSheet> result = new ArrayList<>();
        for (BalanceSheetRecord r : records) {
            result.add(toBalanceSheet(r));
        }
        return result;
    }

    @Override
    public void saveExpense(Expense expense) {
        ExpenseRecord record = dsl.newRecord(Tables.EXPENSE);
        record.setId(expense.getId());
        record.setFinancialClerkUserId(expense.getFinancialClerkUserId());
        record.setBalanceSheetId(expense.getBalanceSheetId());
        record.setName(expense.getName());
        record.setDescription(expense.getDescription());
        record.setAmount(expense.getAmount().getAmount());
        record.setCreatedAt(expense.getCreatedAt());
        record.store();
    }

    @Override
    public Optional<Expense> findExpenseById(UUID expenseId) {
        return dsl.selectFrom(Tables.EXPENSE)
                .where(Tables.EXPENSE.ID.eq(expenseId))
                .fetchOptional()
                .map(this::toExpense);
    }

    @Override
    public void updateExpenseName(UUID expenseId, String newName) {
        dsl.update(Tables.EXPENSE)
                .set(Tables.EXPENSE.NAME, newName)
                .where(Tables.EXPENSE.ID.eq(expenseId))
                .execute();
    }

    @Override
    public void updateExpenseDescription(UUID expenseId, String newDescription) {
        dsl.update(Tables.EXPENSE)
                .set(Tables.EXPENSE.DESCRIPTION, newDescription)
                .where(Tables.EXPENSE.ID.eq(expenseId))
                .execute();
    }

    @Override
    public void updateExpenseAmount(UUID expenseId, double newAmount) {
        dsl.update(Tables.EXPENSE)
                .set(Tables.EXPENSE.AMOUNT, newAmount)
                .where(Tables.EXPENSE.ID.eq(expenseId))
                .execute();
    }

    @Override
    public void deleteExpense(UUID expenseId) {
        dsl.deleteFrom(Tables.EXPENSE)
                .where(Tables.EXPENSE.ID.eq(expenseId))
                .execute();
    }

    @Override
    public List<Expense> findExpensesByBalanceSheet(UUID balanceSheetId) {
        return dsl.selectFrom(Tables.EXPENSE)
                .where(Tables.EXPENSE.BALANCE_SHEET_ID.eq(balanceSheetId))
                .fetch()
                .map(this::toExpense);
    }
}
