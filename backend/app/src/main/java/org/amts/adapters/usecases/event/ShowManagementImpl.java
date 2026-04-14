package org.amts.adapters.usecases.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.event.ShowManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.user.Role;

public class ShowManagementImpl implements ShowManagementUseCase {
    private final ShowPersistenceUseCase showPersistence;
    private final UserPersistenceUseCase userPersistence;

    public ShowManagementImpl(
            ShowPersistenceUseCase showPersistence,
            UserPersistenceUseCase userPersistence
    ) {
        this.showPersistence = showPersistence;
        this.userPersistence = userPersistence;
    }

    @Override
    public void addShowToEvent(
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
    ) {
        AuthorizationHelper.getAuthorizedUser(
            createdByUserId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.addShowToEvent(
            createdByUserId,
            eventId,
            name,
            description,
            thumbnailUrl,
            startingAt,
            endingAt,
            ordinarySeatPrice,
            balconySeatPrice,
            numOrdinarySeats,
            numBalconySeats
        );
    }

    @Override
    public void deleteShow(UUID userId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.deleteShow(showId);
    }

    @Override
    public void updateShowName(UUID userId, UUID showId, String newName) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowName(showId, newName);
    }

    @Override
    public void updateShowDescription(UUID userId, UUID showId, String newDescription) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowDescription(showId, newDescription);
    }

    @Override
    public void updateShowThumbnail(UUID userId, UUID showId, String newUrl) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowThumbnail(showId, newUrl);
    }

    @Override
    public void updateShowStartingAt(UUID userId, UUID showId, LocalDateTime newStartingAt) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowStartingAt(showId, newStartingAt);
    }

    @Override
    public void updateShowEndingAt(UUID userId, UUID showId, LocalDateTime newEndingAt) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowEndingAt(showId, newEndingAt);
    }

    @Override
    public void updateShowOrdinarySeatPrice(UUID userId, UUID showId, double newOrdinarySeatPrice) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowOrdinarySeatPrice(showId, newOrdinarySeatPrice);
    }

    @Override
    public void updateShowBalconySeatPrice(UUID userId, UUID showId, double newBalconySeatPrice) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowBalconySeatPrice(showId, newBalconySeatPrice);
    }

    @Override
    public void updateShowNumOrdinarySeats(UUID userId, UUID showId, int newNumOrdinarySeats) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowNumOrdinarySeats(showId, newNumOrdinarySeats);
    }

    @Override
    public void updateShowNumBalconySeats(UUID userId, UUID showId, int newNumBalconySeats) {
        AuthorizationHelper.getAuthorizedUser(
            userId,
            userPersistence,
            Role.AUDITORIUM_SECRETARY,
            Role.SHOW_MANAGER
        );
        showPersistence.updateShowNumBalconySeats(showId, newNumBalconySeats);
    }

    @Override
    public Optional<Show> getShowByID(UUID userId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        return showPersistence.getShowByID(showId);
    }

    @Override
    public Optional<ArrayList<Show>> getShowsByEvent(UUID userId, UUID eventId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        return showPersistence.getShowsByEvent(eventId);
    }

    @Override
    public void updateSeatDesignation(UUID userId, UUID seatId, SeatDesignation designation) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence, Role.SHOW_MANAGER, Role.AUDITORIUM_SECRETARY);
        showPersistence.updateSeatDesignation(seatId, designation);
    }

    @Override
    public List<Seat> getSeatsByShow(UUID userId, UUID showId) {
        AuthorizationHelper.getAuthorizedUser(userId, userPersistence);
        return showPersistence.getSeatsByShow(showId);
    }
}
