package org.amts.adapters.usecases.ticket;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.amts.jooq.Tables.SEAT;
import static org.amts.jooq.Tables.SHOW;
import static org.amts.jooq.Tables.TICKET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketPurchaseImpl Tests")
class TicketPurchaseImplTest {

    @Mock
    private TicketPersistenceUseCase persistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    private UUID spectatorId;
    private UUID agentId;
    private UUID showId;
    private UUID seatId;
    private User spectatorUser;
    private User agentUser;

    @BeforeEach
    void setUp() {
        spectatorId = UUID.randomUUID();
        agentId = UUID.randomUUID();
        showId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
        agentUser = new User(agentId, "agent@test.com", Set.of(Role.SALES_AGENT), LocalDateTime.now());
    }

    private TicketPurchaseImpl createImpl(MockDataProvider provider) {
        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        return new TicketPurchaseImpl(mockDsl, persistence, userPersistence);
    }

    /** Builds a full MockResult for SHOW with all 13 columns. */
    private MockResult showResult(DSLContext dsl, UUID showId, double ordinaryPrice, double balconyPrice,
            LocalDateTime startingAt) {
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
        record.set(SHOW.STARTING_AT, startingAt);
        record.set(SHOW.ENDING_AT, startingAt.plusHours(3));
        record.set(SHOW.ORDINARY_SEAT_PRICE, ordinaryPrice);
        record.set(SHOW.BALCONY_SEAT_PRICE, balconyPrice);
        record.set(SHOW.NUM_ORDINARY_SEATS, 600);
        record.set(SHOW.NUM_BALCONY_SEATS, 200);
        record.set(SHOW.CREATED_AT, LocalDateTime.now());
        result.add(record);
        return new MockResult(1, result);
    }

    /** Builds a MockResult for SEAT with given type and designation. */
    private MockResult seatResult(DSLContext dsl, UUID seatId, Seattypeenum type, Seatdesignationenum designation) {
        var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
        var record = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
        record.set(SEAT.ID, seatId);
        record.set(SEAT.NUMBER, "A1");
        record.set(SEAT.TYPE, type);
        record.set(SEAT.DESIGNATION, designation);
        result.add(record);
        return new MockResult(1, result);
    }

    /** Builds a MockResult for TICKET (used in cancelTickets). */
    private MockResult ticketResult(DSLContext dsl, UUID ticketId, UUID showId, UUID seatId) {
        var result = dsl.newResult(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID, TICKET.SEAT_ID,
                TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
        var record = dsl.newRecord(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID, TICKET.SEAT_ID,
                TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
        record.set(TICKET.ID, ticketId);
        record.set(TICKET.BOOKING_ID, UUID.randomUUID());
        record.set(TICKET.SHOW_ID, showId);
        record.set(TICKET.SEAT_ID, seatId);
        record.set(TICKET.CODE, "ABCD1234");
        record.set(TICKET.IS_REFUNDED, false);
        record.set(TICKET.CREATED_AT, LocalDateTime.now());
        result.add(record);
        return new MockResult(1, result);
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("purchaseTickets()")
    class PurchaseTicketsTests {

        @Test
        @DisplayName("Throws UserNotFoundException when spectator does not exist")
        void purchaseTickets_SpectatorNotFound_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(UserNotFoundException.class, () ->
                    impl.purchaseTickets(spectatorId, showId, List.of(seatId), null));
        }

        @Test
        @DisplayName("Throws PermissionException when user does not have SPECTATOR role")
        void purchaseTickets_NotSpectatorRole_Throws() {
            User nonSpectator = new User(spectatorId, "x@test.com", Set.of(Role.SALES_AGENT), LocalDateTime.now());
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(nonSpectator));
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(PermissionException.class, () ->
                    impl.purchaseTickets(spectatorId, showId, List.of(seatId), null));
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when a VIP seat is included")
        void purchaseTickets_VipSeat_ThrowsIllegalArgument() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 20.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.BALCONY, Seatdesignationenum.VIP)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            assertThrows(IllegalArgumentException.class, () ->
                    createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), null));
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when a COMPLIMENTARY seat is included")
        void purchaseTickets_ComplimentarySeat_ThrowsIllegalArgument() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 20.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.COMPLIMENTARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            assertThrows(IllegalArgumentException.class, () ->
                    createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), null));
        }

        @Test
        @DisplayName("Returns tickets and calls persistence for valid ORDINARY seats")
        void purchaseTickets_OrdinarySeats_ReturnsTickets() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            doNothing().when(persistence).saveOnlineBookingWithTickets(any(), any(), any(), any(), anyDouble(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 20.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            List<Ticket> tickets = createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), null);

            assertEquals(1, tickets.size());
            assertEquals(seatId, tickets.get(0).getSeatId());
            verify(persistence).saveOnlineBookingWithTickets(any(), eq(showId), eq(spectatorId), isNull(), anyDouble(), any());
        }

        @Test
        @DisplayName("Applies 10% coupon discount when valid coupon code is provided")
        void purchaseTickets_WithCoupon_AppliesDiscount() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID couponId = UUID.randomUUID();
            when(persistence.fetchCouponId(spectatorId, "SAVE10")).thenReturn(couponId);
            doNothing().when(persistence).saveOnlineBookingWithTickets(any(), any(), any(), any(), anyDouble(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 100.0, 200.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), "SAVE10");

            // ordinary price = 100, with 10% discount = 90
            ArgumentCaptor<Double> totalCaptor = ArgumentCaptor.forClass(Double.class);
            verify(persistence).saveOnlineBookingWithTickets(any(), any(), any(), eq(couponId), totalCaptor.capture(), any());
            assertEquals(90.0, totalCaptor.getValue(), 0.001);
        }

        @Test
        @DisplayName("Does not apply discount when coupon code is null")
        void purchaseTickets_NullCoupon_NoDiscount() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            doNothing().when(persistence).saveOnlineBookingWithTickets(any(), any(), any(), any(), anyDouble(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 100.0, 200.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), null);

            ArgumentCaptor<Double> totalCaptor = ArgumentCaptor.forClass(Double.class);
            verify(persistence).saveOnlineBookingWithTickets(any(), any(), any(), isNull(), totalCaptor.capture(), any());
            assertEquals(100.0, totalCaptor.getValue(), 0.001);
        }

        @Test
        @DisplayName("Uses balcony seat price for BALCONY seats")
        void purchaseTickets_BalconySeat_UsesBalconyRate() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            doNothing().when(persistence).saveOnlineBookingWithTickets(any(), any(), any(), any(), anyDouble(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 50.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.BALCONY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).purchaseTickets(spectatorId, showId, List.of(seatId), null);

            ArgumentCaptor<Double> totalCaptor = ArgumentCaptor.forClass(Double.class);
            verify(persistence).saveOnlineBookingWithTickets(any(), any(), any(), isNull(), totalCaptor.capture(), any());
            assertEquals(50.0, totalCaptor.getValue(), 0.001);
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("purchaseCoupon()")
    class PurchaseCouponTests {

        @Test
        @DisplayName("Returns generated coupon code for spectator")
        void purchaseCoupon_Spectator_ReturnsCode() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(persistence.saveCoupon(spectatorId, showId)).thenReturn("1234567890");

            String code = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())})
                    .purchaseCoupon(spectatorId, showId);

            assertEquals("1234567890", code);
            verify(persistence).saveCoupon(spectatorId, showId);
        }

        @Test
        @DisplayName("Throws UserNotFoundException when spectator does not exist")
        void purchaseCoupon_MissingUser_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())})
                            .purchaseCoupon(spectatorId, showId));
        }

        @Test
        @DisplayName("Throws PermissionException when user is not a spectator")
        void purchaseCoupon_NotSpectator_Throws() {
            User nonSpectator = new User(spectatorId, "x@test.com", Set.of(Role.SALES_AGENT), LocalDateTime.now());
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(nonSpectator));

            assertThrows(PermissionException.class, () ->
                    createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())})
                            .purchaseCoupon(spectatorId, showId));

            verify(persistence, never()).saveCoupon(any(), any());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("purchaseTicketsViaAgent()")
    class PurchaseTicketsViaAgentTests {

        @Test
        @DisplayName("Throws UserNotFoundException when spectator does not exist")
        void viaAgent_SpectatorNotFound_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(UserNotFoundException.class, () ->
                    impl.purchaseTicketsViaAgent(agentId, spectatorId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("Throws UserNotFoundException when agent does not exist")
        void viaAgent_AgentNotFound_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.empty());
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(UserNotFoundException.class, () ->
                    impl.purchaseTicketsViaAgent(agentId, spectatorId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("Throws PermissionException when agent lacks SALES_AGENT role")
        void viaAgent_NotSalesAgent_Throws() {
            User nonAgent = new User(agentId, "x@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(nonAgent));
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(PermissionException.class, () ->
                    impl.purchaseTicketsViaAgent(agentId, spectatorId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when a VIP seat is included")
        void viaAgent_VipSeat_ThrowsIllegalArgument() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 20.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.BALCONY, Seatdesignationenum.VIP)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            assertThrows(IllegalArgumentException.class, () ->
                    createImpl(provider).purchaseTicketsViaAgent(agentId, spectatorId, showId, List.of(seatId)));
        }

        @Test
        @DisplayName("Returns tickets and calls persistence for valid seats")
        void viaAgent_ValidSeats_ReturnsTickets() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));
            doNothing().when(persistence).saveOfflineBookingWithTickets(any(), any(), any(), any(), anyDouble(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 10.0, 20.0, LocalDateTime.now().plusDays(7))};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            List<Ticket> tickets = createImpl(provider).purchaseTicketsViaAgent(agentId, spectatorId, showId, List.of(seatId));

            assertEquals(1, tickets.size());
            verify(persistence).saveOfflineBookingWithTickets(any(), eq(showId), eq(spectatorId), eq(agentId), anyDouble(), any());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("cancelTickets()")
    class CancelTicketsTests {

        @Test
        @DisplayName("Throws UserNotFoundException when spectator does not exist")
        void cancelTickets_SpectatorNotFound_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());
            var impl = createImpl(ctx -> new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())});

            assertThrows(UserNotFoundException.class, () ->
                    impl.cancelTickets(spectatorId, List.of(UUID.randomUUID())));
        }

        @Test
        @DisplayName("Refund = price - 5 when show is more than 72 hours away")
        void cancelTickets_Before72Hours_RefundsFullMinusFive() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusDays(5); // >72h

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            double refund = createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));
            assertEquals(25.0, refund, 0.001); // 30 - 5
        }

        @Test
        @DisplayName("Refund = price - 10 for ordinary seat between 24 and 72 hours before show")
        void cancelTickets_Between24And72Hours_Ordinary_RefundsMinus10() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusHours(48); // 24-72h

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            double refund = createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));
            assertEquals(20.0, refund, 0.001); // 30 - 10
        }

        @Test
        @DisplayName("Refund = price - 15 for balcony seat between 24 and 72 hours before show")
        void cancelTickets_Between24And72Hours_Balcony_RefundsMinus15() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusHours(48); // 24-72h

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.BALCONY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            double refund = createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));
            assertEquals(35.0, refund, 0.001); // 50 - 15
        }

        @Test
        @DisplayName("Refund = 50% of price when show is less than 24 hours away")
        void cancelTickets_SameDay_RefundsHalfPrice() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusHours(10); // <24h

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            double refund = createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));
            assertEquals(15.0, refund, 0.001); // 30 * 0.5
        }

        @Test
        @DisplayName("Sums refunds across multiple tickets")
        void cancelTickets_MultipleTickets_SumsTotalRefund() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId1 = UUID.randomUUID();
            UUID ticketId2 = UUID.randomUUID();
            UUID seatId2 = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusDays(5); // >72h — price - 5 each

            when(persistence.getTicketById(ticketId1)).thenReturn(Optional.of(
                    new Ticket(ticketId1, UUID.randomUUID(), showId, seatId, "CODE0001", false, LocalDateTime.now())));
            when(persistence.getTicketById(ticketId2)).thenReturn(Optional.of(
                    new Ticket(ticketId2, UUID.randomUUID(), showId, seatId2, "CODE0002", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(any())).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 20.0, 40.0, showTime)};
                if (sql.contains("seat")) {
                    var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                    var rec = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION);
                    rec.set(SEAT.ID, seatId);
                    rec.set(SEAT.NUMBER, "A1");
                    rec.set(SEAT.TYPE, Seattypeenum.ORDINARY);
                    rec.set(SEAT.DESIGNATION, Seatdesignationenum.ORDINARY);
                    result.add(rec);
                    return new MockResult[]{new MockResult(1, result)};
                }
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            double total = createImpl(provider).cancelTickets(spectatorId, List.of(ticketId1, ticketId2));
            assertEquals(30.0, total, 0.001); // (20-5) + (20-5)
        }

        @Test
        @DisplayName("Calls saveRefundAndMarkTicketWithSeatRelease for each cancelled ticket")
        void cancelTickets_CallsSaveRefundAndMarkTicketWithSeatRelease() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusDays(5);

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));

            verify(persistence).saveRefundAndMarkTicketWithSeatRelease(eq(ticketId), any(TicketRefund.class), isNull());
        }

        @Test
        @DisplayName("Passes null agent to persistence for an online booking cancellation")
        void cancelTickets_OnlineBooking_PassesNullAgentToPersistence() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusDays(5);

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(null);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));

            verify(persistence).saveRefundAndMarkTicketWithSeatRelease(eq(ticketId), any(TicketRefund.class), isNull());
        }

        @Test
        @DisplayName("Passes agent UUID to persistence for an offline booking cancellation")
        void cancelTickets_OfflineBooking_PassesAgentIdToPersistence() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            UUID ticketId = UUID.randomUUID();
            UUID expectedAgentId = UUID.randomUUID();
            LocalDateTime showTime = LocalDateTime.now().plusDays(5);

            when(persistence.getTicketById(ticketId)).thenReturn(Optional.of(
                    new Ticket(ticketId, UUID.randomUUID(), showId, seatId, "ABCD1234", false, LocalDateTime.now())));
            when(persistence.getAgentForTicket(ticketId)).thenReturn(expectedAgentId);
            doNothing().when(persistence).saveRefundAndMarkTicketWithSeatRelease(any(), any(), any());

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("show")) return new MockResult[]{showResult(dsl, showId, 30.0, 50.0, showTime)};
                if (sql.contains("seat")) return new MockResult[]{seatResult(dsl, seatId, Seattypeenum.ORDINARY, Seatdesignationenum.ORDINARY)};
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            };

            createImpl(provider).cancelTickets(spectatorId, List.of(ticketId));

            verify(persistence).saveRefundAndMarkTicketWithSeatRelease(eq(ticketId), any(TicketRefund.class), eq(expectedAgentId));
        }
    }
}
