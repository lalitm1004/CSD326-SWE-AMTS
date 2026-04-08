package org.amts.application.usecases.event;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Event;

public interface EventManagementUseCase{
    void createEvent(
        UUID createdByUserId, 
        String name, 
        String description, 
        String thumbnailUrl
    );

    void deleteEvent(UUID userId, UUID eventId);

    void updateEventName(UUID userId, UUID EventId, String newName);
    
    void updateEventDescription(
        UUID userId, 
        UUID EventId, 
        String newDescription
    );

    void updateEventThumbnail(UUID userId, UUID EventId, String newUrl);

    Optional<Event> getEventByID(UUID userId, UUID EventId);

    ArrayList<Event> getAllEvents(UUID userId);
}