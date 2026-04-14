package org.amts.adapters.usecases.event;

import static org.amts.jooq.Tables.SEAT;
import static org.amts.jooq.Tables.SHOW;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.amts.domain.valueobjects.Money;
import org.amts.jooq.enums.Seatdesignationenum;
import org.amts.jooq.tables.records.ShowRecord;
import org.jooq.DSLContext;

public class ShowPersistenceImpl implements ShowPersistenceUseCase {
    private final DSLContext dsl;

    public ShowPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private Show fromRecord(ShowRecord r) {
        return new Show(
                r.getId(),
                r.getEventId(),
                r.getCreatedByUserId(),
                r.getName(),
                r.getDescription(),
                r.getThumbnailUrl(),
                r.getStartingAt(),
                r.getEndingAt(),
                Money.of(r.getOrdinarySeatPrice()),
                Money.of(r.getBalconySeatPrice()),
                r.getNumOrdinarySeats(),
                r.getNumBalconySeats(),
                r.getCreatedAt()
        );
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
        ShowRecord newShow = dsl.newRecord(SHOW);

        newShow.setId(UUID.randomUUID());
        newShow.setEventId(eventId);
        newShow.setCreatedByUserId(createdByUserId);
        newShow.setName(name);
        newShow.setDescription(description);
        newShow.setThumbnailUrl(thumbnailUrl);
        newShow.setStartingAt(startingAt);
        newShow.setEndingAt(endingAt);
        newShow.setOrdinarySeatPrice(ordinarySeatPrice);
        newShow.setBalconySeatPrice(balconySeatPrice);
        newShow.setNumOrdinarySeats(numOrdinarySeats);
        newShow.setNumBalconySeats(numBalconySeats);

        newShow.store();
    }

    @Override
    public void deleteShow(UUID showId) {
        dsl.deleteFrom(SHOW)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowName(UUID showId, String newName) {
        dsl.update(SHOW)
            .set(SHOW.NAME, newName)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowDescription(UUID showId, String newDescription) {
        dsl.update(SHOW)
            .set(SHOW.DESCRIPTION, newDescription)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowThumbnail(UUID showId, String newUrl) {
        dsl.update(SHOW)
            .set(SHOW.THUMBNAIL_URL, newUrl)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowStartingAt(UUID showId, LocalDateTime newStartingAt) {
        dsl.update(SHOW)
            .set(SHOW.STARTING_AT, newStartingAt)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowEndingAt(UUID showId, LocalDateTime newEndingAt) {
        dsl.update(SHOW)
            .set(SHOW.ENDING_AT, newEndingAt)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowOrdinarySeatPrice(UUID showId, double newOrdinarySeatPrice) {
        dsl.update(SHOW)
            .set(SHOW.ORDINARY_SEAT_PRICE, newOrdinarySeatPrice)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowBalconySeatPrice(UUID showId, double newBalconySeatPrice) {
        dsl.update(SHOW)
            .set(SHOW.BALCONY_SEAT_PRICE, newBalconySeatPrice)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowNumOrdinarySeats(UUID showId, int newNumOrdinarySeats) {
        dsl.update(SHOW)
            .set(SHOW.NUM_ORDINARY_SEATS, newNumOrdinarySeats)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public void updateShowNumBalconySeats(UUID showId, int newNumBalconySeats) {
        dsl.update(SHOW)
            .set(SHOW.NUM_BALCONY_SEATS, newNumBalconySeats)
            .where(SHOW.ID.eq(showId))
            .execute();
    }

    @Override
    public Optional<Show> getShowByID(UUID showId) {
        return dsl.selectFrom(SHOW)
            .where(SHOW.ID.eq(showId))
            .fetchOptional()
            .map(this::fromRecord);
    }

    @Override
    public Optional<ArrayList<Show>> getShowsByEvent(UUID eventId) {
        var records = dsl.selectFrom(SHOW)
            .where(SHOW.EVENT_ID.eq(eventId))
            .fetch();

        if (records.isEmpty()) {
            return Optional.empty();
        }
        var result = new ArrayList<Show>(records.size());
        for (ShowRecord r : records) {
            result.add(fromRecord(r));
        }
        return Optional.of(result);
    }

    @Override
    public void updateSeatDesignation(UUID seatId, SeatDesignation designation) {
        dsl.update(SEAT)
            .set(SEAT.DESIGNATION, Seatdesignationenum.valueOf(designation.name()))
            .where(SEAT.ID.eq(seatId))
            .execute();
    }

    @Override
    public List<Seat> getSeatsByShow(UUID showId) {
        return dsl.selectFrom(SEAT)
            .orderBy(SEAT.NUMBER)
            .fetch()
            .map(r -> new Seat(
                r.getId(),
                r.getNumber(),
                SeatType.valueOf(r.getType().name()),
                SeatDesignation.valueOf(r.getDesignation().name())
            ));
    }

}