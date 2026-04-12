package org.amts.application.usecases.sales;

import java.util.Map;
import java.util.UUID;

public interface SalesTrackingUseCase {

    double getRevenueByShow(UUID actorUserId, UUID showId);

    Map<UUID, Double> getRevenueByEvent(UUID actorUserId, UUID eventId);

    double getOfflineCommissionByAgentForEvent(UUID actorUserId, UUID agentId, UUID eventId);

    double getOfflineCommissionByAgent(UUID actorUserId, UUID agentId);
}
