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

    public EventController(
            EventManagementUseCase eventPersistence
    ) {
        this.eventPersistence = eventPersistence;
    }

    record CreateEventRequest(
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl
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
    public ResponseEntity<Void> deleteEvent(
            @RequestParam UUID userId,
            @RequestParam UUID eventId
    ) {
        eventPersistence.deleteEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/name")
    public ResponseEntity<Void> updateEventName(
            @RequestParam UUID userId,
            @RequestParam UUID eventId,
            @RequestParam String newName
    ) {

        eventPersistence.updateEventName(userId, eventId, newName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> updateEventDescription(
            @RequestParam UUID userId,
            @RequestParam UUID eventId,
            @RequestParam String newDescription
    ) {
        eventPersistence.updateEventDescription(userId, eventId, newDescription);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/thumbnail")
    public ResponseEntity<Void> updateEventThumbnail(
            @RequestParam UUID userId,
            @RequestParam UUID eventId,
            @RequestParam String newUrl
    ) {
        eventPersistence.updateEventThumbnail(userId, eventId, newUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Event> getEventById(
            @RequestParam UUID userId,
            @RequestParam UUID eventId
    ) {
        return eventPersistence.getEventByID(userId, eventId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(eventPersistence.getAllEvents(userId));
    }
}