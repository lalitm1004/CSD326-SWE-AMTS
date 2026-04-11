package org.amts.adapters.usecases.event;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Event;
import org.amts.domain.entities.user.Role;
import org.amts.application.usecases.event.EventManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.adapters.usecases.AuthorizationHelper;

public class EventManagementImpl implements EventManagementUseCase {
    private final EventPersistenceUseCase eventPersistence;
    private final UserPersistenceUseCase userPersistence;

    public EventManagementImpl(
            EventPersistenceUseCase eventPersistence,
            UserPersistenceUseCase userPersistence) {
        this.eventPersistence = eventPersistence;
        this.userPersistence = userPersistence;
    }

    public void createEvent(UUID createdByUserId, String name, String description, String thumbnailUrl) {
        AuthorizationHelper.getAuthorizedUser(createdByUserId, userPersistence, Role.AUDITORIUM_SECRETARY,
                Role.SHOW_MANAGER);
        eventPersistence.createEvent(createdByUserId, name, description, thumbnailUrl);
    }

    public void deleteEvent(UUID userId, UUID eventId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER);
        eventPersistence.deleteEvent(eventId);
    }

    public void updateEventName(UUID userId, UUID eventId, String newName) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER);
        eventPersistence.updateEventName(eventId, newName);
    }

    public void updateEventDescription(UUID userId, UUID eventId, String newDescription) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER);
        eventPersistence.updateEventDescription(eventId, newDescription);
    }

    public void updateEventThumbnail(UUID userId, UUID eventId, String newUrl) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER);
        eventPersistence.updateEventThumbnail(eventId, newUrl);
    }

    public Optional<Event> getEventByID(UUID userId, UUID eventId) {
        return eventPersistence.getEventByID(eventId);
    }

    public ArrayList<Event> getAllEvents(UUID userId) {
        return eventPersistence.getAllEvents();
    }
}