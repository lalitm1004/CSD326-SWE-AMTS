package org.amts.domain.entities.coupon;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private final UUID id;
    private final UUID showId;
    private final UUID spectatorUserId;

    private final String code;

    private final LocalDateTime createdAt;

    public Coupon(
            UUID id,
            UUID showId,
            UUID spectatorUserId,
            String code,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Coupon id must not be null");
        this.showId = showId; // nullable — ON DELETE SET NULL
        this.spectatorUserId = spectatorUserId; // nullable — ON DELETE SET NULL

        Objects.requireNonNull(code, "Coupon code must not be null");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Coupon code must not be blank");
        }
        this.code = code;

        this.createdAt = Objects.requireNonNull(createdAt, "Coupon createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getShowId() {
        return showId;
    }

    public UUID getSpectatorUserId() {
        return spectatorUserId;
    }

    public String getCode() {
        return code;
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
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Coupon{id=" + id + ", code='" + code + "'}";
    }
}
