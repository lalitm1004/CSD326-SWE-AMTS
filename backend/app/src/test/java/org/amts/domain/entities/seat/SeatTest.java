package org.amts.domain.entities.seat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Seat Domain Entity Tests")
class SeatTest {

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("validSeat - Creates successfully with all required fields")
        void validSeat_CreatesSuccessfully() {
            assertDoesNotThrow(() -> new Seat(UUID.randomUUID(), "A1", SeatType.ORDINARY));
        }

        @Test
        @DisplayName("nullId - Throws NullPointerException")
        void nullId_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Seat(null, "A1", SeatType.ORDINARY));
        }

        @Test
        @DisplayName("nullNumber - Throws NullPointerException")
        void nullNumber_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Seat(UUID.randomUUID(), null, SeatType.ORDINARY));
        }

        @Test
        @DisplayName("blankNumber - Throws IllegalArgumentException")
        void blankNumber_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Seat(UUID.randomUUID(), "   ", SeatType.ORDINARY));
        }

        @Test
        @DisplayName("nullType - Throws NullPointerException")
        void nullType_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new Seat(UUID.randomUUID(), "A1", null));
        }

        @Test
        @DisplayName("balconySeat - Creates successfully with BALCONY type")
        void balconySeat_CreatesSuccessfully() {
            Seat seat = new Seat(UUID.randomUUID(), "B5", SeatType.BALCONY);
            assertEquals(SeatType.BALCONY, seat.getType());
            assertEquals("B5", seat.getNumber());
        }
    }

    @Nested
    @DisplayName("Identity Tests")
    class IdentityTests {

        @Test
        @DisplayName("equalSeats - Same UUID means equal regardless of other fields")
        void equalSeats_SameId() {
            UUID id = UUID.randomUUID();
            Seat a = new Seat(id, "A1", SeatType.ORDINARY);
            Seat b = new Seat(id, "Z99", SeatType.BALCONY);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("differentId - Different UUIDs means not equal")
        void differentId_NotEqual() {
            Seat a = new Seat(UUID.randomUUID(), "A1", SeatType.ORDINARY);
            Seat b = new Seat(UUID.randomUUID(), "A1", SeatType.ORDINARY);
            assertNotEquals(a, b);
        }
    }
}
