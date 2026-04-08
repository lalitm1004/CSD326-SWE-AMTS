package org.amts.adapters.usecases.event;

import static org.amts.jooq.Tables.EVENT;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.domain.entities.event.Event;
import org.amts.jooq.tables.records.EventRecord;
import org.jooq.DSLContext;

public class EventPersistenceImpl implements EventPersistenceUseCase{
    private final DSLContext dsl;

    public EventPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void createEvent(
        UUID createdByUserId, 
        String name, 
        String description, 
        String thumbnailUrl
    ) {
        EventRecord newEvent = dsl.newRecord(EVENT);

        newEvent.setId(UUID.randomUUID());
        newEvent.setCreatedByUserId(createdByUserId);
        newEvent.setName(name);
        newEvent.setDescription(description);
        newEvent.setThumbnailUrl(thumbnailUrl);

        newEvent.store();
    }

    @Override
    public void deleteEvent(UUID eventId) {
        dsl.deleteFrom(EVENT)
        .where(EVENT.ID.eq(eventId))
        .execute();
    }

    @Override
    public void updateEventName(UUID eventId, String newName) {
        dsl.update(EVENT)
        .set(EVENT.NAME, newName)
        .where(EVENT.ID.eq(eventId))
        .execute();
    }

    @Override
    public void updateEventDescription(UUID eventId, String newDescription) {
        dsl.update(EVENT)
        .set(EVENT.DESCRIPTION, newDescription)
        .where(EVENT.ID.eq(eventId))
        .execute();
    }

    @Override
    public void updateEventThumbnail(UUID eventId, String newUrl) {
        dsl.update(EVENT)
        .set(EVENT.THUMBNAIL_URL, newUrl)
        .where(EVENT.ID.eq(eventId))
        .execute();
    }

    @Override
    public Optional<Event> getEventByID(UUID eventId) {
        var result = dsl.selectFrom(EVENT)
            .where(EVENT.ID.eq(eventId))
            .fetchOptionalInto(Event.class);
        
        return result;
    }
    
    @Override
    public ArrayList<Event> getAllEvents() {
        var result = new ArrayList<>(
            dsl.selectFrom(EVENT)
            .fetchInto(Event.class)
        );

        return result;
    }
}
