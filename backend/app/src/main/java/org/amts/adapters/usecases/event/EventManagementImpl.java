package org.amts.adapters.usecases.event;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.domain.entities.event.Event;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.application.usecases.event.EventManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;

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

    private User getAuthorizedUser(UUID userId) {
        User user = userPersistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.hasRolesAny(Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER)) {
            throw new RuntimeException("Unauthorized");
        }

        return user;
    }

    public void createEvent(
        UUID createdByUserId, 
        String name, 
        String description, 
        String thumbnailUrl
    ) {

        getAuthorizedUser(createdByUserId);
        eventPersistence.createEvent(
            createdByUserId,
            name,
            description,
            thumbnailUrl
        );
    }

    public void deleteEvent( UUID userId, UUID eventId) {
        getAuthorizedUser(userId);
        eventPersistence.deleteEvent(eventId);
    }

    public void updateEventName( UUID userId, UUID eventId, String newName) {
        getAuthorizedUser(userId);
        eventPersistence.updateEventName(eventId, newName);
    }

    public void updateEventDescription(
        UUID userId,
        UUID eventId,
        String newDescription
    ) {
        getAuthorizedUser(userId);
        eventPersistence.updateEventDescription(eventId, newDescription);
    }

    public void updateEventThumbnail(UUID userId, UUID eventId, String newUrl) {
        getAuthorizedUser(userId);

        eventPersistence.updateEventThumbnail(eventId, newUrl);
    }

    public Optional<Event> getEventByID(UUID userId, UUID eventId) {
        getAuthorizedUser(userId);
        return eventPersistence.getEventByID(eventId);
    }

    public ArrayList<Event> getAllEvents(UUID userId) {
        getAuthorizedUser(userId);
        return eventPersistence.getAllEvents();
    }
}