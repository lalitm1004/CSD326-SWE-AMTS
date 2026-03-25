package com.amts.events

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {

    private UUID id;
    private UUID createdByUserId;
    private String name;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public Event(
      UUID id,
      UUID createdByUserId,
      String name,
      String description,
      String thumbnailUrl,
      LocalDateTime createdAt
    ) {
        this.id = id;
        this.createdByUserId = createdByUserId;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
    }

    // getters
    public UUID getId() { return id; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}
