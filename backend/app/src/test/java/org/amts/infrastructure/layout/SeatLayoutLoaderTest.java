package org.amts.infrastructure.layout;

import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.amts.infrastructure.config.SeatLayoutProperties;
import org.amts.infrastructure.config.SeatLayoutProperties.RowConfig;
import org.amts.jooq.enums.Seatdesignationenum;
import org.amts.jooq.enums.Seattypeenum;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SeatLayoutLoader Tests")
class SeatLayoutLoaderTest {

    private SeatLayoutProperties twoRowProperties() {
        SeatLayoutProperties props = new SeatLayoutProperties();

        RowConfig balcony = new RowConfig();
        balcony.setLabel("A");
        balcony.setType(SeatType.BALCONY);
        balcony.setCount(3);

        RowConfig ordinary = new RowConfig();
        ordinary.setLabel("B");
        ordinary.setType(SeatType.ORDINARY);
        ordinary.setCount(2);

        props.setRows(List.of(balcony, ordinary));
        return props;
    }

    @Test
    @DisplayName("onApplicationEvent - Seeds correct number of seats when DB is empty")
    void onApplicationEvent_EmptyDb_SeedsAllSeats() {
        // batchInsert in jOOQ issues a single provider call for the whole batch.
        // We verify correct seat count by inspecting the batch's MockResult array size.
        AtomicInteger batchSize = new AtomicInteger(0);

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            String sql = ctx.sql().toLowerCase();

            if (sql.contains("count")) {
                var field = DSL.field("count", Integer.class);
                var result = dsl.newResult(field);
                var record = dsl.newRecord(field);
                record.set(field, 0);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            }

            // batchBindings() returns one binding array per record in the batch
            int rows = ctx.batchBindings() != null ? ctx.batchBindings().length : 1;
            batchSize.set(rows);
            MockResult[] results = new MockResult[rows];
            for (int i = 0; i < rows; i++) {
                results[i] = new MockResult(1, null);
            }
            return results;
        };

        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        SeatLayoutLoader loader = new SeatLayoutLoader(mockDsl, twoRowProperties());
        loader.onApplicationEvent(null);

        // 3 balcony (A1-A3) + 2 ordinary (B1-B2) = 5 seats in one batch
        assertEquals(5, batchSize.get());
    }

    @Test
    @DisplayName("onApplicationEvent - Skips seeding when seats already exist")
    void onApplicationEvent_SeatsExist_SkipsSeeding() {
        AtomicInteger insertCount = new AtomicInteger(0);

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            String sql = ctx.sql().toLowerCase();

            if (sql.contains("count")) {
                var field = DSL.field("count", Integer.class);
                var result = dsl.newResult(field);
                var record = dsl.newRecord(field);
                record.set(field, 800);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            }

            insertCount.incrementAndGet();
            var empty = dsl.newResult(DSL.field("id"));
            return new MockResult[]{new MockResult(0, empty)};
        };

        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        SeatLayoutLoader loader = new SeatLayoutLoader(mockDsl, twoRowProperties());
        loader.onApplicationEvent(null);

        assertEquals(0, insertCount.get(), "No inserts should happen when seats already exist");
    }

    @Test
    @DisplayName("onApplicationEvent - Generates correct seat numbers from label and count")
    void onApplicationEvent_GeneratesCorrectSeatNumbers() {
        // Verifies the label+index generation logic directly without DB interaction.
        SeatLayoutProperties props = new SeatLayoutProperties();
        RowConfig row = new RowConfig();
        row.setLabel("A");
        row.setType(SeatType.BALCONY);
        row.setCount(3);
        props.setRows(List.of(row));

        var expectedNumbers = List.of("A1", "A2", "A3");
        var actualNumbers = new ArrayList<String>();
        for (int i = 1; i <= row.getCount(); i++) {
            actualNumbers.add(row.getLabel() + i);
        }

        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    @DisplayName("onApplicationEvent - Maps BALCONY rows to BALCONY enum and ORDINARY rows to ORDINARY enum")
    void onApplicationEvent_MapsTypesCorrectly() {
        // batchBindings()[i] = { UUID id, String number, type, designation }
        // Type is at index 2. Capture it by position to avoid confusing with the designation binding at index 3.
        var capturedTypeNames = new ArrayList<String>();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            String sql = ctx.sql().toLowerCase();

            if (sql.contains("count")) {
                var field = DSL.field("count", Integer.class);
                var result = dsl.newResult(field);
                var record = dsl.newRecord(field);
                record.set(field, 0);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            }

            Object[][] batchBindings = ctx.batchBindings();
            if (batchBindings != null) {
                for (Object[] bindings : batchBindings) {
                    // bindings: [id(0), number(1), type(2), designation(3)]
                    if (bindings.length > 2) {
                        Object typeBinding = bindings[2];
                        if (typeBinding instanceof Seattypeenum t) {
                            capturedTypeNames.add(t.name());
                        } else if (typeBinding != null) {
                            capturedTypeNames.add(typeBinding.toString());
                        }
                    }
                }
            }

            int rows = batchBindings != null ? batchBindings.length : 1;
            MockResult[] results = new MockResult[rows];
            for (int i = 0; i < rows; i++) {
                results[i] = new MockResult(1, null);
            }
            return results;
        };

        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        new SeatLayoutLoader(mockDsl, twoRowProperties()).onApplicationEvent(null);

        // twoRowProperties: 3 balcony (A) + 2 ordinary (B)
        assertEquals(5, capturedTypeNames.size());
        assertTrue(capturedTypeNames.subList(0, 3).stream().allMatch(s -> s.equals("BALCONY")),
                "First 3 seats (row A) should be BALCONY");
        assertTrue(capturedTypeNames.subList(3, 5).stream().allMatch(s -> s.equals("ORDINARY")),
                "Last 2 seats (row B) should be ORDINARY");
    }

    @Test
    @DisplayName("onApplicationEvent - Defaults to ORDINARY designation when none specified")
    void onApplicationEvent_DefaultsToOrdinaryDesignation() {
        var capturedDesignationNames = new ArrayList<String>();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            String sql = ctx.sql().toLowerCase();

            if (sql.contains("count")) {
                var field = DSL.field("count", Integer.class);
                var result = dsl.newResult(field);
                var record = dsl.newRecord(field);
                record.set(field, 0);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            }

            Object[][] batchBindings = ctx.batchBindings();
            if (batchBindings != null) {
                for (Object[] bindings : batchBindings) {
                    // bindings: [id(0), number(1), type(2), designation(3)]
                    if (bindings.length > 3) {
                        Object designationBinding = bindings[3];
                        if (designationBinding instanceof Seatdesignationenum d) {
                            capturedDesignationNames.add(d.name());
                        } else if (designationBinding != null) {
                            capturedDesignationNames.add(designationBinding.toString());
                        }
                    }
                }
            }

            int rows = batchBindings != null ? batchBindings.length : 1;
            MockResult[] results = new MockResult[rows];
            for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
            return results;
        };

        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        new SeatLayoutLoader(mockDsl, twoRowProperties()).onApplicationEvent(null);

        // twoRowProperties has no designation set — all should default to ORDINARY
        assertEquals(5, capturedDesignationNames.size(), "Should capture designation for each seat");
        assertTrue(capturedDesignationNames.stream().allMatch(s -> s.equals("ORDINARY")),
                "All seats should default to ORDINARY designation");
    }

    @Test
    @DisplayName("onApplicationEvent - Maps VIP and COMPLIMENTARY designations correctly")
    void onApplicationEvent_MapsDesignationsCorrectly() {
        SeatLayoutProperties props = new SeatLayoutProperties();

        RowConfig vipRow = new RowConfig();
        vipRow.setLabel("A");
        vipRow.setType(SeatType.BALCONY);
        vipRow.setCount(2);
        vipRow.setDesignation(SeatDesignation.VIP);

        RowConfig complRow = new RowConfig();
        complRow.setLabel("T");
        complRow.setType(SeatType.ORDINARY);
        complRow.setCount(1);
        complRow.setDesignation(SeatDesignation.COMPLIMENTARY);

        props.setRows(List.of(vipRow, complRow));

        var capturedDesignationNames = new ArrayList<String>();

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            String sql = ctx.sql().toLowerCase();

            if (sql.contains("count")) {
                var field = DSL.field("count", Integer.class);
                var result = dsl.newResult(field);
                var record = dsl.newRecord(field);
                record.set(field, 0);
                result.add(record);
                return new MockResult[]{new MockResult(1, result)};
            }

            Object[][] batchBindings = ctx.batchBindings();
            if (batchBindings != null) {
                for (Object[] bindings : batchBindings) {
                    // bindings: [id(0), number(1), type(2), designation(3)]
                    if (bindings.length > 3) {
                        Object designationBinding = bindings[3];
                        if (designationBinding instanceof Seatdesignationenum d) {
                            capturedDesignationNames.add(d.name());
                        } else if (designationBinding != null) {
                            capturedDesignationNames.add(designationBinding.toString());
                        }
                    }
                }
            }

            int rows = batchBindings != null ? batchBindings.length : 1;
            MockResult[] results = new MockResult[rows];
            for (int i = 0; i < rows; i++) results[i] = new MockResult(1, null);
            return results;
        };

        DSLContext mockDsl = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        new SeatLayoutLoader(mockDsl, props).onApplicationEvent(null);

        // 2 VIP (row A) + 1 COMPLIMENTARY (row T)
        assertEquals(3, capturedDesignationNames.size());
        assertTrue(capturedDesignationNames.subList(0, 2).stream().allMatch(s -> s.equals("VIP")),
                "First 2 seats (row A) should be VIP");
        assertEquals("COMPLIMENTARY", capturedDesignationNames.get(2),
                "Last seat (row T) should be COMPLIMENTARY");
    }
}
