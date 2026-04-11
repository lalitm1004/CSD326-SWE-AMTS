package org.amts.adapters.http.controllers;

import java.util.List;
import java.util.UUID;

import org.amts.application.usecases.event.EventManagementUseCase;
import org.amts.domain.entities.event.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventManagementUseCase eventPersistence;

    public EventController(EventManagementUseCase eventPersistence) {
        this.eventPersistence = eventPersistence;
    }

    record CreateEventRequest(
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl
    ) {}

    record DeleteEventRequest(
            UUID userId,
            UUID eventId
    ) {}

    record UpdateEventNameRequest(
            UUID userId,
            UUID eventId,
            String newName
    ) {}

    record UpdateEventDescriptionRequest(
            UUID userId,
            UUID eventId,
            String newDescription
    ) {}

    record UpdateEventThumbnailRequest(
            UUID userId,
            UUID eventId,
            String newUrl
    ) {}

    record GetEventRequest(
            UUID userId,
            UUID eventId
    ) {}

    record GetAllEventsRequest(
            UUID userId
    ) {}

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody CreateEventRequest request) {
        eventPersistence.createEvent(
                request.createdByUserId(),
                request.name(),
                request.description(),
                request.thumbnailUrl()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEvent(@RequestBody DeleteEventRequest request) {
        eventPersistence.deleteEvent(
                request.userId(),
                request.eventId()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/name")
    public ResponseEntity<Void> updateEventName(@RequestBody UpdateEventNameRequest request) {
        eventPersistence.updateEventName(
                request.userId(),
                request.eventId(),
                request.newName()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> updateEventDescription(@RequestBody UpdateEventDescriptionRequest request) {
        eventPersistence.updateEventDescription(
                request.userId(),
                request.eventId(),
                request.newDescription()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/thumbnail")
    public ResponseEntity<Void> updateEventThumbnail(@RequestBody UpdateEventThumbnailRequest request) {
        eventPersistence.updateEventThumbnail(
                request.userId(),
                request.eventId(),
                request.newUrl()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get")
    public ResponseEntity<Event> getEventById(@RequestBody GetEventRequest request) {
        return eventPersistence.getEventByID(
                request.userId(),
                request.eventId()
        )
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents(@RequestBody GetAllEventsRequest request) {
        return ResponseEntity.ok(
                eventPersistence.getAllEvents(request.userId())
        );
    }
}