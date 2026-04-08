package org.amts.adapters.http.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public class EventDto {

    private final UUID id;
    private final UUID createdByUserId;

    private final String name;
    private final String description;
    private final String thumbnailUrl;

    private final LocalDateTime startingAt;
    private final LocalDateTime endingAt;
    private final LocalDateTime createdAt;

    public EventDto(
            UUID id,
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl,
            LocalDateTime startingAt,
            LocalDateTime endingAt,
            LocalDateTime createdAt) {

        this.id = id;
        this.createdByUserId = createdByUserId;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.startingAt = startingAt;
        this.endingAt = endingAt;
        this.createdAt = createdAt;
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

    public LocalDateTime getStartingAt() {
        return startingAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getEndingAt() {
        return endingAt;
    }
}