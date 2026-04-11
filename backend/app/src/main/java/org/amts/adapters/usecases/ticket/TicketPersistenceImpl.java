package org.amts.adapters.usecases.ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
import static org.amts.jooq.Tables.BOOKING;
import static org.amts.jooq.Tables.COUPON;
import static org.amts.jooq.Tables.OFFLINE_BOOKING;
import static org.amts.jooq.Tables.ONLINE_BOOKING;
import static org.amts.jooq.Tables.TICKET;
import static org.amts.jooq.Tables.TICKET_REFUND;
import org.jooq.DSLContext;

public class TicketPersistenceImpl implements TicketPersistenceUseCase {

    private final DSLContext dsl;

    public TicketPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Ticket> getTicketById(UUID ticketId) {
        var record = dsl.selectFrom(TICKET)
                .where(TICKET.ID.eq(ticketId))
                .fetchOne();

        if (record == null) return Optional.empty();

        return Optional.of(new Ticket(
                record.getId(),
                record.getBookingId(),
                record.getShowId(),
                record.getSeatId(),
                record.getCode(),
                record.getIsRefunded(),
                record.getCreatedAt()
        ));
    }

    @Override
    public List<Ticket> getTicketsByBookingId(UUID bookingId) {
        return dsl.selectFrom(TICKET)
                .where(TICKET.BOOKING_ID.eq(bookingId))
                .fetch()
                .map(record -> new Ticket(
                        record.getId(),
                        record.getBookingId(),
                        record.getShowId(),
                        record.getSeatId(),
                        record.getCode(),
                        record.getIsRefunded(),
                        record.getCreatedAt()
                ));
    }

    @Override
    public UUID fetchCouponId(UUID spectatorUserId, String couponCode) {
        var coupon = dsl.selectFrom(COUPON)
                .where(COUPON.CODE.eq(couponCode)
                        .and(COUPON.SPECTATOR_USER_ID.eq(spectatorUserId)))
                .fetchOne();

        return coupon != null ? coupon.getId() : null;
    }

    @Override
    public void saveOnlineBookingWithTickets(
            UUID bookingId,
            UUID showId,
            UUID spectatorUserId,
            UUID couponId,
            double total,
            List<Ticket> tickets
    ) {
        dsl.transaction(ctx -> {
            var tx = ctx.dsl();

            tx.insertInto(BOOKING)
                    .set(BOOKING.ID, bookingId)
                    .set(BOOKING.SHOW_ID, showId)
                    .set(BOOKING.CODE, UUID.randomUUID().toString())
                    .set(BOOKING.TYPE, org.amts.jooq.enums.Bookingtypeenum.ONLINE)
                    .set(BOOKING.AMOUNT, total)
                    .set(BOOKING.CREATED_AT, LocalDateTime.now())
                    .execute();

            tx.insertInto(ONLINE_BOOKING)
                    .set(ONLINE_BOOKING.ID, bookingId)
                    .set(ONLINE_BOOKING.SPECTATOR_USER_ID, spectatorUserId)
                    .set(ONLINE_BOOKING.COUPON_ID, couponId)
                    .execute();

            var queries = tickets.stream()
                    .map(ticket -> tx.insertInto(TICKET)
                            .set(TICKET.ID, ticket.getId())
                            .set(TICKET.BOOKING_ID, ticket.getBookingId())
                            .set(TICKET.SHOW_ID, ticket.getShowId())
                            .set(TICKET.SEAT_ID, ticket.getSeatId())
                            .set(TICKET.CODE, ticket.getCode())
                            .set(TICKET.IS_REFUNDED, ticket.isRefunded())
                            .set(TICKET.CREATED_AT, ticket.getCreatedAt()))
                    .toArray(org.jooq.Query[]::new);

            tx.batch(queries).execute();
        });
    }

    @Override
    public void saveOfflineBookingWithTickets(
            UUID bookingId,
            UUID showId,
            UUID spectatorUserId,
            UUID agentUserId,
            List<Ticket> tickets
    ) {
        dsl.transaction(ctx -> {
            var tx = ctx.dsl();

            tx.insertInto(BOOKING)
                    .set(BOOKING.ID, bookingId)
                    .set(BOOKING.SHOW_ID, showId)
                    .set(BOOKING.CODE, UUID.randomUUID().toString())
                    .set(BOOKING.TYPE, org.amts.jooq.enums.Bookingtypeenum.OFFLINE)
                    .set(BOOKING.CREATED_AT, LocalDateTime.now())
                    .execute();

            tx.insertInto(OFFLINE_BOOKING)
                    .set(OFFLINE_BOOKING.ID, bookingId)
                    .set(OFFLINE_BOOKING.SPECTATOR_USER_ID, spectatorUserId)
                    .set(OFFLINE_BOOKING.SALES_AGENT_USER_ID, agentUserId)
                    .execute();

            var queries = tickets.stream()
                    .map(ticket -> tx.insertInto(TICKET)
                            .set(TICKET.ID, ticket.getId())
                            .set(TICKET.BOOKING_ID, ticket.getBookingId())
                            .set(TICKET.SHOW_ID, ticket.getShowId())
                            .set(TICKET.SEAT_ID, ticket.getSeatId())
                            .set(TICKET.CODE, ticket.getCode())
                            .set(TICKET.IS_REFUNDED, ticket.isRefunded())
                            .set(TICKET.CREATED_AT, ticket.getCreatedAt()))
                    .toArray(org.jooq.Query[]::new);

            tx.batch(queries).execute();
        });
    }

    @Override
    public String saveCoupon(UUID spectatorUserId, UUID showId) {
        String code = String.valueOf((long) (Math.random() * 1_000_000_0000L));

        dsl.transaction(ctx -> {
            ctx.dsl().insertInto(COUPON)
                    .set(COUPON.ID, UUID.randomUUID())
                    .set(COUPON.SHOW_ID, showId)
                    .set(COUPON.SPECTATOR_USER_ID, spectatorUserId)
                    .set(COUPON.CODE, code)
                    .set(COUPON.CREATED_AT, LocalDateTime.now())
                    .execute();
        });

        return code;
    }

    @Override
    public void saveTickets(List<Ticket> tickets) {
        var queries = tickets.stream()
                .map(ticket -> dsl.insertInto(TICKET)
                        .set(TICKET.ID, ticket.getId())
                        .set(TICKET.BOOKING_ID, ticket.getBookingId())
                        .set(TICKET.SHOW_ID, ticket.getShowId())
                        .set(TICKET.SEAT_ID, ticket.getSeatId())
                        .set(TICKET.CODE, ticket.getCode())
                        .set(TICKET.IS_REFUNDED, ticket.isRefunded())
                        .set(TICKET.CREATED_AT, ticket.getCreatedAt()))
                .toArray(org.jooq.Query[]::new);

        dsl.batch(queries).execute();
    }

    @Override
    public void markTicketAsRefunded(UUID ticketId) {
        dsl.update(TICKET)
                .set(TICKET.IS_REFUNDED, true)
                .where(TICKET.ID.eq(ticketId))
                .execute();
    }

    @Override
    public void saveRefundAndMarkTicket(UUID ticketId, TicketRefund refund) {
        dsl.transaction(ctx -> {
            var tx = ctx.dsl();

            tx.update(TICKET)
                    .set(TICKET.IS_REFUNDED, true)
                    .where(TICKET.ID.eq(ticketId))
                    .execute();

            tx.insertInto(TICKET_REFUND)
                    .set(TICKET_REFUND.TICKET_ID, refund.getTicketId())
                    .set(TICKET_REFUND.TYPE,
                            org.amts.jooq.enums.Refundtype.valueOf(refund.getType().name()))
                    .set(TICKET_REFUND.CREATED_AT, refund.getCreatedAt())
                    .execute();
        });
    }

    @Override
    public void saveTicketRefund(TicketRefund refund) {
        dsl.insertInto(TICKET_REFUND)
                .set(TICKET_REFUND.TICKET_ID, refund.getTicketId())
                .set(TICKET_REFUND.TYPE,
                        org.amts.jooq.enums.Refundtype.valueOf(refund.getType().name()))
                .set(TICKET_REFUND.CREATED_AT, refund.getCreatedAt())
                .execute();
    }
}