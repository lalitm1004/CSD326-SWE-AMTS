package org.amts.adapters.usecases.event;

import static org.amts.jooq.Tables.EVENT;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.domain.entities.event.Event;
import org.amts.jooq.tables.records.EventRecord;
import org.jooq.DSLContext;

public class EventPersistenceImpl implements EventPersistenceUseCase {
    private final DSLContext dsl;

    public EventPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private Event fromRecord(EventRecord r) {
        return new Event(
                r.getId(),
                r.getCreatedByUserId(),
                r.getName(),
                r.getDescription(),
                r.getThumbnailUrl(),
                r.getStartingAt(),
                r.getEndingAt(),
                r.getCreatedAt()
        );
    }

    @Override
    public void createEvent(
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl) {
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
        return dsl.selectFrom(EVENT)
                .where(EVENT.ID.eq(eventId))
                .fetchOptional()
                .map(this::fromRecord);
    }

    @Override
    public ArrayList<Event> getAllEvents() {
        var records = dsl.selectFrom(EVENT).fetch();
        var result = new ArrayList<Event>(records.size());
        for (EventRecord r : records) {
            result.add(fromRecord(r));
        }
        return result;
    }
}
