package org.amts.application.usecases.event.rawinterfaces;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.event.Show;

public interface ShowPersistenceUseCase {
    void addShowtoEvent(Show show, UUID eventID);

    void deleteShow(UUID showId);

    void updateShowName(UUID showId, String newName);

    void updateShowDescription(UUID showId, String newDescription);

    void updateShowThumbnail(UUID showId, String newUrl);

    void updateShowStartingAt(UUID showId, LocalDateTime newStartDateTime);

    void updateShowEndingAt(UUID showId, LocalDateTime newStartDateTime);

    void updateShowOrdinarySeatPrice(UUID showId, double newOrdinarySeatPrice);

    void updateShowBalconySeatPrice(UUID showId, double newBalconySeatPrice);

    void updateShowNumOrdinarySeats(UUID showId, int newNumOrdinarySeats);

    void updateShowNumBalconySeats(UUID showId, int newNumBalconySeats);

    Optional<Show> getShowByID(UUID showId);
    
    Optional<ArrayList<Show>> getShowsByEvent(UUID eventId);
}
