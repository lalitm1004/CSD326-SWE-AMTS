package org.amts.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Tests")
class MoneyTest {

    @Nested
    @DisplayName("Arithmetic Tests")
    class ArithmeticTests {

        @Test
        @DisplayName("add - sums two positive amounts")
        void add_twoPositive() {
            Money result = Money.of(100).add(Money.of(50));
            assertEquals(Money.of(150), result);
        }

        @Test
        @DisplayName("subtract - returns difference (can be negative)")
        void subtract_returnsNegativeWhenSmaller() {
            Money result = Money.of(30).subtract(Money.of(50));
            assertEquals(Money.of(-20), result);
        }

        @Test
        @DisplayName("multiply - scales by factor")
        void multiply_byFactor() {
            Money result = Money.of(40).multiply(2.5);
            assertEquals(Money.of(100), result);
        }

        @Test
        @DisplayName("percentage - 10% of 200 is 20 (coupon discount formula)")
        void percentage_tenPercent() {
            Money result = Money.of(200).percentage(10);
            assertEquals(Money.of(20), result);
        }

        @Test
        @DisplayName("percentage - 0% of any amount is zero")
        void percentage_zero() {
            Money result = Money.of(500).percentage(0);
            assertEquals(Money.zero(), result);
        }

        @Test
        @DisplayName("add - zero is identity element")
        void add_zero_isIdentity() {
            Money result = Money.of(75).add(Money.zero());
            assertEquals(Money.of(75), result);
        }
    }

    @Nested
    @DisplayName("Predicate Tests")
    class PredicateTests {

        @Test
        @DisplayName("isNegative - true for negative amount")
        void isNegative_negative() {
            assertTrue(Money.of(-1).isNegative());
        }

        @Test
        @DisplayName("isNegative - false for positive amount")
        void isNegative_positive() {
            assertFalse(Money.of(1).isNegative());
        }

        @Test
        @DisplayName("isNegative - false for zero")
        void isNegative_zero() {
            assertFalse(Money.zero().isNegative());
        }

        @Test
        @DisplayName("isZero - true for zero")
        void isZero_zero() {
            assertTrue(Money.zero().isZero());
        }

        @Test
        @DisplayName("isZero - false for non-zero")
        void isZero_nonZero() {
            assertFalse(Money.of(0.01).isZero());
        }

        @Test
        @DisplayName("isPositive - true for positive amount")
        void isPositive_positive() {
            assertTrue(Money.of(1).isPositive());
        }

        @Test
        @DisplayName("isPositive - false for negative amount")
        void isPositive_negative() {
            assertFalse(Money.of(-1).isPositive());
        }

        @Test
        @DisplayName("isPositive - false for zero")
        void isPositive_zero() {
            assertFalse(Money.zero().isPositive());
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("equals - same amount is equal")
        void equals_sameAmount() {
            assertEquals(Money.of(100), Money.of(100));
        }

        @Test
        @DisplayName("equals - different amount is not equal")
        void equals_differentAmount() {
            assertNotEquals(Money.of(100), Money.of(101));
        }

        @Test
        @DisplayName("zero() equals Money.of(0)")
        void zero_equalsMoneyOfZero() {
            assertEquals(Money.zero(), Money.of(0));
        }

        @Test
        @DisplayName("hashCode - equal amounts have equal hash codes")
        void hashCode_equalAmounts() {
            assertEquals(Money.of(50).hashCode(), Money.of(50).hashCode());
        }
    }

    @Nested
    @DisplayName("Null Safety Tests")
    class NullSafetyTests {

        @Test
        @DisplayName("add - throws NullPointerException for null argument")
        void add_null_throws() {
            assertThrows(NullPointerException.class, () -> Money.of(10).add(null));
        }

        @Test
        @DisplayName("subtract - throws NullPointerException for null argument")
        void subtract_null_throws() {
            assertThrows(NullPointerException.class, () -> Money.of(10).subtract(null));
        }
    }
}
