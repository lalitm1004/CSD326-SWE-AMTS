package org.amts.adapters.usecase.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.ticket.TicketPurchaseUseCase;
import org.amts.domain.entities.coupon.Coupon;
import org.amts.domain.entities.ticket.RefundType;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;

public class TicketPurchaseUseCaseImpl implements TicketPurchaseUseCase {

    private final TicketPersistenceUseCase persistence;

    private final Map<String, Coupon> couponStore = new HashMap<>();
    private final Map<UUID, Double> agentCommission = new HashMap<>();

    public TicketPurchaseUseCaseImpl(TicketPersistenceUseCase persistence) {
        this.persistence = persistence;
    }

    @Override
    public List<Ticket> purchaseTickets(UUID spectatorUserId, UUID showId, List<UUID> seatIds, String couponCode) {

        double seatPrice = 200; 
        double total = seatPrice * seatIds.size();

        // Apply coupon
        if (couponCode != null && couponStore.containsKey(couponCode)) {
            total = total * 0.9;
        }

        List<Ticket> tickets = generateTickets(showId, seatIds);
        persistence.saveTickets(tickets);

        return tickets;
    }

    @Override
    public String purchaseCoupon(UUID spectatorUserId, UUID showId) {

        String code = generateCouponCode();
        Coupon coupon = new Coupon(
                UUID.randomUUID(),
                showId,
                spectatorUserId,
                code,
                LocalDateTime.now()
        );

        couponStore.put(code, coupon);
        return code;
    }

    @Override
    public List<Ticket> purchaseTicketsViaAgent(UUID agentUserId, UUID spectatorUserId, UUID showId, List<UUID> seatIds) {

        List<Ticket> tickets = generateTickets(showId, seatIds);
        persistence.saveTickets(tickets);
        agentCommission.put(agentUserId,
                agentCommission.getOrDefault(agentUserId, 0.0) + 50 * seatIds.size());

        return tickets;
    }

    @Override
    public double cancelTickets(UUID spectatorUserId, List<UUID> ticketIds) {

        double totalRefund = 0;
        for (UUID ticketId : ticketIds) {

            Ticket ticket = persistence.getTicketById(ticketId).orElseThrow();

            long hoursBeforeShow = 48; // mock (replace after show creation)
            double price = 200;

            RefundType type;
            double refund;

            if (hoursBeforeShow > 72) {
                type = RefundType.BEFORE_THREE_DAYS;
                refund = price - 5;
            } else if (hoursBeforeShow > 24) {
                type = RefundType.BEFORE_ONE_DAY;
                refund = price - 10;
            } else {
                type = RefundType.SAME_DAY;
                refund = price * 0.5;
            }

            totalRefund += refund;
            persistence.markTicketAsRefunded(ticketId);
            persistence.saveTicketRefund(
                    new TicketRefund(ticketId, type, LocalDateTime.now())
            );
        }

        return totalRefund;
    }


    private List<Ticket> generateTickets(UUID showId, List<UUID> seatIds) {

        List<Ticket> tickets = new ArrayList<>();
        UUID bookingId = UUID.randomUUID();

        for (UUID seatId : seatIds) {
            tickets.add(new Ticket(
                    UUID.randomUUID(),
                    bookingId,
                    showId,
                    seatId,
                    generateTicketCode(),
                    false,
                    LocalDateTime.now()
            ));
        }

        return tickets;
    }

    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateCouponCode() {
        return String.valueOf((long)(Math.random() * 1_000_000_0000L));
    }
}