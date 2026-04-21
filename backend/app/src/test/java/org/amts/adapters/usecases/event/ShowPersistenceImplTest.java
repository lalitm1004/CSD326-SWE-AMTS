package org.amts.adapters.usecases.event;

import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.amts.domain.valueobjects.Money;
import org.amts.jooq.enums.Seatdesignationenum;
import org.amts.jooq.enums.Seattypeenum;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.amts.jooq.Tables.SEAT;
import static org.amts.jooq.Tables.SHOW;
import static org.amts.jooq.Tables.TICKET;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShowPersistenceImpl Tests")
class ShowPersistenceImplTest {

    private UUID showId;
    private UUID eventId;
    private UUID creatorId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        showId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        now = LocalDateTime.now();
    }

    private ShowPersistenceImpl createPersistence(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new ShowPersistenceImpl(mockDsl);
    }

    @Test
    @DisplayName("addShowToEvent - Executes insert without throwing")
    void addShowToEvent_ExecutesInsert() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            return new MockResult[]{new MockResult(1, dsl.newResult(SHOW.ID))};
        };
        ShowPersistenceImpl persistence = createPersistence(provider);

        assertDoesNotThrow(() -> 
            persistence.addShowToEvent(creatorId, eventId, "Evening Show", "A great show", "url", now, now.plusHours(2), 100.0, 200.0, 50, 20)
        );
    }

    @Test
    @DisplayName("getShowByID - Returns show when found")
    void getShowByID_Found() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(
                    SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME, SHOW.DESCRIPTION,
                    SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT,
                    SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE,
                    SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
            var record = dsl.newRecord(
                    SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME, SHOW.DESCRIPTION,
                    SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT,
                    SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE,
                    SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
            record.values(showId, eventId, creatorId, "Matinee", "Daytime show", "url", now, now.plusHours(2), 50.0, 100.0, 50, 20, now);
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        ShowPersistenceImpl persistence = createPersistence(provider);
        Optional<Show> result = persistence.getShowByID(showId);

        assertTrue(result.isPresent());
        assertEquals("Matinee", result.get().getName());
        assertEquals(Money.of(50.0), result.get().getOrdinarySeatPrice());
    }

    @Test
    @DisplayName("getShowsByEvent - Returns list of shows")
    void getShowsByEvent_ReturnsList() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME, SHOW.DESCRIPTION, SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT, SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE, SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
            var record = dsl.newRecord(SHOW.ID, SHOW.EVENT_ID, SHOW.CREATED_BY_USER_ID, SHOW.NAME, SHOW.DESCRIPTION, SHOW.THUMBNAIL_URL, SHOW.STARTING_AT, SHOW.ENDING_AT, SHOW.ORDINARY_SEAT_PRICE, SHOW.BALCONY_SEAT_PRICE, SHOW.NUM_ORDINARY_SEATS, SHOW.NUM_BALCONY_SEATS, SHOW.CREATED_AT);
            record.values(showId, eventId, creatorId, "S1", "D1", "U1", now, now.plusHours(2), 10.0, 20.0, 1, 1, now);
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        ShowPersistenceImpl persistence = createPersistence(provider);
        Optional<ArrayList<Show>> result = persistence.getShowsByEvent(eventId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("S1", result.get().get(0).getName());
    }

    @Test
    @DisplayName("updateSeatDesignation - Executes update")
    void updateSeatDesignation_ExecutesUpdate() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        ShowPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.updateSeatDesignation(UUID.randomUUID(), SeatDesignation.VIP));
    }

    @Test
    @DisplayName("getSeatsByShow - Returns list of seats")
    void getSeatsByShow_ReturnsList() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var activeTicketId = DSL.field("active_ticket_id", TICKET.ID.getDataType());
            var result = dsl.newResult(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION, activeTicketId);

            var availableSeat = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION, activeTicketId);
            availableSeat.set(SEAT.ID, UUID.randomUUID());
            availableSeat.set(SEAT.NUMBER, "A1");
            availableSeat.set(SEAT.TYPE, Seattypeenum.ORDINARY);
            availableSeat.set(SEAT.DESIGNATION, Seatdesignationenum.ORDINARY);
            availableSeat.set(activeTicketId, null);
            result.add(availableSeat);

            var bookedSeat = dsl.newRecord(SEAT.ID, SEAT.NUMBER, SEAT.TYPE, SEAT.DESIGNATION, activeTicketId);
            bookedSeat.set(SEAT.ID, UUID.randomUUID());
            bookedSeat.set(SEAT.NUMBER, "A2");
            bookedSeat.set(SEAT.TYPE, Seattypeenum.BALCONY);
            bookedSeat.set(SEAT.DESIGNATION, Seatdesignationenum.VIP);
            bookedSeat.set(activeTicketId, UUID.randomUUID());
            result.add(bookedSeat);

            return new MockResult[]{new MockResult(1, result)};
        };

        ShowPersistenceImpl persistence = createPersistence(provider);
        List<Seat> result = persistence.getSeatsByShow(showId);

        assertEquals(2, result.size());
        assertEquals("A1", result.get(0).getNumber());
        assertEquals(SeatType.ORDINARY, result.get(0).getType());
        assertTrue(result.get(0).isAvailable());
        assertEquals("A2", result.get(1).getNumber());
        assertEquals(SeatType.BALCONY, result.get(1).getType());
        assertEquals(SeatDesignation.VIP, result.get(1).getDesignation());
        assertFalse(result.get(1).isAvailable());
    }

    @Test
    @DisplayName("updateShow... - Tests update methods execute without throwing")
    void updateShowMethods_ExecuteWithoutThrowing() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        ShowPersistenceImpl persistence = createPersistence(provider);
        
        assertDoesNotThrow(() -> persistence.updateShowName(showId, "New Name"));
        assertDoesNotThrow(() -> persistence.updateShowDescription(showId, "New Desc"));
        assertDoesNotThrow(() -> persistence.updateShowThumbnail(showId, "New Url"));
        assertDoesNotThrow(() -> persistence.updateShowStartingAt(showId, now));
        assertDoesNotThrow(() -> persistence.updateShowEndingAt(showId, now.plusHours(1)));
        assertDoesNotThrow(() -> persistence.updateShowOrdinarySeatPrice(showId, 150.0));
        assertDoesNotThrow(() -> persistence.updateShowBalconySeatPrice(showId, 250.0));
        assertDoesNotThrow(() -> persistence.updateShowNumOrdinarySeats(showId, 100));
        assertDoesNotThrow(() -> persistence.updateShowNumBalconySeats(showId, 50));
    }

    @Test
    @DisplayName("deleteShow - Executes delete")
    void deleteShow_ExecutesDelete() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        ShowPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.deleteShow(showId));
    }
}
