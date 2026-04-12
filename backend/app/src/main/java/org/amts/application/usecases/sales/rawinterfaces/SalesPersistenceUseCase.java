package org.amts.application.usecases.sales.rawinterfaces;

import java.util.Map;
import java.util.UUID;

public interface SalesPersistenceUseCase {

    double getRevenueByShow(UUID showId);

    Map<UUID, Double> getRevenueByEvent(UUID eventId);

    double getOfflineRevenueByAgentForEvent(UUID agentId, UUID eventId);

    double getOfflineRevenueByAgent(UUID agentId);
}
