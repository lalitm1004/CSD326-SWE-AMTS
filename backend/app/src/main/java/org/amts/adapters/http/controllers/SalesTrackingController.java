package org.amts.adapters.http.controllers;

import java.util.Map;
import java.util.UUID;

import org.amts.application.usecases.sales.SalesTrackingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SalesTrackingController {

    private final SalesTrackingUseCase salesTrackingUseCase;

    public SalesTrackingController(SalesTrackingUseCase salesTrackingUseCase) {
        this.salesTrackingUseCase = salesTrackingUseCase;
    }

    @GetMapping("/revenue/show")
    public ResponseEntity<Double> getRevenueByShow(
            @RequestParam UUID actorUserId,
            @RequestParam UUID showId) {
        return ResponseEntity.ok(salesTrackingUseCase.getRevenueByShow(actorUserId, showId));
    }

    @GetMapping("/revenue/event")
    public ResponseEntity<Map<UUID, Double>> getRevenueByEvent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID eventId) {
        return ResponseEntity.ok(salesTrackingUseCase.getRevenueByEvent(actorUserId, eventId));
    }

    @GetMapping("/commission/agent/event")
    public ResponseEntity<Double> getOfflineCommissionByAgentForEvent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID agentId,
            @RequestParam UUID eventId) {
        return ResponseEntity.ok(salesTrackingUseCase.getOfflineCommissionByAgentForEvent(actorUserId, agentId, eventId));
    }

    @GetMapping("/commission/agent")
    public ResponseEntity<Double> getOfflineCommissionByAgent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID agentId) {
        return ResponseEntity.ok(salesTrackingUseCase.getOfflineCommissionByAgent(actorUserId, agentId));
    }
}
