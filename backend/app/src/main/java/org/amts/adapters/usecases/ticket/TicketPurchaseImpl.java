package org.amts.adapters.usecases.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.ticket.TicketPurchaseUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.ticket.RefundType;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
import org.amts.domain.entities.user.Role;
import static org.amts.jooq.Tables.SHOW;
import org.jooq.DSLContext;

public class TicketPurchaseImpl implements TicketPurchaseUseCase {

    private final TicketPersistenceUseCase persistence;
    private final UserPersistenceUseCase userPersistence;
    private final DSLContext dsl;

    public TicketPurchaseImpl(
            DSLContext dsl,
            TicketPersistenceUseCase persistence,
            UserPersistenceUseCase userPersistence
    ) {
        this.dsl = dsl;
        this.persistence = persistence;
        this.userPersistence = userPersistence;
    }

    @Override
    public List<Ticket> purchaseTickets(
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds,
            String couponCode
    ) {
        AuthorizationHelper.getAuthorizedUser(spectatorUserId, userPersistence, Role.SPECTATOR);

        var show = dsl.selectFrom(SHOW)
                .where(SHOW.ID.eq(showId))
                .fetchOne();

        var seats = dsl.selectFrom(org.amts.jooq.Tables.SEAT)
                .where(org.amts.jooq.Tables.SEAT.ID.in(seatIds))
                .fetch();

        boolean hasNonOrdinaryDesignation = seats.stream()
                .anyMatch(s -> s.getDesignation() != org.amts.jooq.enums.Seatdesignationenum.ORDINARY);
        if (hasNonOrdinaryDesignation) {
            throw new IllegalArgumentException("Cannot purchase VIP or complimentary seats through this flow");
        }

        double total = 0;
        for (var seat : seats) {
            if (seat.getType() == org.amts.jooq.enums.Seattypeenum.BALCONY) {
                total += show.getBalconySeatPrice();
            } else {
                total += show.getOrdinarySeatPrice();
            }
        }

        UUID couponId = null;
        if (couponCode != null) {
            couponId = persistence.fetchCouponId(spectatorUserId, couponCode);
            if (couponId != null) {
                total *= 0.9;
            }
        }

        UUID bookingId = UUID.randomUUID();
        List<Ticket> tickets = generateTickets(bookingId, showId, seatIds);

        persistence.saveOnlineBookingWithTickets(
                bookingId,
                showId,
                spectatorUserId,
                couponId,
                total,
                tickets
        );

        return tickets;
    }

    @Override
    public String purchaseCoupon(UUID spectatorUserId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(spectatorUserId, userPersistence, Role.SPECTATOR);
        return persistence.saveCoupon(spectatorUserId, showId);
    }

    @Override
    public List<Ticket> purchaseTicketsViaAgent(
            UUID agentUserId,
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds
    ) {
        AuthorizationHelper.getAuthorizedUser(spectatorUserId, userPersistence, Role.SPECTATOR);
        AuthorizationHelper.getAuthorizedUser(agentUserId, userPersistence, Role.SALES_AGENT);

        var show = dsl.selectFrom(SHOW)
                .where(SHOW.ID.eq(showId))
                .fetchOne();

        var seats = dsl.selectFrom(org.amts.jooq.Tables.SEAT)
                .where(org.amts.jooq.Tables.SEAT.ID.in(seatIds))
                .fetch();

        boolean hasNonOrdinaryDesignationAgent = seats.stream()
                .anyMatch(s -> s.getDesignation() != org.amts.jooq.enums.Seatdesignationenum.ORDINARY);
        if (hasNonOrdinaryDesignationAgent) {
            throw new IllegalArgumentException("Cannot purchase VIP or complimentary seats through this flow");
        }

        double total = 0;
        for (var seat : seats) {
            if (seat.getType() == org.amts.jooq.enums.Seattypeenum.BALCONY) {
                total += show.getBalconySeatPrice();
            } else {
                total += show.getOrdinarySeatPrice();
            }
        }

        UUID bookingId = UUID.randomUUID();
        List<Ticket> tickets = generateTickets(bookingId, showId, seatIds);

        persistence.saveOfflineBookingWithTickets(
                bookingId,
                showId,
                spectatorUserId,
                agentUserId,
                total,
                tickets
        );

        return tickets;
    }

    @Override
    public double cancelTickets(UUID spectatorUserId, List<UUID> ticketIds) {
        AuthorizationHelper.getAuthorizedUser(spectatorUserId, userPersistence, Role.SPECTATOR);

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

            var seat = dsl.selectFrom(org.amts.jooq.Tables.SEAT)
                    .where(org.amts.jooq.Tables.SEAT.ID.eq(ticket.getSeatId()))
                    .fetchOne();

            double price = seat.getType() == org.amts.jooq.enums.Seattypeenum.BALCONY 
                    ? show.getBalconySeatPrice() : show.getOrdinarySeatPrice();
                    
            double refund;
            RefundType type;

            if (hoursBeforeShow > 72) {
                refund = price - 5;
                type = RefundType.BEFORE_THREE_DAYS;
            } else if (hoursBeforeShow > 24) {
                refund = price - (seat.getType() == org.amts.jooq.enums.Seattypeenum.BALCONY ? 15 : 10);
                type = RefundType.BEFORE_ONE_DAY;
            } else {
                refund = price * 0.5;
                type = RefundType.SAME_DAY;
            }

            totalRefund += refund;

            persistence.saveRefundAndMarkTicket(
                    ticketId,
                    new TicketRefund(ticketId, type, LocalDateTime.now())
            );
        }

        return totalRefund;
    }

    private List<Ticket> generateTickets(UUID bookingId, UUID showId, List<UUID> seatIds) {
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
}