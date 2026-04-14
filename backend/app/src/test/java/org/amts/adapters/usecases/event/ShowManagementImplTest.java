package org.amts.adapters.usecases.event;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowManagementImpl Tests")
class ShowManagementImplTest {

    @Mock
    private ShowPersistenceUseCase showPersistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    @InjectMocks
    private ShowManagementImpl showManagement;

    private UUID showManagerId;
    private UUID secretaryId;
    private UUID spectatorId;
    private UUID seatId;
    private UUID showId;
    private User showManagerUser;
    private User secretaryUser;
    private User spectatorUser;

    @BeforeEach
    void setUp() {
        showManagerId = UUID.randomUUID();
        secretaryId = UUID.randomUUID();
        spectatorId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        showId = UUID.randomUUID();

        showManagerUser = new User(showManagerId, "manager@test.com", Set.of(Role.SHOW_MANAGER, Role.SPECTATOR), LocalDateTime.now());
        secretaryUser = new User(secretaryId, "secretary@test.com", Set.of(Role.AUDITORIUM_SECRETARY, Role.SPECTATOR), LocalDateTime.now());
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
    }

    @Nested
    @DisplayName("Update Seat Designation Tests")
    class UpdateSeatDesignationTests {

        @Test
        @DisplayName("updateSeatDesignation - Show Manager can designate seat as VIP")
        void updateSeatDesignation_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() ->
                    showManagement.updateSeatDesignation(showManagerId, seatId, SeatDesignation.VIP));

            verify(showPersistence).updateSeatDesignation(eq(seatId), eq(SeatDesignation.VIP));
        }

        @Test
        @DisplayName("updateSeatDesignation - Auditorium Secretary can designate seat as Complimentary")
        void updateSeatDesignation_AuditoriumSecretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() ->
                    showManagement.updateSeatDesignation(secretaryId, seatId, SeatDesignation.COMPLIMENTARY));

            verify(showPersistence).updateSeatDesignation(eq(seatId), eq(SeatDesignation.COMPLIMENTARY));
        }

        @Test
        @DisplayName("updateSeatDesignation - Throws PermissionException for Spectator")
        void updateSeatDesignation_Spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    showManagement.updateSeatDesignation(spectatorId, seatId, SeatDesignation.VIP));

            verify(showPersistence, never()).updateSeatDesignation(any(), any());
        }

        @Test
        @DisplayName("updateSeatDesignation - Throws UserNotFoundException when user missing")
        void updateSeatDesignation_UserNotFound_ThrowsUserNotFoundException() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    showManagement.updateSeatDesignation(showManagerId, seatId, SeatDesignation.VIP));

            verify(showPersistence, never()).updateSeatDesignation(any(), any());
        }
    }

    @Nested
    @DisplayName("Get Seats By Show Tests")
    class GetSeatsByShowTests {

        @Test
        @DisplayName("getSeatsByShow - Returns seat list for any authenticated user")
        void getSeatsByShow_AnyAuthenticatedUser_ReturnsList() {
            List<Seat> seats = List.of(
                    new Seat(UUID.randomUUID(), "A1", SeatType.BALCONY, SeatDesignation.ORDINARY),
                    new Seat(UUID.randomUUID(), "B1", SeatType.ORDINARY, SeatDesignation.VIP)
            );
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(showPersistence.getSeatsByShow(showId)).thenReturn(seats);

            List<Seat> result = showManagement.getSeatsByShow(spectatorId, showId);

            assertEquals(2, result.size());
            verify(showPersistence).getSeatsByShow(eq(showId));
        }

        @Test
        @DisplayName("getSeatsByShow - Throws UserNotFoundException when user missing")
        void getSeatsByShow_UserNotFound_ThrowsUserNotFoundException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    showManagement.getSeatsByShow(spectatorId, showId));

            verify(showPersistence, never()).getSeatsByShow(any());
        }

        @Test
        @DisplayName("getSeatsByShow - Returns empty list when no seats exist")
        void getSeatsByShow_NoSeats_ReturnsEmptyList() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(showPersistence.getSeatsByShow(showId)).thenReturn(List.of());

            List<Seat> result = showManagement.getSeatsByShow(spectatorId, showId);

            assertTrue(result.isEmpty());
            verify(showPersistence).getSeatsByShow(eq(showId));
        }
    }

    @Nested
    @DisplayName("Reset Seat Designation Tests")
    class ResetSeatDesignationTests {

        @Test
        @DisplayName("updateSeatDesignation - Show Manager can reset seat back to ORDINARY")
        void updateSeatDesignation_ShowManager_ResetsToOrdinary() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() ->
                    showManagement.updateSeatDesignation(showManagerId, seatId, SeatDesignation.ORDINARY));

            verify(showPersistence).updateSeatDesignation(eq(seatId), eq(SeatDesignation.ORDINARY));
        }

        @Test
        @DisplayName("updateSeatDesignation - Auditorium Secretary can reset seat back to ORDINARY")
        void updateSeatDesignation_AuditoriumSecretary_ResetsToOrdinary() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() ->
                    showManagement.updateSeatDesignation(secretaryId, seatId, SeatDesignation.ORDINARY));

            verify(showPersistence).updateSeatDesignation(eq(seatId), eq(SeatDesignation.ORDINARY));
        }
    }
}
