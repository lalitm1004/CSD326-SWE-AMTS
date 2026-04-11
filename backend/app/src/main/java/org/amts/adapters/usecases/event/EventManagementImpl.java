package org.amts.adapters.usecases.event;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Event;
import org.amts.application.usecases.event.EventManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;

import org.amts.adapters.usecases.AuthorizationHelper;

public class EventManagementImpl implements EventManagementUseCase {
    private final EventPersistenceUseCase eventPersistence;
    private final UserPersistenceUseCase userPersistence;

    public EventManagementImpl(
            EventPersistenceUseCase eventPersistence,
            UserPersistenceUseCase userPersistence
    ) {
        this.eventPersistence = eventPersistence;
        this.userPersistence = userPersistence;
    }

    public void createEvent(
        UUID createdByUserId, 
        String name, 
        String description, 
        String thumbnailUrl
    ) {

        AuthorizationHelper.getAuthorizedUser(createdByUserId, userPersistence);
        eventPersistence.createEvent(
            createdByUserId,
            name,
            description,
            thumbnailUrl
        );
    }

    public void deleteEvent( UUID userId, UUID eventId) {
       AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        eventPersistence.deleteEvent(eventId);
    }

    public void updateEventName( UUID userId, UUID eventId, String newName) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        eventPersistence.updateEventName(eventId, newName);
    }

    public void updateEventDescription(
        UUID userId,
        UUID eventId,
        String newDescription
    ) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        eventPersistence.updateEventDescription(eventId, newDescription);
    }

    public void updateEventThumbnail(UUID userId, UUID eventId, String newUrl) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);

        eventPersistence.updateEventThumbnail(eventId, newUrl);
    }

    public Optional<Event> getEventByID(UUID userId, UUID eventId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        return eventPersistence.getEventByID(eventId);
    }

    public ArrayList<Event> getAllEvents(UUID userId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        return eventPersistence.getAllEvents();
    }
}