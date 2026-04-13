package org.amts.adapters.usecases.ticket;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.jooq.enums.Seatdesignationenum;
import org.amts.jooq.enums.Seattypeenum;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.amts.jooq.Tables.SEAT;
import static org.amts.jooq.Tables.SHOW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComplementaryAllocationImpl Tests")
class ComplementaryAllocationImplTest {

    @Mock
    private TicketPersistenceUseCase persistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    private UUID secretaryId;
    private UUID showId;
    private UUID seatId;
    private User secretaryUser;

    @BeforeEach
    void setUp() {
        secretaryId = UUID.randomUUID();
        showId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        secretaryUser = new User(secretaryId, "secretary@test.com", Set.of(Role.AUDITORIUM_SECRETARY), LocalDateTime.now());
    }

    private ComplementaryAllocationImpl createImpl(MockDataProvider provider) {
        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        return new ComplementaryAllocationImpl(mockDsl, persistence, userPersistence);
    }

    /** Builds a MockResult for SHOW with all required columns to satisfy jOOQ's selectFrom(SHOW). */
    private MockResult showResult(DSLContext dsl, UUID showId) {
        var result = dsl.newResult(
                SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME,
                SHOW.DESCRIPTION, SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT,
                SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE,
                SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
        var record = dsl.newRecord(
                SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME,
                SHOW.DESCRIPTION, SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT,
                SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE,
                SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
        record.set(SHOW.ID, showId);
        record.set(SHOW.EVENT_ID, UUID.randomUUID());
        record.set(SHOW.CREATED_BY_USER_ID, UUID.randomUUID());
        record.set(SHOW.NAME, "Test Show");
        record.set(SHOW.DESCRIPTION, "desc");
        record.set(SHOW.THUMBNAIL_URL, null);
        record.set(SHOW.STARTING_AT, LocalDateTime.now().plusDays(7));
        record.set(SHOW.ENDING_AT, LocalDateTime.now().plusDays(7).plusHours(3));
        record.set(SHOW.ORDINARY_SEAT_PRICE, 10.0);
        record.set(SHOW.BALCONY_SEAT_PRICE, 20.0);
        record.set(SHOW.NUM_ORDINARY_SEATS, 600);
        record.set(SHOW.NUM_BALCONY_SEATS, 200);
        record.set(SHOW.CREATED_AT, LocalDateTime.now());
        result.add(record);
        return new MockResult(1, result);
    }

    /** Builds a MockResult for SEAT with COMPLIMENTARY designation. */
    private MockResult complimentarySeatResult(DSLContext dsl, UUID seatId, String number) {
        var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
        var record = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
        record.set(SEAT.ID, seatId);
        record.set(SEAT.NUMBER, number);
        record.set(SEAT.TYPE, Seattypeenum.ORDINARY);
        record.set(SEAT.DESIGNATION, Seatdesignationenum.COMPLIMENTARY);
        result.add(record);
        return new MockResult(1, result);
    }

    @Nested
    @DisplayName("Authorization Tests")
    class AuthorizationTests {

        @Test
        @DisplayName("allocateComplementaryTickets - Throws UserNotFoundException when allocator does not exist")
        void allocate_AllocatorNotFound_ThrowsException() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.empty());
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(UserNotFoundException.class, () ->
                    impl.allocateComplementaryTickets(secretaryId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("allocateComplementaryTickets - Throws PermissionException when allocator lacks AUDITORIUM_SECRETARY role")
        void allocate_AllocatorNotSecretary_ThrowsPermissionException() {
            UUID spectatorId = UUID.randomUUID();
            User spectator = new User(spectatorId, "s@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectator));
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(PermissionException.class, () ->
                    impl.allocateComplementaryTickets(spectatorId, showId, List.of(seatId)));
        }
    }

    @Nested
    @DisplayName("Seat Validation Tests")
    class SeatValidationTests {

        @Test
        @DisplayName("allocateComplementaryTickets - Throws when show does not exist")
        void allocate_ShowNotFound_ThrowsException() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                if (ctx.sql().toLowerCase().contains("show")) {
                    // Return empty result — no show found
                    var result = dsl.newResult(
                            SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME,
                            SHOW.DESCRIPTION, SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT,
                            SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE,
                            SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
                    return new MockResult[]{new MockResult(0, result)};
                }
                return new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())};
            };

            var impl = createImpl(provider);
            assertThrows(IllegalArgumentException.class, () ->
                    impl.allocateComplementaryTickets(secretaryId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("allocateComplementaryTickets - Throws when a seat has non-COMPLIMENTARY designation")
        void allocate_SeatNotComplimentary_ThrowsException() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();

                if (sql.contains("show")) {
                    return new MockResult[]{showResult(dsl, showId)};
                }

                if (sql.contains("seat")) {
                    // Return an ORDINARY (not complimentary) seat
                    var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                    var record = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                    record.set(SEAT.ID, seatId);
                    record.set(SEAT.NUMBER, "A1");
                    record.set(SEAT.TYPE, Seattypeenum.BALCONY);
                    record.set(SEAT.DESIGNATION, Seatdesignationenum.ORDINARY);
                    result.add(record);
                    return new MockResult[]{new MockResult(1, result)};
                }

                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            var impl = createImpl(provider);
            assertThrows(IllegalArgumentException.class, () ->
                    impl.allocateComplementaryTickets(secretaryId, showId, List.of(seatId)));
        }
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("allocateComplementaryTickets - Returns tickets for valid COMPLIMENTARY seats")
        void allocate_ValidComplimentarySeats_ReturnsTickets() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));
            doNothing().when(persistence).saveComplementaryBookingWithTickets(
                    any(), any(), eq(secretaryId), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId)};
                if (sql.contains("seat")) return new MockResult[]{complimentarySeatResult(dsl, seatId, "T1")};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            var impl = createImpl(provider);
            List<Ticket> tickets = impl.allocateComplementaryTickets(secretaryId, showId, List.of(seatId));

            assertEquals(1, tickets.size());
            assertEquals(showId, tickets.get(0).getShowId());
            assertEquals(seatId, tickets.get(0).getSeatId());
            assertFalse(tickets.get(0).isRefunded());

            verify(persistence).saveComplementaryBookingWithTickets(
                    any(), eq(showId), eq(secretaryId), any());
        }

        @Test
        @DisplayName("allocateComplementaryTickets - Creates one ticket per seat ID")
        void allocate_MultipleSeatIds_CreatesOneTicketEach() {
            UUID seatId2 = UUID.randomUUID();
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));
            doNothing().when(persistence).saveComplementaryBookingWithTickets(
                    any(), any(), eq(secretaryId), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();

                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId)};

                if (sql.contains("seat")) {
                    var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                    for (UUID sid : List.of(seatId, seatId2)) {
                        var record = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                        record.set(SEAT.ID, sid);
                        record.set(SEAT.NUMBER, "T" + sid.toString().substring(0, 2));
                        record.set(SEAT.TYPE, Seattypeenum.ORDINARY);
                        record.set(SEAT.DESIGNATION, Seatdesignationenum.COMPLIMENTARY);
                        result.add(record);
                    }
                    return new MockResult[]{new MockResult(2, result)};
                }

                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            var impl = createImpl(provider);
            List<Ticket> tickets = impl.allocateComplementaryTickets(secretaryId, showId, List.of(seatId, seatId2));

            assertEquals(2, tickets.size());
        }
    }
}
