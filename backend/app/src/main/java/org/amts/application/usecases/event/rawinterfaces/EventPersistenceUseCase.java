package org.amts.application.usecases.event.rawinterfaces;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Event;

public interface EventPersistenceUseCase {
    void createEvent(
        UUID createdByUserId, 
        String name, 
        String description, 
        String thumbnailUrl
    );

    void deleteEvent(UUID eventId);

    void updateEventName(UUID eventId, String newName);

    void updateEventDescription(UUID eventId, String newDescription);

    void updateEventThumbnail(UUID eventId, String newUrl);

    Optional<Event> getEventByID(UUID eventId);
    
    ArrayList<Event> getAllEvents();
}
