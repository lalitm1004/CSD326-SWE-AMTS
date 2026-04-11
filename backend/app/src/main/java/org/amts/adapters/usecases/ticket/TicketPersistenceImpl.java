package org.amts.adapters.usecases.ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;
import static org.amts.jooq.Tables.TICKET;
import static org.amts.jooq.Tables.TICKET_REFUND;
import org.jooq.DSLContext;

public class TicketPersistenceImpl implements TicketPersistenceUseCase {

    private final DSLContext dsl;

    public TicketPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Ticket> getTicketById(UUID ticketId) {

        var record = dsl.selectFrom(TICKET)
                .where(TICKET.ID.eq(ticketId))
                .fetchOne();

        if (record == null) return Optional.empty();

        return Optional.of(new Ticket(
                record.getId(),
                record.getBookingId(),
                record.getShowId(),
                record.getSeatId(),
                record.getCode(),
                record.getIsRefunded(),
                record.getCreatedAt()
        ));
    }

    @Override
    public List<Ticket> getTicketsByBookingId(UUID bookingId) {

        var result = dsl.selectFrom(TICKET)
                .where(TICKET.BOOKING_ID.eq(bookingId))
                .fetch();

        return result.map(record -> new Ticket(
                record.getId(),
                record.getBookingId(),
                record.getShowId(),
                record.getSeatId(),
                record.getCode(),
                record.getIsRefunded(),
                record.getCreatedAt()
        ));
    }

    @Override
    public void saveTickets(List<Ticket> tickets) {

        var queries = tickets.stream()
                .map(ticket -> dsl.insertInto(TICKET)
                        .set(TICKET.ID, ticket.getId())
                        .set(TICKET.BOOKING_ID, ticket.getBookingId())
                        .set(TICKET.SHOW_ID, ticket.getShowId())
                        .set(TICKET.SEAT_ID, ticket.getSeatId())
                        .set(TICKET.CODE, ticket.getCode())
                        .set(TICKET.IS_REFUNDED, ticket.isRefunded())
                        .set(TICKET.CREATED_AT, ticket.getCreatedAt()))
                .toArray(org.jooq.Query[]::new);

        dsl.batch(queries).execute();
    }

    @Override
    public void markTicketAsRefunded(UUID ticketId) {

        dsl.update(TICKET)
                .set(TICKET.IS_REFUNDED, true)
                .where(TICKET.ID.eq(ticketId))
                .execute();
    }

    @Override
    public void saveTicketRefund(TicketRefund refund) {

        dsl.insertInto(TICKET_REFUND)
                .set(TICKET_REFUND.TICKET_ID, refund.getTicketId())
                .set(TICKET_REFUND.TYPE,
                        org.amts.jooq.enums.Refundtype.valueOf(refund.getType().name()))
                .set(TICKET_REFUND.CREATED_AT, refund.getCreatedAt())
                .execute();
    }
}