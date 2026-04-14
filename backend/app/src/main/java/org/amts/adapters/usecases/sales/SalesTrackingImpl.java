package org.amts.adapters.usecases.sales;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.sales.SalesTrackingUseCase;
import org.amts.application.usecases.sales.rawinterfaces.SalesPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

public class SalesTrackingImpl implements SalesTrackingUseCase {

    private final SalesPersistenceUseCase persistence;
    private final UserPersistenceUseCase userPersistence;
    private final ShowPersistenceUseCase showPersistence;

    public SalesTrackingImpl(
            SalesPersistenceUseCase persistence,
            UserPersistenceUseCase userPersistence,
            ShowPersistenceUseCase showPersistence) {
        this.persistence = persistence;
        this.userPersistence = userPersistence;
        this.showPersistence = showPersistence;
    }

    @Override
    public double getRevenueByShow(UUID actorUserId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(actorUserId, userPersistence, Role.PRESIDENT, Role.FINANCIAL_CLERK);
        return persistence.getRevenueByShow(showId);
    }

    @Override
    public Map<UUID, Double> getRevenueByEvent(UUID actorUserId, UUID eventId) {
        AuthorizationHelper.getAuthorizedUser(actorUserId, userPersistence, Role.PRESIDENT, Role.FINANCIAL_CLERK);

        List<Show> shows = showPersistence.getShowsByEvent(eventId).orElseGet(java.util.ArrayList::new);
        Map<UUID, Double> breakdown = new HashMap<>();

        for (Show show : shows) {
            breakdown.put(show.getId(), getRevenueByShow(actorUserId, show.getId()));
        }

        return breakdown;
    }

    private double getOfflineRevenueByAgentForEvent(UUID actorUserId, UUID agentId, UUID eventId) {
        validateAgentOrAdminAccess(actorUserId, agentId);
        return persistence.getOfflineRevenueByAgentForEvent(agentId, eventId);
    }

    private double getOfflineRevenueByAgent(UUID actorUserId, UUID agentId) {
        validateAgentOrAdminAccess(actorUserId, agentId);
        return persistence.getOfflineRevenueByAgent(agentId);
    }

    @Override
    public double getOfflineCommissionByAgentForEvent(UUID actorUserId, UUID agentId, UUID eventId) {
        return getOfflineRevenueByAgentForEvent(actorUserId, agentId, eventId) * 0.01;
    }

    @Override
    public double getOfflineCommissionByAgent(UUID actorUserId, UUID agentId) {
        return getOfflineRevenueByAgent(actorUserId, agentId) * 0.01;
    }

    private void validateAgentOrAdminAccess(UUID actorUserId, UUID agentId) {
        User actor = AuthorizationHelper.getAuthorizedUser(actorUserId, userPersistence,
                Role.PRESIDENT, Role.FINANCIAL_CLERK, Role.SALES_AGENT);

        if (actor.getRoles().contains(Role.SALES_AGENT) &&
                !actor.getRoles().contains(Role.PRESIDENT) &&
                !actor.getRoles().contains(Role.FINANCIAL_CLERK)) {
            if (!actorUserId.equals(agentId)) {
                throw new SecurityException("Sales agents can only view their own sales data.");
            }
        }
    }
}
