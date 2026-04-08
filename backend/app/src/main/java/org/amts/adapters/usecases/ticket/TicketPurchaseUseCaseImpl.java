package org.amts.adapters.usecase.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.ticket.TicketPurchaseUseCase;
import org.amts.domain.entities.ticket.RefundType;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
import static org.amts.jooq.Tables.BOOKING;
import static org.amts.jooq.Tables.COUPON;
import static org.amts.jooq.Tables.OFFLINE_BOOKING;
import static org.amts.jooq.Tables.ONLINE_BOOKING;
import static org.amts.jooq.Tables.SHOW;
import org.jooq.DSLContext;

public class TicketPurchaseUseCaseImpl implements TicketPurchaseUseCase {

    private final TicketPersistenceUseCase persistence;
    private final DSLContext dsl;

    public TicketPurchaseUseCaseImpl(DSLContext dsl,
                                    TicketPersistenceUseCase persistence) {
        this.dsl = dsl;
        this.persistence = persistence;
    }

    @Override
    public List<Ticket> purchaseTickets(UUID spectatorUserId, UUID showId,
                                        List<UUID> seatIds, String couponCode) {

        // 1. Get show pricing
        var show = dsl.selectFrom(SHOW)
                .where(SHOW.ID.eq(showId))
                .fetchOne();

        double seatPrice = show.getOrdinarySeatPrice(); // simplified
        double total = seatPrice * seatIds.size();

        // 2. Apply coupon if exists
        UUID couponId = null;
        if (couponCode != null) {
            var coupon = dsl.selectFrom(COUPON)
                    .where(COUPON.CODE.eq(couponCode)
                            .and(COUPON.SPECTATOR_USER_ID.eq(spectatorUserId)))
                    .fetchOne();

            if (coupon != null) {
                total *= 0.9;
                couponId = coupon.getId();
            }
        }

        // 3. Create booking
        UUID bookingId = UUID.randomUUID();

        dsl.insertInto(BOOKING)
                .set(BOOKING.ID, bookingId)
                .set(BOOKING.SHOW_ID, showId)
                .set(BOOKING.CODE, UUID.randomUUID().toString())
                .set(BOOKING.TYPE, org.amts.jooq.enums.Bookingtypeenum.ONLINE)
                .set(BOOKING.AMOUNT, total)
                .set(BOOKING.CREATED_AT, LocalDateTime.now())
                .execute();

        // 4. Online booking entry
        dsl.insertInto(ONLINE_BOOKING)
                .set(ONLINE_BOOKING.ID, bookingId)
                .set(ONLINE_BOOKING.SPECTATOR_USER_ID, spectatorUserId)
                .set(ONLINE_BOOKING.COUPON_ID, couponId)
                .execute();

        // 5. Generate tickets
        List<Ticket> tickets = generateTickets(bookingId, showId, seatIds);
        persistence.saveTickets(tickets);

        return tickets;
    }

    @Override
    public String purchaseCoupon(UUID spectatorUserId, UUID showId) {

        String code = generateCouponCode();
        UUID id = UUID.randomUUID();

        dsl.insertInto(COUPON)
                .set(COUPON.ID, id)
                .set(COUPON.SHOW_ID, showId)
                .set(COUPON.SPECTATOR_USER_ID, spectatorUserId)
                .set(COUPON.CODE, code)
                .set(COUPON.CREATED_AT, LocalDateTime.now())
                .execute();

        return code;
    }

    @Override
    public List<Ticket> purchaseTicketsViaAgent(UUID agentUserId,
                                                UUID spectatorUserId,
                                                UUID showId,
                                                List<UUID> seatIds) {

        UUID bookingId = UUID.randomUUID();

        // Create booking
        dsl.insertInto(BOOKING)
                .set(BOOKING.ID, bookingId)
                .set(BOOKING.SHOW_ID, showId)
                .set(BOOKING.CODE, UUID.randomUUID().toString())
                .set(BOOKING.TYPE, org.amts.jooq.enums.Bookingtypeenum.OFFLINE)
                .set(BOOKING.CREATED_AT, LocalDateTime.now())
                .execute();

        // Offline booking entry (agent tracking)
        dsl.insertInto(OFFLINE_BOOKING)
                .set(OFFLINE_BOOKING.ID, bookingId)
                .set(OFFLINE_BOOKING.SPECTATOR_USER_ID, spectatorUserId)
                .set(OFFLINE_BOOKING.SALES_AGENT_USER_ID, agentUserId)
                .execute();

        List<Ticket> tickets = generateTickets(bookingId, showId, seatIds);
        persistence.saveTickets(tickets);

        return tickets;
    }

    @Override
    public double cancelTickets(UUID spectatorUserId, List<UUID> ticketIds) {

        double totalRefund = 0;

        for (UUID ticketId : ticketIds) {

            var ticket = persistence.getTicketById(ticketId).orElseThrow();

            var show = dsl.selectFrom(SHOW)
                    .where(SHOW.ID.eq(ticket.getShowId()))
                    .fetchOne();

            long hoursBeforeShow = Duration.between(
                    LocalDateTime.now(),
                    show.getStartingAt()
            ).toHours();

            double price = show.getOrdinarySeatPrice(); // simplified
            double refund;
            RefundType type;

            if (hoursBeforeShow > 72) {
                refund = price - 5;
                type = RefundType.BEFORE_THREE_DAYS;
            } else if (hoursBeforeShow > 24) {
                refund = price - 10;
                type = RefundType.BEFORE_ONE_DAY;
            } else {
                refund = price * 0.5;
                type = RefundType.SAME_DAY;
            }

            totalRefund += refund;

            persistence.markTicketAsRefunded(ticketId);
            persistence.saveTicketRefund(
                    new TicketRefund(ticketId, type, LocalDateTime.now())
            );
        }

        return totalRefund;
    }


    private List<Ticket> generateTickets(UUID bookingId,
                                         UUID showId,
                                         List<UUID> seatIds) {

        List<Ticket> tickets = new ArrayList<>();

        for (UUID seatId : seatIds) {
            tickets.add(new Ticket(
                    UUID.randomUUID(),
                    bookingId,
                    showId,
                    seatId,
                    UUID.randomUUID().toString().substring(0, 8),
                    false,
                    LocalDateTime.now()
            ));
        }

        return tickets;
    }

    private String generateCouponCode() {
        return String.valueOf((long) (Math.random() * 1_000_000_0000L));
    }
}