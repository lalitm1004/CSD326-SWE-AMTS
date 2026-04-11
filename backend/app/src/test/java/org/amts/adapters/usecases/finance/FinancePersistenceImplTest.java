package org.amts.adapters.usecases.finance;

import org.amts.domain.entities.finance.BalanceSheet;
import org.amts.domain.entities.finance.Expense;
import org.amts.domain.valueobjects.Money;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.amts.jooq.Tables.BALANCE_SHEET;
import static org.amts.jooq.Tables.EXPENSE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FinancePersistenceImpl Tests")
class FinancePersistenceImplTest {

    private FinancePersistenceImpl createPersistence(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new FinancePersistenceImpl(mockDsl);
    }

    @Nested
    @DisplayName("BalanceSheet Tests")
    class BalanceSheetTests {

        @Test
        @DisplayName("findBalanceSheetByShow - Returns BalanceSheet with expenses when found")
        void findBalanceSheetByShow_Found() {
            UUID sheetId = UUID.randomUUID();
            UUID clerkId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            UUID expenseId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();

                // First query: select from balance_sheet where show_id = ?
                if (sql.contains("balance_sheet") && !sql.contains("expense")) {
                    var result = dsl.newResult(
                            BALANCE_SHEET.ID,
                            BALANCE_SHEET.FINANCIAL_CLERK_USER_ID,
                            BALANCE_SHEET.SHOW_ID,
                            BALANCE_SHEET.CREATED_AT);
                    var record = dsl.newRecord(
                            BALANCE_SHEET.ID,
                            BALANCE_SHEET.FINANCIAL_CLERK_USER_ID,
                            BALANCE_SHEET.SHOW_ID,
                            BALANCE_SHEET.CREATED_AT);
                    record.values(sheetId, clerkId, showId, now);
                    result.add(record);
                    return new MockResult[]{new MockResult(1, result)};
                }

                // Second query: select from expense where balance_sheet_id = ?
                if (sql.contains("expense")) {
                    var result = dsl.newResult(
                            EXPENSE.ID,
                            EXPENSE.FINANCIAL_CLERK_USER_ID,
                            EXPENSE.BALANCE_SHEET_ID,
                            EXPENSE.NAME,
                            EXPENSE.DESCRIPTION,
                            EXPENSE.AMOUNT,
                            EXPENSE.CREATED_AT);
                    var record = dsl.newRecord(
                            EXPENSE.ID,
                            EXPENSE.FINANCIAL_CLERK_USER_ID,
                            EXPENSE.BALANCE_SHEET_ID,
                            EXPENSE.NAME,
                            EXPENSE.DESCRIPTION,
                            EXPENSE.AMOUNT,
                            EXPENSE.CREATED_AT);
                    record.values(expenseId, clerkId, sheetId, "Lighting", "Stage lights", 5000.0, now);
                    result.add(record);
                    return new MockResult[]{new MockResult(1, result)};
                }

                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            FinancePersistenceImpl persistence = createPersistence(provider);
            Optional<BalanceSheet> result = persistence.findBalanceSheetByShow(showId);

            assertTrue(result.isPresent());
            BalanceSheet sheet = result.get();
            assertAll(
                    () -> assertEquals(sheetId, sheet.getId()),
                    () -> assertEquals(showId, sheet.getShowId()),
                    () -> assertEquals(1, sheet.getExpenses().size()),
                    () -> assertEquals("Lighting", sheet.getExpenses().get(0).getName()),
                    () -> assertEquals(Money.of(5000.0), sheet.getTotalExpenses())
            );
        }

        @Test
        @DisplayName("findBalanceSheetByShow - Returns empty Optional when not found")
        void findBalanceSheetByShow_NotFound() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("balance_sheet")) {
                    var result = dsl.newResult(
                            BALANCE_SHEET.ID,
                            BALANCE_SHEET.FINANCIAL_CLERK_USER_ID,
                            BALANCE_SHEET.SHOW_ID,
                            BALANCE_SHEET.CREATED_AT);
                    return new MockResult[]{new MockResult(0, result)};
                }
                return new MockResult[]{new MockResult(0, null)};
            };

            FinancePersistenceImpl persistence = createPersistence(provider);
            Optional<BalanceSheet> result = persistence.findBalanceSheetByShow(UUID.randomUUID());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("createBalanceSheet - Stores record and returns BalanceSheet")
        void createBalanceSheet_StoresRecord() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var emptyExpenses = dsl.newResult(
                        EXPENSE.ID, EXPENSE.FINANCIAL_CLERK_USER_ID, EXPENSE.BALANCE_SHEET_ID,
                        EXPENSE.NAME, EXPENSE.DESCRIPTION, EXPENSE.AMOUNT, EXPENSE.CREATED_AT);
                return new MockResult[]{new MockResult(0, emptyExpenses)};
            };
            FinancePersistenceImpl persistence = createPersistence(provider);

            BalanceSheet sheet = assertDoesNotThrow(() ->
                    persistence.createBalanceSheet(UUID.randomUUID(), UUID.randomUUID()));

            assertNotNull(sheet.getId());
            assertTrue(sheet.getExpenses().isEmpty());
        }
    }

    @Nested
    @DisplayName("Expense Tests")
    class ExpenseTests {

        @Test
        @DisplayName("saveExpense - Stores record without throwing")
        void saveExpense_StoresRecord() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var empty = dsl.newResult(EXPENSE.ID);
                return new MockResult[]{new MockResult(0, empty)};
            };
            FinancePersistenceImpl persistence = createPersistence(provider);

            Expense expense = new Expense(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    "Sound", null, Money.of(2000.0), LocalDateTime.now());

            assertDoesNotThrow(() -> persistence.saveExpense(expense));
        }

        @Test
        @DisplayName("findExpenseById - Returns expense when found")
        void findExpenseById_Found() {
            UUID expenseId = UUID.randomUUID();
            UUID clerkId = UUID.randomUUID();
            UUID sheetId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(
                        EXPENSE.ID, EXPENSE.FINANCIAL_CLERK_USER_ID, EXPENSE.BALANCE_SHEET_ID,
                        EXPENSE.NAME, EXPENSE.DESCRIPTION, EXPENSE.AMOUNT, EXPENSE.CREATED_AT);
                var record = dsl.newRecord(
                        EXPENSE.ID, EXPENSE.FINANCIAL_CLERK_USER_ID, EXPENSE.BALANCE_SHEET_ID,
                        EXPENSE.NAME, EXPENSE.DESCRIPTION, EXPENSE.AMOUNT, EXPENSE.CREATED_AT);
                record.values(expenseId, clerkId, sheetId, "Sound", "PA system", 2000.0, now);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            };

            FinancePersistenceImpl persistence = createPersistence(provider);
            Optional<Expense> result = persistence.findExpenseById(expenseId);

            assertTrue(result.isPresent());
            assertAll(
                    () -> assertEquals(expenseId, result.get().getId()),
                    () -> assertEquals("Sound", result.get().getName()),
                    () -> assertEquals(Money.of(2000.0), result.get().getAmount())
            );
        }

        @Test
        @DisplayName("findExpenseById - Returns empty when not found")
        void findExpenseById_NotFound() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(
                        EXPENSE.ID, EXPENSE.FINANCIAL_CLERK_USER_ID, EXPENSE.BALANCE_SHEET_ID,
                        EXPENSE.NAME, EXPENSE.DESCRIPTION, EXPENSE.AMOUNT, EXPENSE.CREATED_AT);
                return new MockResult[]{new MockResult(0, result)};
            };

            FinancePersistenceImpl persistence = createPersistence(provider);
            assertTrue(persistence.findExpenseById(UUID.randomUUID()).isEmpty());
        }

        @Test
        @DisplayName("deleteExpense - Executes without throwing")
        void deleteExpense_ExecutesDelete() {
            MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
            FinancePersistenceImpl persistence = createPersistence(provider);
            assertDoesNotThrow(() -> persistence.deleteExpense(UUID.randomUUID()));
        }

        @Test
        @DisplayName("updateExpenseName - Executes without throwing")
        void updateExpenseName_ExecutesUpdate() {
            MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
            FinancePersistenceImpl persistence = createPersistence(provider);
            assertDoesNotThrow(() -> persistence.updateExpenseName(UUID.randomUUID(), "New Name"));
        }

        @Test
        @DisplayName("updateExpenseAmount - Executes without throwing")
        void updateExpenseAmount_ExecutesUpdate() {
            MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
            FinancePersistenceImpl persistence = createPersistence(provider);
            assertDoesNotThrow(() -> persistence.updateExpenseAmount(UUID.randomUUID(), 9999.0));
        }
    }
}
