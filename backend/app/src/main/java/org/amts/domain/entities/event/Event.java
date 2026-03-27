package org.amts.domain.entities.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Event {

    private final UUID id;
    private final UUID createdByUserId;

    private final String name;
    private final String description;
    private final String thumbnailUrl;

    private final List<Show> shows;

    private final LocalDateTime createdAt;

    public Event(
            UUID id,
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl,
            List<Show> shows,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Event id must not be null");
        this.createdByUserId = createdByUserId; // nullable — ON DELETE SET NULL in DB

        Objects.requireNonNull(name, "Event name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Event name must not be blank");
        }
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;

        this.shows = new ArrayList<>(
                Objects.requireNonNull(shows, "Event shows must not be null"));

        this.createdAt = Objects.requireNonNull(createdAt, "Event createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public List<Show> getShows() {
        return Collections.unmodifiableList(shows);
    }

    /**
     * Derived from the earliest show start time.
     * Returns empty if the event has no shows.
     */
    public Optional<LocalDateTime> getStartingAt() {
        return shows.stream()
                .map(Show::getStartingAt)
                .min(Comparator.naturalOrder());
    }

    /**
     * Derived from the latest show end time.
     * Returns empty if the event has no shows.
     */
    public Optional<LocalDateTime> getEndingAt() {
        return shows.stream()
                .map(Show::getEndingAt)
                .max(Comparator.naturalOrder());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Event{id=" + id + ", name='" + name + "'}";
    }
}