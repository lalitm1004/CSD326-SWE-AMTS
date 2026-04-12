package org.amts.adapters.usecases.sales;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.amts.application.usecases.sales.rawinterfaces.SalesPersistenceUseCase;
import org.amts.jooq.Tables;
import org.jooq.DSLContext;

import static org.jooq.impl.DSL.sum;

public class SalesPersistenceImpl implements SalesPersistenceUseCase {

    private final DSLContext dsl;

    public SalesPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public double getRevenueByShow(UUID showId) {
        Double total = dsl.select(sum(Tables.BOOKING.AMOUNT))
                .from(Tables.BOOKING)
                .where(Tables.BOOKING.SHOW_ID.eq(showId))
                .fetchOneInto(Double.class);
        return total != null ? total : 0.0;
    }

    @Override
    public Map<UUID, Double> getRevenueByEvent(UUID eventId) {
        var rs = dsl.select(Tables.BOOKING.SHOW_ID, sum(Tables.BOOKING.AMOUNT))
                .from(Tables.BOOKING)
                .join(Tables.SHOW).on(Tables.BOOKING.SHOW_ID.eq(Tables.SHOW.ID))
                .where(Tables.SHOW.EVENT_ID.eq(eventId))
                .groupBy(Tables.BOOKING.SHOW_ID)
                .fetch();

        return rs.stream().collect(Collectors.toMap(
                r -> r.get(Tables.BOOKING.SHOW_ID),
                r -> {
                    Double s = r.get(sum(Tables.BOOKING.AMOUNT), Double.class);
                    return s != null ? s : 0.0;
                }));
    }

    @Override
    public double getOfflineRevenueByAgentForEvent(UUID agentId, UUID eventId) {
        Double total = dsl.select(sum(Tables.BOOKING.AMOUNT))
                .from(Tables.BOOKING)
                .join(Tables.OFFLINE_BOOKING).on(Tables.BOOKING.ID.eq(Tables.OFFLINE_BOOKING.ID))
                .join(Tables.SHOW).on(Tables.BOOKING.SHOW_ID.eq(Tables.SHOW.ID))
                .where(Tables.OFFLINE_BOOKING.SALES_AGENT_USER_ID.eq(agentId))
                .and(Tables.SHOW.EVENT_ID.eq(eventId))
                .fetchOneInto(Double.class);
        return total != null ? total : 0.0;
    }

    @Override
    public double getOfflineRevenueByAgent(UUID agentId) {
        Double total = dsl.select(sum(Tables.BOOKING.AMOUNT))
                .from(Tables.BOOKING)
                .join(Tables.OFFLINE_BOOKING).on(Tables.BOOKING.ID.eq(Tables.OFFLINE_BOOKING.ID))
                .where(Tables.OFFLINE_BOOKING.SALES_AGENT_USER_ID.eq(agentId))
                .fetchOneInto(Double.class);
        return total != null ? total : 0.0;
    }
}
