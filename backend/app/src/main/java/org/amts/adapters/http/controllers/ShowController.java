package org.amts.adapters.http.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.amts.application.usecases.event.ShowManagementUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/show")
public class ShowController {

    private final ShowManagementUseCase showManagement;

    public ShowController(ShowManagementUseCase showManagement) {
        this.showManagement = showManagement;
    }

    record AddShowToEventRequest(
            UUID createdByUserId,
            UUID eventId,
            String name,
            String description,
            String thumbnailUrl,
            LocalDateTime startingAt,
            LocalDateTime endingAt,
            double ordinarySeatPrice,
            double balconySeatPrice,
            int numOrdinarySeats,
            int numBalconySeats
    ) {}

    record DeleteShowRequest(
            UUID userId,
            UUID showId
    ) {}

    record UpdateShowNameRequest(
            UUID userId,
            UUID showId,
            String newName
    ) {}

    record UpdateShowDescriptionRequest(
            UUID userId,
            UUID showId,
            String newDescription
    ) {}

    record UpdateShowThumbnailRequest(
            UUID userId,
            UUID showId,
            String newUrl
    ) {}

    record UpdateShowStartingAtRequest(
            UUID userId,
            UUID showId,
            LocalDateTime newStartingAt
    ) {}

    record UpdateShowEndingAtRequest(
            UUID userId,
            UUID showId,
            LocalDateTime newEndingAt
    ) {}

    record UpdateShowOrdinarySeatPriceRequest(
            UUID userId,
            UUID showId,
            double newOrdinarySeatPrice
    ) {}

    record UpdateShowBalconySeatPriceRequest(
            UUID userId,
            UUID showId,
            double newBalconySeatPrice
    ) {}

    record UpdateShowNumOrdinarySeatsRequest(
            UUID userId,
            UUID showId,
            int newNumOrdinarySeats
    ) {}

    record UpdateShowNumBalconySeatsRequest(
            UUID userId,
            UUID showId,
            int newNumBalconySeats
    ) {}

    record GetShowRequest(
            UUID userId,
            UUID showId
    ) {}

    record GetPublicShowRequest(
            UUID showId
    ) {}

    record GetShowsByEventRequest(
            UUID userId,
            UUID eventId
    ) {}

    record GetPublicShowsByEventRequest(
            UUID eventId
    ) {}

    record UpdateSeatDesignationRequest(
            UUID userId,
            UUID seatId,
            SeatDesignation designation
    ) {}

    record GetSeatsByShowRequest(
            UUID userId,
            UUID showId
    ) {}

    @PostMapping
    public ResponseEntity<Void> addShowToEvent(@RequestBody AddShowToEventRequest request) {
        showManagement.addShowToEvent(
                request.createdByUserId(),
                request.eventId(),
                request.name(),
                request.description(),
                request.thumbnailUrl(),
                request.startingAt(),
                request.endingAt(),
                request.ordinarySeatPrice(),
                request.balconySeatPrice(),
                request.numOrdinarySeats(),
                request.numBalconySeats()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteShow(@RequestBody DeleteShowRequest request) {
        showManagement.deleteShow(request.userId(), request.showId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/name")
    public ResponseEntity<Void> updateShowName(@RequestBody UpdateShowNameRequest request) {
        showManagement.updateShowName(request.userId(), request.showId(), request.newName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> updateShowDescription(@RequestBody UpdateShowDescriptionRequest request) {
        showManagement.updateShowDescription(request.userId(), request.showId(), request.newDescription());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/thumbnail")
    public ResponseEntity<Void> updateShowThumbnail(@RequestBody UpdateShowThumbnailRequest request) {
        showManagement.updateShowThumbnail(request.userId(), request.showId(), request.newUrl());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/starting-at")
    public ResponseEntity<Void> updateShowStartingAt(@RequestBody UpdateShowStartingAtRequest request) {
        showManagement.updateShowStartingAt(request.userId(), request.showId(), request.newStartingAt());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ending-at")
    public ResponseEntity<Void> updateShowEndingAt(@RequestBody UpdateShowEndingAtRequest request) {
        showManagement.updateShowEndingAt(request.userId(), request.showId(), request.newEndingAt());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ordinary-seat-price")
    public ResponseEntity<Void> updateShowOrdinarySeatPrice(@RequestBody UpdateShowOrdinarySeatPriceRequest request) {
        showManagement.updateShowOrdinarySeatPrice(request.userId(), request.showId(), request.newOrdinarySeatPrice());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/balcony-seat-price")
    public ResponseEntity<Void> updateShowBalconySeatPrice(@RequestBody UpdateShowBalconySeatPriceRequest request) {
        showManagement.updateShowBalconySeatPrice(request.userId(), request.showId(), request.newBalconySeatPrice());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/num-ordinary-seats")
    public ResponseEntity<Void> updateShowNumOrdinarySeats(@RequestBody UpdateShowNumOrdinarySeatsRequest request) {
        showManagement.updateShowNumOrdinarySeats(request.userId(), request.showId(), request.newNumOrdinarySeats());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/num-balcony-seats")
    public ResponseEntity<Void> updateShowNumBalconySeats(@RequestBody UpdateShowNumBalconySeatsRequest request) {
        showManagement.updateShowNumBalconySeats(request.userId(), request.showId(), request.newNumBalconySeats());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get")
    public ResponseEntity<Show> getShowById(@RequestBody GetShowRequest request) {
        return showManagement.getShowByID(request.userId(), request.showId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/public/get")
    public ResponseEntity<Show> getPublicShowById(@RequestBody GetPublicShowRequest request) {
        return showManagement.getPublicShowByID(request.showId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/by-event")
    public ResponseEntity<ArrayList<Show>> getShowsByEvent(@RequestBody GetShowsByEventRequest request) {
        return showManagement.getShowsByEvent(request.userId(), request.eventId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/public/by-event")
    public ResponseEntity<ArrayList<Show>> getPublicShowsByEvent(@RequestBody GetPublicShowsByEventRequest request) {
        return showManagement.getPublicShowsByEvent(request.eventId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/seat-designation")
    public ResponseEntity<Void> updateSeatDesignation(@RequestBody UpdateSeatDesignationRequest request) {
        showManagement.updateSeatDesignation(request.userId(), request.seatId(), request.designation());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seats")
    public ResponseEntity<List<Seat>> getSeatsByShow(@RequestBody GetSeatsByShowRequest request) {
        return ResponseEntity.ok(showManagement.getSeatsByShow(request.userId(), request.showId()));
    }
}
