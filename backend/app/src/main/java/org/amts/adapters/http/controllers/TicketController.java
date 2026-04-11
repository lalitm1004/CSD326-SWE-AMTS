package org.amts.adapters.http.controllers;

import java.util.List;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketUseCase ticketUseCases;

    public TicketController(TicketUseCase ticketUseCases) {
        this.ticketUseCases = ticketUseCases;
    }

    record PurchaseTicketsRequest(
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds,
            String couponCode
    ) {}

    record PurchaseTicketsViaAgentRequest(
            UUID agentUserId,
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds
    ) {}

    record CancelTicketsRequest(
            UUID spectatorUserId,
            List<UUID> ticketIds
    ) {}

    @PostMapping("/purchase")
    public ResponseEntity<List<Ticket>> purchaseTickets(
            @RequestBody PurchaseTicketsRequest request) {
        List<Ticket> tickets = ticketUseCases.purchaseTickets(
                request.spectatorUserId(),
                request.showId(),
                request.seatIds(),
                request.couponCode()
        );
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/purchase/agent")
    public ResponseEntity<List<Ticket>> purchaseTicketsViaAgent(
            @RequestBody PurchaseTicketsViaAgentRequest request) {
        List<Ticket> tickets = ticketUseCases.purchaseTicketsViaAgent(
                request.agentUserId(),
                request.spectatorUserId(),
                request.showId(),
                request.seatIds()
        );
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/coupon")
    public ResponseEntity<String> purchaseCoupon(
            @RequestParam UUID spectatorUserId,
            @RequestParam UUID showId) {
        String code = ticketUseCases.purchaseCoupon(spectatorUserId, showId);
        return ResponseEntity.ok(code);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Double> cancelTickets(
            @RequestBody CancelTicketsRequest request) {
        double refund = ticketUseCases.cancelTickets(
                request.spectatorUserId(),
                request.ticketIds()
        );
        return ResponseEntity.ok(refund);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getTicketsByBookingId(
            @RequestParam UUID bookingId) {
        return ResponseEntity.ok(
                ticketUseCases.getTicketsByBookingId(bookingId)
        );
    }
}
