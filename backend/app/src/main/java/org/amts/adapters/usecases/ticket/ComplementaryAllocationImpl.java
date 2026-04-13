package org.amts.adapters.usecases.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.ticket.ComplementaryAllocationUseCase;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.user.Role;
import org.amts.jooq.enums.Seatdesignationenum;
import static org.amts.jooq.Tables.SEAT;
import static org.amts.jooq.Tables.SHOW;
import org.jooq.DSLContext;

public class ComplementaryAllocationImpl implements ComplementaryAllocationUseCase {

    private final DSLContext dsl;
    private final TicketPersistenceUseCase persistence;
    private final UserPersistenceUseCase userPersistence;

    public ComplementaryAllocationImpl(
            DSLContext dsl,
            TicketPersistenceUseCase persistence,
            UserPersistenceUseCase userPersistence
    ) {
        this.dsl = dsl;
        this.persistence = persistence;
        this.userPersistence = userPersistence;
    }

    @Override
    public List<Ticket> allocateComplementaryTickets(
            UUID allocatedByUserId,
            UUID showId,
            List<UUID> seatIds
    ) {
        AuthorizationHelper.getAuthorizedUser(allocatedByUserId, userPersistence, Role.AUDITORIUM_SECRETARY);

        var show = dsl.selectFrom(SHOW)
                .where(SHOW.ID.eq(showId))
                .fetchOne();

        if (show == null) {
            throw new IllegalArgumentException("Show not found: " + showId);
        }

        var seats = dsl.selectFrom(SEAT)
                .where(SEAT.ID.in(seatIds))
                .fetch();

        boolean hasNonComplementary = seats.stream()
                .anyMatch(s -> s.getDesignation() != Seatdesignationenum.COMPLIMENTARY);
        if (hasNonComplementary) {
            throw new IllegalArgumentException("All seats must have COMPLIMENTARY designation for this allocation");
        }

        UUID bookingId = UUID.randomUUID();
        List<Ticket> tickets = generateTickets(bookingId, showId, seatIds);

        persistence.saveComplementaryBookingWithTickets(bookingId, showId, allocatedByUserId, tickets);

        return tickets;
    }

    private List<Ticket> generateTickets(UUID bookingId, UUID showId, List<UUID> seatIds) {
        List<Ticket> tickets = new ArrayList<>();
        for (UUID seatId : seatIds) {
            tickets.add(new Ticket(
                    UUID.randomUUID(),
                    bookingId,
                    showId,
                    seatId,
                    UUID.randomUUID().toString().substring(0, 8),
                    false,
                    LocalDateTime.now()
            ));
        }
        return tickets;
    }
}
