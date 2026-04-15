package org.amts.application.usecases.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.booking.BookingType;
import org.amts.domain.entities.booking.SpectatorBookingSummary;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

class TicketUseCaseTest {

    private final TicketPersistenceUseCase persistence = mock(TicketPersistenceUseCase.class);
    private final TicketPurchaseUseCase purchaseUseCase = mock(TicketPurchaseUseCase.class);
    private final UserPersistenceUseCase userPersistence = mock(UserPersistenceUseCase.class);
    private final TicketUseCase useCase = new TicketUseCase(persistence, purchaseUseCase, userPersistence);

    @Test
    void getBookingsBySpectatorUserIdReturnsBookingsForAuthorizedSpectator() {
        UUID spectatorId = UUID.randomUUID();
        User spectator = new User(spectatorId, "spectator@snu.edu.in", Set.of(Role.SPECTATOR), LocalDateTime.now());
        List<SpectatorBookingSummary> expected = List.of(
                new SpectatorBookingSummary(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "BOOK-001",
                        BookingType.ONLINE,
                        Money.of(500),
                        LocalDateTime.now(),
                        2,
                        0
                )
        );

        when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectator));
        when(persistence.getBookingsBySpectatorUserId(spectatorId)).thenReturn(expected);

        List<SpectatorBookingSummary> actual = useCase.getBookingsBySpectatorUserId(spectatorId);

        assertSame(expected, actual);
        verify(persistence).getBookingsBySpectatorUserId(spectatorId);
    }

    @Test
    void getBookingsBySpectatorUserIdRejectsUnauthorizedActor() {
        UUID spectatorId = UUID.randomUUID();
        User actor = new User(spectatorId, "agent@snu.edu.in", Set.of(Role.SALES_AGENT), LocalDateTime.now());

        when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(actor));

        PermissionException ex = assertThrows(
                PermissionException.class,
                () -> useCase.getBookingsBySpectatorUserId(spectatorId)
        );

        assertEquals("You do not have permission to perform this action.", ex.getMessage());
        verify(persistence, never()).getBookingsBySpectatorUserId(spectatorId);
    }
}
