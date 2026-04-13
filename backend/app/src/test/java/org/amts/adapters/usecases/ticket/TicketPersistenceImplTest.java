package org.amts.adapters.usecases.ticket;

import org.amts.domain.entities.ticket.RefundType;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
import org.amts.jooq.enums.Bookingtypeenum;
import org.amts.jooq.enums.Refundtype;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.amts.jooq.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TicketPersistenceImpl Tests")
class TicketPersistenceImplTest {

    private TicketPersistenceImpl createPersistence(MockDataProvider provider) {
        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        return new TicketPersistenceImpl(mockDsl);
    }

    /** Builds a ticket domain object for use in tests. */
    private Ticket aTicket(UUID ticketId, UUID bookingId, UUID showId, UUID seatId) {
        return new Ticket(ticketId, bookingId, showId, seatId, "CODE1234", false, LocalDateTime.now());
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ticket Retrieval")
    class TicketRetrievalTests {

        @Test
        @DisplayName("getTicketById - Returns populated Optional when ticket exists")
        void getTicketById_Found_ReturnsTicket() {
            UUID ticketId = UUID.randomUUID();
            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            UUID seatId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID,
                        TICKET.SEAT_ID, TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
                var record = dsl.newRecord(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID,
                        TICKET.SEAT_ID, TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
                record.set(TICKET.ID, ticketId);
                record.set(TICKET.BOOKING_ID, bookingId);
                record.set(TICKET.SHOW_ID, showId);
                record.set(TICKET.SEAT_ID, seatId);
                record.set(TICKET.CODE, "CODE1234");
                record.set(TICKET.IS_REFUNDED, false);
                record.set(TICKET.CREATED_AT, createdAt);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            };

            Optional<Ticket> result = createPersistence(provider).getTicketById(ticketId);

            assertTrue(result.isPresent());
            assertAll(
                    () -> assertEquals(ticketId, result.get().getId()),
                    () -> assertEquals(bookingId, result.get().getBookingId()),
                    () -> assertEquals(showId, result.get().getShowId()),
                    () -> assertEquals(seatId, result.get().getSeatId()),
                    () -> assertEquals("CODE1234", result.get().getCode()),
                    () -> assertFalse(result.get().isRefunded())
            );
        }

        @Test
        @DisplayName("getTicketById - Returns empty Optional when ticket does not exist")
        void getTicketById_NotFound_ReturnsEmpty() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID,
                        TICKET.SEAT_ID, TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
                return new MockResult[]{new MockResult(0, result)};
            };

            Optional<Ticket> result = createPersistence(provider).getTicketById(UUID.randomUUID());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getTicketsByBookingId - Returns all tickets for a booking, correctly mapped")
        void getTicketsByBookingId_ReturnsList() {
            UUID bookingId = UUID.randomUUID();
            UUID ticketId1 = UUID.randomUUID();
            UUID ticketId2 = UUID.randomUUID();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID,
                        TICKET.SEAT_ID, TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
                for (UUID tid : List.of(ticketId1, ticketId2)) {
                    var record = dsl.newRecord(TICKET.ID, TICKET.BOOKING_ID, TICKET.SHOW_ID,
                            TICKET.SEAT_ID, TICKET.CODE, TICKET.IS_REFUNDED, TICKET.CREATED_AT);
                    record.set(TICKET.ID, tid);
                    record.set(TICKET.BOOKING_ID, bookingId);
                    record.set(TICKET.SHOW_ID, UUID.randomUUID());
                    record.set(TICKET.SEAT_ID, UUID.randomUUID());
                    record.set(TICKET.CODE, "C" + tid.toString().substring(0, 4));
                    record.set(TICKET.IS_REFUNDED, false);
                    record.set(TICKET.CREATED_AT, LocalDateTime.now());
                    result.add(record);
                }
                return new MockResult[]{new MockResult(2, result)};
            };

            List<Ticket> tickets = createPersistence(provider).getTicketsByBookingId(bookingId);
            assertEquals(2, tickets.size());
            assertTrue(tickets.stream().allMatch(t -> bookingId.equals(t.getBookingId())));
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Online Booking Persistence")
    class OnlineBookingTests {

        @Test
        @DisplayName("saveOnlineBookingWithTickets - Issues inserts for BOOKING, ONLINE_BOOKING, and TICKET batch")
        void saveOnlineBookingWithTickets_ExecutesTransaction() {
            var capturedTables = new ArrayList<String>();

            MockDataProvider provider = ctx -> {
                capturedTables.add(ctx.sql().toLowerCase());
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            UUID spectatorId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveOnlineBookingWithTickets(
                    bookingId, showId, spectatorId, null, 99.0, List.of(ticket));

            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("booking")),
                    "Should insert into booking");
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("online_booking")),
                    "Should insert into online_booking");
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("ticket")),
                    "Should insert into ticket");
        }

        @Test
        @DisplayName("saveOnlineBookingWithTickets - Sets booking type to ONLINE")
        void saveOnlineBookingWithTickets_SetsBookingTypeOnline() {
            var capturedBookingType = new AtomicReference<String>();

            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("insert") && sql.contains("booking") && !sql.contains("online_booking") && !sql.contains("ticket")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof Bookingtypeenum e) {
                                capturedBookingType.set(e.name());
                            } else if (b instanceof String s && (s.equals("ONLINE") || s.equals("OFFLINE") || s.equals("COMPLEMENTARY"))) {
                                capturedBookingType.set(s);
                            }
                        }
                    }
                }
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveOnlineBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), null, 50.0, List.of(ticket));

            assertEquals("ONLINE", capturedBookingType.get());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Offline Booking Persistence")
    class OfflineBookingTests {

        @Test
        @DisplayName("saveOfflineBookingWithTickets - Issues inserts for BOOKING, OFFLINE_BOOKING, and TICKET batch")
        void saveOfflineBookingWithTickets_ExecutesTransaction() {
            var capturedTables = new ArrayList<String>();

            MockDataProvider provider = ctx -> {
                capturedTables.add(ctx.sql().toLowerCase());
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveOfflineBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), UUID.randomUUID(), 30.0, List.of(ticket));

            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("booking")));
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("offline_booking")));
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("ticket")));
        }

        @Test
        @DisplayName("saveOfflineBookingWithTickets - Sets booking type to OFFLINE")
        void saveOfflineBookingWithTickets_SetsBookingTypeOffline() {
            var capturedBookingType = new AtomicReference<String>();

            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("insert") && sql.contains("booking") && !sql.contains("offline_booking") && !sql.contains("ticket")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof Bookingtypeenum e) {
                                capturedBookingType.set(e.name());
                            } else if (b instanceof String s && (s.equals("ONLINE") || s.equals("OFFLINE") || s.equals("COMPLEMENTARY"))) {
                                capturedBookingType.set(s);
                            }
                        }
                    }
                }
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveOfflineBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), UUID.randomUUID(), 30.0, List.of(ticket));

            assertEquals("OFFLINE", capturedBookingType.get());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Complementary Booking Persistence")
    class ComplementaryBookingTests {

        @Test
        @DisplayName("saveComplementaryBookingWithTickets - Issues inserts for BOOKING, COMPLEMENTARY_BOOKING, and TICKET batch")
        void saveComplementaryBookingWithTickets_ExecutesTransaction() {
            var capturedTables = new ArrayList<String>();

            MockDataProvider provider = ctx -> {
                capturedTables.add(ctx.sql().toLowerCase());
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveComplementaryBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), List.of(ticket));

            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("booking")));
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("complementary_booking")));
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("ticket")));
        }

        @Test
        @DisplayName("saveComplementaryBookingWithTickets - Sets booking amount to 0.0")
        void saveComplementaryBookingWithTickets_SetsAmountZero() {
            var capturedAmount = new AtomicReference<Double>();

            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("insert") && sql.contains("booking")
                        && !sql.contains("complementary_booking") && !sql.contains("ticket")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof Double d) {
                                capturedAmount.set(d);
                            }
                        }
                    }
                }
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveComplementaryBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), List.of(ticket));

            assertNotNull(capturedAmount.get(), "Amount binding should have been captured");
            assertEquals(0.0, capturedAmount.get(), 0.001);
        }

        @Test
        @DisplayName("saveComplementaryBookingWithTickets - Sets booking type to COMPLEMENTARY")
        void saveComplementaryBookingWithTickets_SetsBookingTypeComplementary() {
            var capturedBookingType = new AtomicReference<String>();

            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("insert") && sql.contains("booking")
                        && !sql.contains("complementary_booking") && !sql.contains("ticket")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof Bookingtypeenum e) {
                                capturedBookingType.set(e.name());
                            } else if (b instanceof String s && (s.equals("ONLINE") || s.equals("OFFLINE") || s.equals("COMPLEMENTARY"))) {
                                capturedBookingType.set(s);
                            }
                        }
                    }
                }
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveComplementaryBookingWithTickets(
                    bookingId, showId, UUID.randomUUID(), List.of(ticket));

            assertEquals("COMPLEMENTARY", capturedBookingType.get());
        }

        @Test
        @DisplayName("saveComplementaryBookingWithTickets - Passes createdByUserId to COMPLEMENTARY_BOOKING insert")
        void saveComplementaryBookingWithTickets_SetsCreatedByUserId() {
            UUID secretaryId = UUID.randomUUID();
            var capturedUserId = new AtomicReference<UUID>();

            MockDataProvider provider = ctx -> {
                String sql = ctx.sql().toLowerCase();
                if (sql.contains("complementary_booking")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof UUID u && u.equals(secretaryId)) {
                                capturedUserId.set(u);
                            }
                        }
                    }
                }
                int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
                MockResult[] results = new MockResult[rows];
                for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
                return results;
            };

            UUID bookingId = UUID.randomUUID();
            UUID showId = UUID.randomUUID();
            Ticket ticket = aTicket(UUID.randomUUID(), bookingId, showId, UUID.randomUUID());

            createPersistence(provider).saveComplementaryBookingWithTickets(
                    bookingId, showId, secretaryId, List.of(ticket));

            assertEquals(secretaryId, capturedUserId.get());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Coupon Management")
    class CouponTests {

        @Test
        @DisplayName("fetchCouponId - Returns coupon UUID when matching row exists")
        void fetchCouponId_Found_ReturnsCouponId() {
            UUID couponId = UUID.randomUUID();
            UUID spectatorId = UUID.randomUUID();

            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(COUPON.ID, COUPON.SHOW_ID, COUPON.SPECTATOR_USER_ID,
                        COUPON.CODE, COUPON.CREATED_AT);
                var record = dsl.newRecord(COUPON.ID, COUPON.SHOW_ID, COUPON.SPECTATOR_USER_ID,
                        COUPON.CODE, COUPON.CREATED_AT);
                record.set(COUPON.ID, couponId);
                record.set(COUPON.SHOW_ID, UUID.randomUUID());
                record.set(COUPON.SPECTATOR_USER_ID, spectatorId);
                record.set(COUPON.CODE, "SAVE10");
                record.set(COUPON.CREATED_AT, LocalDateTime.now());
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            };

            UUID result = createPersistence(provider).fetchCouponId(spectatorId, "SAVE10");
            assertEquals(couponId, result);
        }

        @Test
        @DisplayName("fetchCouponId - Returns null when no matching coupon")
        void fetchCouponId_NotFound_ReturnsNull() {
            MockDataProvider provider = ctx -> {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                var result = dsl.newResult(COUPON.ID, COUPON.SHOW_ID, COUPON.SPECTATOR_USER_ID,
                        COUPON.CODE, COUPON.CREATED_AT);
                return new MockResult[]{new MockResult(0, result)};
            };

            UUID result = createPersistence(provider).fetchCouponId(UUID.randomUUID(), "INVALID");
            assertNull(result);
        }

        @Test
        @DisplayName("saveCoupon - Returns a non-null, non-blank coupon code")
        void saveCoupon_ReturnsCode() {
            MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};

            String code = createPersistence(provider).saveCoupon(UUID.randomUUID(), UUID.randomUUID());

            assertNotNull(code);
            assertFalse(code.isBlank());
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Refund Handling")
    class RefundTests {

        @Test
        @DisplayName("saveRefundAndMarkTicket - Issues UPDATE on ticket and INSERT into ticket_refund")
        void saveRefundAndMarkTicket_ExecutesTransaction() {
            var capturedTables = new ArrayList<String>();

            MockDataProvider provider = ctx -> {
                capturedTables.add(ctx.sql().toLowerCase());
                return new MockResult[]{new MockResult(1, null)};
            };

            UUID ticketId = UUID.randomUUID();
            TicketRefund refund = new TicketRefund(ticketId, RefundType.BEFORE_THREE_DAYS, LocalDateTime.now());

            createPersistence(provider).saveRefundAndMarkTicket(ticketId, refund);

            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("update") && s.contains("ticket")),
                    "Should issue UPDATE on ticket");
            assertTrue(capturedTables.stream().anyMatch(s -> s.contains("ticket_refund")),
                    "Should insert into ticket_refund");
        }

        @Test
        @DisplayName("saveRefundAndMarkTicket - Passes correct refund type to ticket_refund insert")
        void saveRefundAndMarkTicket_SetsCorrectRefundType() {
            var capturedRefundType = new AtomicReference<String>();

            MockDataProvider provider = ctx -> {
                if (ctx.sql().toLowerCase().contains("ticket_refund")) {
                    Object[] bindings = ctx.bindings();
                    if (bindings != null) {
                        for (Object b : bindings) {
                            if (b instanceof Refundtype e) {
                                capturedRefundType.set(e.name());
                            } else if (b instanceof String s && (s.equals("BEFORE_THREE_DAYS") || s.equals("BEFORE_ONE_DAY") || s.equals("SAME_DAY"))) {
                                capturedRefundType.set(s);
                            }
                        }
                    }
                }
                return new MockResult[]{new MockResult(1, null)};
            };

            UUID ticketId = UUID.randomUUID();
            TicketRefund refund = new TicketRefund(ticketId, RefundType.SAME_DAY, LocalDateTime.now());

            createPersistence(provider).saveRefundAndMarkTicket(ticketId, refund);

            assertEquals("SAME_DAY", capturedRefundType.get());
        }
    }
}
