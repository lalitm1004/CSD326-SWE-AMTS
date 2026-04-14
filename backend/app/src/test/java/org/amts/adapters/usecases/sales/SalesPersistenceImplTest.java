package org.amts.adapters.usecases.sales;

import org.amts.jooq.Tables;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.jooq.impl.DSL.sum;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("SalesPersistenceImpl Tests")
class SalesPersistenceImplTest {

    private SalesPersistenceImpl createPersistence(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new SalesPersistenceImpl(mockDsl);
    }

    @Test
    @DisplayName("getRevenueByShow - Sums booking amounts correctly")
    void getRevenueByShow_SumsCorrectly() {
        UUID showId = UUID.randomUUID();
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(sum(Tables.BOOKING.AMOUNT));
            var record = dsl.newRecord(sum(Tables.BOOKING.AMOUNT));
            record.values(java.math.BigDecimal.valueOf(1500.0));
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        SalesPersistenceImpl persistence = createPersistence(provider);
        double revenue = persistence.getRevenueByShow(showId);

        assertEquals(1500.0, revenue);
    }

    @Test
    @DisplayName("getRevenueByShow - Returns zero when no bookings found")
    void getRevenueByShow_ReturnsZeroWhenEmpty() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(sum(Tables.BOOKING.AMOUNT));
            var record = dsl.newRecord(sum(Tables.BOOKING.AMOUNT));
            record.values(null); // Database SUM returns null for no rows
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        SalesPersistenceImpl persistence = createPersistence(provider);
        assertEquals(0.0, persistence.getRevenueByShow(UUID.randomUUID()));
    }

    @Test
    @DisplayName("getRevenueByEvent - Groups revenue by show for an event")
    void getRevenueByEvent_GroupsCorrectly() {
        UUID eventId = UUID.randomUUID();
        UUID show1Id = UUID.randomUUID();
        UUID show2Id = UUID.randomUUID();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(Tables.BOOKING.SHOW_ID, sum(Tables.BOOKING.AMOUNT));
            
            var r1 = dsl.newRecord(Tables.BOOKING.SHOW_ID, sum(Tables.BOOKING.AMOUNT));
            r1.values(show1Id, java.math.BigDecimal.valueOf(500.0));
            
            var r2 = dsl.newRecord(Tables.BOOKING.SHOW_ID, sum(Tables.BOOKING.AMOUNT));
            r2.values(show2Id, java.math.BigDecimal.valueOf(1200.0));
            
            result.add(r1);
            result.add(r2);
            return new MockResult[]{new MockResult(2, result)};
        };

        SalesPersistenceImpl persistence = createPersistence(provider);
        Map<UUID, Double> revenueMap = persistence.getRevenueByEvent(eventId);

        assertNotNull(revenueMap);
        assertEquals(2, revenueMap.size());
        assertEquals(500.0, revenueMap.get(show1Id));
        assertEquals(1200.0, revenueMap.get(show2Id));
    }

    @Test
    @DisplayName("getOfflineRevenueByAgentForEvent - Calculates revenue with seating logic")
    void getOfflineRevenueByAgentForEvent_CalculatesCorrectly() {
        UUID agentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            // The query uses a choose/sum dynamic field. We mock the typed sum record.
            var result = dsl.newResult(sum(DSL.field("priceField", java.math.BigDecimal.class)));
            var record = dsl.newRecord(sum(DSL.field("priceField", java.math.BigDecimal.class)));
            record.values(java.math.BigDecimal.valueOf(750.0));
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        SalesPersistenceImpl persistence = createPersistence(provider);
        double revenue = persistence.getOfflineRevenueByAgentForEvent(agentId, eventId);

        assertEquals(750.0, revenue);
    }

    @Test
    @DisplayName("getOfflineRevenueByAgent - Aggregates total offline revenue for agent")
    void getOfflineRevenueByAgent_AggregatesCorrectly() {
        UUID agentId = UUID.randomUUID();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(sum(DSL.field("priceField", java.math.BigDecimal.class)));
            var record = dsl.newRecord(sum(DSL.field("priceField", java.math.BigDecimal.class)));
            record.values(java.math.BigDecimal.valueOf(2300.0));
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        SalesPersistenceImpl persistence = createPersistence(provider);
        double total = persistence.getOfflineRevenueByAgent(agentId);

        assertEquals(2300.0, total);
    }
}
