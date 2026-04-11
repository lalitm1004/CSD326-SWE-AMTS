package org.amts.application.usecases.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Show;

public interface ShowManagementUseCase {
    void addShowToEvent(
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
    );

    void deleteShow(UUID userId, UUID showId);

    void updateShowName(UUID userId, UUID showId, String newName);

    void updateShowDescription(UUID userId, UUID showId, String newDescription);

    void updateShowThumbnail(UUID userId, UUID showId, String newUrl);

    void updateShowStartingAt(UUID userId, UUID showId, LocalDateTime newStartDateTime);

    void updateShowEndingAt(UUID userId, UUID showId, LocalDateTime newStartDateTime);

    void updateShowOrdinarySeatPrice(UUID userId, UUID showId, double newOrdinarySeatPrice);

    void updateShowBalconySeatPrice(UUID userId, UUID showId, double newBalconySeatPrice);

    void updateShowNumOrdinarySeats(UUID userId, UUID showId, int newNumOrdinarySeats);

    void updateShowNumBalconySeats(UUID userId, UUID showId, int newNumBalconySeats);

    Optional<Show> getShowByID(UUID userId, UUID showId);
    
    Optional<ArrayList<Show>> getShowsByEvent(UUID userId, UUID eventId);
}
