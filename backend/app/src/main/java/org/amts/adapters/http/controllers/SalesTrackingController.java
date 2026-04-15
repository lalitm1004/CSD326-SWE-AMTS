package org.amts.adapters.http.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.List;

import org.amts.application.usecases.sales.SalesTrackingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SalesTrackingController {

    record RevenueByShowResponse(
            double totalRevenue
    ) {}

    record RevenueBreakdownItem(
            UUID showId,
            double totalRevenue
    ) {}

    record RevenueByEventResponse(
            List<RevenueBreakdownItem> items
    ) {}

    record CommissionResponse(
            double commission
    ) {}

    private final SalesTrackingUseCase salesTrackingUseCase;

    public SalesTrackingController(SalesTrackingUseCase salesTrackingUseCase) {
        this.salesTrackingUseCase = salesTrackingUseCase;
    }

    @GetMapping("/revenue/show")
    public ResponseEntity<RevenueByShowResponse> getRevenueByShow(
            @RequestParam UUID actorUserId,
            @RequestParam UUID showId) {
        return ResponseEntity.ok(new RevenueByShowResponse(
                salesTrackingUseCase.getRevenueByShow(actorUserId, showId)
        ));
    }

    @GetMapping("/revenue/event")
    public ResponseEntity<RevenueByEventResponse> getRevenueByEvent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID eventId) {
        Map<UUID, Double> breakdown = salesTrackingUseCase.getRevenueByEvent(actorUserId, eventId);
        List<RevenueBreakdownItem> items = breakdown.entrySet().stream()
                .map(entry -> new RevenueBreakdownItem(entry.getKey(), entry.getValue()))
                .toList();
        return ResponseEntity.ok(new RevenueByEventResponse(items));
    }

    @GetMapping("/commission/agent/event")
    public ResponseEntity<CommissionResponse> getOfflineCommissionByAgentForEvent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID agentId,
            @RequestParam UUID eventId) {
        return ResponseEntity.ok(new CommissionResponse(
                salesTrackingUseCase.getOfflineCommissionByAgentForEvent(actorUserId, agentId, eventId)
        ));
    }

    @GetMapping("/commission/agent")
    public ResponseEntity<CommissionResponse> getOfflineCommissionByAgent(
            @RequestParam UUID actorUserId,
            @RequestParam UUID agentId) {
        return ResponseEntity.ok(new CommissionResponse(
                salesTrackingUseCase.getOfflineCommissionByAgent(actorUserId, agentId)
        ));
    }
}
