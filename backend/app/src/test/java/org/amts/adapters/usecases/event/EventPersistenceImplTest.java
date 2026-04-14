package org.amts.adapters.usecases.event;

import org.amts.domain.entities.event.Event;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.amts.jooq.Tables.EVENT;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventPersistenceImpl Tests")
class EventPersistenceImplTest {

    private UUID eventId;
    private UUID creatorId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        now = LocalDateTime.now();
    }

    private EventPersistenceImpl createPersistence(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext mockDsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new EventPersistenceImpl(mockDsl);
    }

    @Test
    @DisplayName("createEvent - Executes insert without throwing")
    void createEvent_ExecutesInsert() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            return new MockResult[]{new MockResult(1, dsl.newResult(EVENT.ID))};
        };
        EventPersistenceImpl persistence = createPersistence(provider);

        assertDoesNotThrow(() -> 
            persistence.createEvent(creatorId, "Concert", "Rock concert", "http://image.url")
        );
    }

    @Test
    @DisplayName("getEventByID - Returns event when found")
    void getEventByID_Found() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(
                    EVENT.ID, EVENT.CREATED_BY_USER_ID, EVENT.NAME, EVENT.DESCRIPTION,
                    EVENT.THUMBNAIL_URL, EVENT.STARTING_AT, EVENT.ENDING_AT, EVENT.CREATED_AT);
            var record = dsl.newRecord(
                    EVENT.ID, EVENT.CREATED_BY_USER_ID, EVENT.NAME, EVENT.DESCRIPTION,
                    EVENT.THUMBNAIL_URL, EVENT.STARTING_AT, EVENT.ENDING_AT, EVENT.CREATED_AT);
            record.values(eventId, creatorId, "Play", "Theater play", "url", now, now.plusHours(2), now);
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        EventPersistenceImpl persistence = createPersistence(provider);
        Optional<Event> result = persistence.getEventByID(eventId);

        assertTrue(result.isPresent());
        assertEquals("Play", result.get().getName());
        assertEquals(creatorId, result.get().getCreatedByUserId());
    }

    @Test
    @DisplayName("getEventByID - Returns empty when not found")
    void getEventByID_NotFound() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(EVENT.ID);
            return new MockResult[]{new MockResult(0, result)};
        };

        EventPersistenceImpl persistence = createPersistence(provider);
        assertTrue(persistence.getEventByID(eventId).isEmpty());
    }

    @Test
    @DisplayName("getAllEvents - Returns all events")
    void getAllEvents_ReturnsList() {
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(
                    EVENT.ID, EVENT.CREATED_BY_USER_ID, EVENT.NAME, EVENT.DESCRIPTION,
                    EVENT.THUMBNAIL_URL, EVENT.STARTING_AT, EVENT.ENDING_AT, EVENT.CREATED_AT);
            var record = dsl.newRecord(
                    EVENT.ID, EVENT.CREATED_BY_USER_ID, EVENT.NAME, EVENT.DESCRIPTION,
                    EVENT.THUMBNAIL_URL, EVENT.STARTING_AT, EVENT.ENDING_AT, EVENT.CREATED_AT);
            record.values(eventId, creatorId, "E1", "D1", "U1", now, now.plusHours(2), now);
            result.add(record);
            return new MockResult[]{new MockResult(1, result)};
        };

        EventPersistenceImpl persistence = createPersistence(provider);
        List<Event> result = persistence.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("E1", result.get(0).getName());
    }

    @Test
    @DisplayName("deleteEvent - Executes delete")
    void deleteEvent_ExecutesDelete() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        EventPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.deleteEvent(eventId));
    }

    @Test
    @DisplayName("updateEventName - Executes update")
    void updateEventName_ExecutesUpdate() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        EventPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.updateEventName(eventId, "New Name"));
    }

    @Test
    @DisplayName("updateEventDescription - Executes update")
    void updateEventDescription_ExecutesUpdate() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        EventPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.updateEventDescription(eventId, "New Desc"));
    }

    @Test
    @DisplayName("updateEventThumbnail - Executes update")
    void updateEventThumbnail_ExecutesUpdate() {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, null)};
        EventPersistenceImpl persistence = createPersistence(provider);
        assertDoesNotThrow(() -> persistence.updateEventThumbnail(eventId, "New Url"));
    }
}
