package org.amts.domain.entities.finance;

import org.amts.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Expense Domain Entity Tests")
class ExpenseTest {

    private Expense validExpense() {
        return new Expense(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Lighting",
                "Stage lighting equipment",
                Money.of(5000.0),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("validExpense - Creates successfully with all required fields")
        void validExpense_CreatesSuccessfully() {
            assertDoesNotThrow(() -> validExpense());
        }

        @Test
        @DisplayName("blankName - Throws IllegalArgumentException")
        void blankName_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                            "  ", null, Money.of(100.0), LocalDateTime.now()));
        }

        @Test
        @DisplayName("negativeAmount - Throws IllegalArgumentException")
        void negativeAmount_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                            "Lights", null, Money.of(-1.0), LocalDateTime.now()));
        }

        @Test
        @DisplayName("nullId - Throws NullPointerException")
        void nullId_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Expense(null, UUID.randomUUID(), UUID.randomUUID(),
                            "Lights", null, Money.of(100.0), LocalDateTime.now()));
        }

        @Test
        @DisplayName("nullBalanceSheetId - Throws NullPointerException")
        void nullBalanceSheetId_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), null,
                            "Lights", null, Money.of(100.0), LocalDateTime.now()));
        }

        @Test
        @DisplayName("nullAmount - Throws NullPointerException")
        void nullAmount_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                            "Lights", null, null, LocalDateTime.now()));
        }

        @Test
        @DisplayName("zeroAmount - Allowed (boundary case)")
        void zeroAmount_IsAllowed() {
            assertDoesNotThrow(() ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                            "Lights", null, Money.zero(), LocalDateTime.now()));
        }

        @Test
        @DisplayName("nullDescription - Allowed (optional field)")
        void nullDescription_IsAllowed() {
            assertDoesNotThrow(() ->
                    new Expense(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                            "Lights", null, Money.of(100.0), LocalDateTime.now()));
        }
    }

    @Nested
    @DisplayName("Identity Tests")
    class IdentityTests {

        @Test
        @DisplayName("equalExpenses - Same UUID means equal")
        void equalExpenses_SameId() {
            UUID id = UUID.randomUUID();
            Expense a = new Expense(id, UUID.randomUUID(), UUID.randomUUID(),
                    "A", null, Money.of(100.0), LocalDateTime.now());
            Expense b = new Expense(id, UUID.randomUUID(), UUID.randomUUID(),
                    "B", "desc", Money.of(999.0), LocalDateTime.now());
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("differentId - Different UUIDs means not equal")
        void differentId_NotEqual() {
            Expense a = validExpense();
            Expense b = validExpense();
            assertNotEquals(a, b);
        }
    }
}
