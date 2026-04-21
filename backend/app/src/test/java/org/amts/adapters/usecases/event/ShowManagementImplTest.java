package org.amts.adapters.usecases.event;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.entities.seat.Seat;
import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private UUID eventId;
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
        eventId = UUID.randomUUID();

        showManagerUser = new User(showManagerId, "manager@test.com", Set.of(Role.SHOW_MANAGER, Role.SPECTATOR), LocalDateTime.now());
        secretaryUser = new User(secretaryId, "secretary@test.com", Set.of(Role.AUDITORIUM_SECRETARY, Role.SPECTATOR), LocalDateTime.now());
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
    }

    private Show sampleShow() {
        return new Show(
                showId,
                eventId,
                showManagerId,
                "Show Name",
                "Show Description",
                "https://example.com/thumb.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                Money.of(100.0),
                Money.of(200.0),
                100,
                50,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Add Show Tests")
    class AddShowTests {

        @Test
        @DisplayName("addShowToEvent - Show Manager succeeds")
        void addShowToEvent_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() -> showManagement.addShowToEvent(
                    showManagerId,
                    eventId,
                    "Show Name",
                    "Description",
                    "https://example.com/thumb.jpg",
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    100.0,
                    200.0,
                    100,
                    50
            ));

            verify(showPersistence).addShowToEvent(
                    eq(showManagerId),
                    eq(eventId),
                    eq("Show Name"),
                    eq("Description"),
                    eq("https://example.com/thumb.jpg"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    eq(100.0),
                    eq(200.0),
                    eq(100),
                    eq(50)
            );
        }

        @Test
        @DisplayName("addShowToEvent - Auditorium Secretary succeeds")
        void addShowToEvent_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> showManagement.addShowToEvent(
                    secretaryId,
                    eventId,
                    "Show Name",
                    "Description",
                    null,
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    100.0,
                    200.0,
                    100,
                    50
            ));

            verify(showPersistence).addShowToEvent(
                    eq(secretaryId),
                    eq(eventId),
                    eq("Show Name"),
                    eq("Description"),
                    eq(null),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    eq(100.0),
                    eq(200.0),
                    eq(100),
                    eq(50)
            );
        }

        @Test
        @DisplayName("addShowToEvent - Spectator throws PermissionException")
        void addShowToEvent_Spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () -> showManagement.addShowToEvent(
                    spectatorId,
                    eventId,
                    "Show Name",
                    "Description",
                    null,
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    100.0,
                    200.0,
                    100,
                    50
            ));

            verify(showPersistence, never()).addShowToEvent(any(), any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("addShowToEvent - Invalid schedule throws IllegalArgumentException")
        void addShowToEvent_InvalidSchedule_ThrowsIllegalArgumentException() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));
            LocalDateTime start = LocalDateTime.now().plusDays(1);

            assertThrows(IllegalArgumentException.class, () -> showManagement.addShowToEvent(
                    showManagerId,
                    eventId,
                    "Show Name",
                    "Description",
                    null,
                    start,
                    start,
                    100.0,
                    200.0,
                    100,
                    50
            ));

            verify(showPersistence, never()).addShowToEvent(any(), any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Show Mutation Tests")
    class ShowMutationTests {

        @Test
        @DisplayName("deleteShow - Show Manager succeeds")
        void deleteShow_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() -> showManagement.deleteShow(showManagerId, showId));

            verify(showPersistence).deleteShow(showId);
        }

        @Test
        @DisplayName("updateShowName - Secretary succeeds")
        void updateShowName_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> showManagement.updateShowName(secretaryId, showId, "New Name"));

            verify(showPersistence).updateShowName(showId, "New Name");
        }

        @Test
        @DisplayName("updateShowDescription - Show Manager succeeds")
        void updateShowDescription_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() -> showManagement.updateShowDescription(showManagerId, showId, "New Description"));

            verify(showPersistence).updateShowDescription(showId, "New Description");
        }

        @Test
        @DisplayName("updateShowThumbnail - Secretary succeeds")
        void updateShowThumbnail_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> showManagement.updateShowThumbnail(secretaryId, showId, "https://new.example/thumb.jpg"));

            verify(showPersistence).updateShowThumbnail(showId, "https://new.example/thumb.jpg");
        }

        @Test
        @DisplayName("updateShowStartingAt - Show Manager succeeds")
        void updateShowStartingAt_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));
            LocalDateTime newStart = LocalDateTime.now().plusDays(3);

            assertDoesNotThrow(() -> showManagement.updateShowStartingAt(showManagerId, showId, newStart));

            verify(showPersistence).updateShowStartingAt(showId, newStart);
        }

        @Test
        @DisplayName("updateShowEndingAt - Secretary succeeds")
        void updateShowEndingAt_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));
            when(showPersistence.getShowByID(showId)).thenReturn(Optional.of(sampleShow()));
            LocalDateTime newEnd = LocalDateTime.now().plusDays(3).plusHours(4);

            assertDoesNotThrow(() -> showManagement.updateShowEndingAt(secretaryId, showId, newEnd));

            verify(showPersistence).updateShowEndingAt(showId, newEnd);
        }

        @Test
        @DisplayName("updateShowOrdinarySeatPrice - Show Manager succeeds")
        void updateShowOrdinarySeatPrice_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() -> showManagement.updateShowOrdinarySeatPrice(showManagerId, showId, 125.0));

            verify(showPersistence).updateShowOrdinarySeatPrice(showId, 125.0);
        }

        @Test
        @DisplayName("updateShowBalconySeatPrice - Secretary succeeds")
        void updateShowBalconySeatPrice_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> showManagement.updateShowBalconySeatPrice(secretaryId, showId, 225.0));

            verify(showPersistence).updateShowBalconySeatPrice(showId, 225.0);
        }

        @Test
        @DisplayName("updateShowNumOrdinarySeats - Show Manager succeeds")
        void updateShowNumOrdinarySeats_ShowManager_Succeeds() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));

            assertDoesNotThrow(() -> showManagement.updateShowNumOrdinarySeats(showManagerId, showId, 120));

            verify(showPersistence).updateShowNumOrdinarySeats(showId, 120);
        }

        @Test
        @DisplayName("updateShowNumBalconySeats - Secretary succeeds")
        void updateShowNumBalconySeats_Secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> showManagement.updateShowNumBalconySeats(secretaryId, showId, 60));

            verify(showPersistence).updateShowNumBalconySeats(showId, 60);
        }

        @Test
        @DisplayName("show mutations - Spectator throws PermissionException")
        void showMutations_Spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () -> showManagement.updateShowName(spectatorId, showId, "Blocked"));

            verify(showPersistence, never()).updateShowName(any(), any());
        }

        @Test
        @DisplayName("updateShowStartingAt - Rejects schedule when new start is after existing end")
        void updateShowStartingAt_InvalidSchedule_ThrowsIllegalArgumentException() {
            when(userPersistence.getUserById(showManagerId)).thenReturn(Optional.of(showManagerUser));
            when(showPersistence.getShowByID(showId)).thenReturn(Optional.of(sampleShow()));

            assertThrows(IllegalArgumentException.class, () ->
                    showManagement.updateShowStartingAt(showManagerId, showId, LocalDateTime.now().plusDays(5)));

            verify(showPersistence, never()).updateShowStartingAt(any(), any());
        }

        @Test
        @DisplayName("updateShowEndingAt - Rejects schedule when new end is before existing start")
        void updateShowEndingAt_InvalidSchedule_ThrowsIllegalArgumentException() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));
            when(showPersistence.getShowByID(showId)).thenReturn(Optional.of(sampleShow()));

            assertThrows(IllegalArgumentException.class, () ->
                    showManagement.updateShowEndingAt(secretaryId, showId, LocalDateTime.now().plusDays(1)));

            verify(showPersistence, never()).updateShowEndingAt(any(), any());
        }
    }

    @Nested
    @DisplayName("Show Query Tests")
    class ShowQueryTests {

        @Test
        @DisplayName("getShowByID - Returns show for authenticated user")
        void getShowById_AuthenticatedUser_ReturnsShow() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(showPersistence.getShowByID(showId)).thenReturn(Optional.of(sampleShow()));

            Optional<Show> result = showManagement.getShowByID(spectatorId, showId);

            assertTrue(result.isPresent());
            assertEquals(showId, result.get().getId());
        }

        @Test
        @DisplayName("getPublicShowByID - Returns show without authentication")
        void getPublicShowById_AnonymousUser_ReturnsShow() {
            when(showPersistence.getShowByID(showId)).thenReturn(Optional.of(sampleShow()));

            Optional<Show> result = showManagement.getPublicShowByID(showId);

            assertTrue(result.isPresent());
            assertEquals(showId, result.get().getId());
            verify(userPersistence, never()).getUserById(any());
        }

        @Test
        @DisplayName("getShowsByEvent - Returns shows for authenticated user")
        void getShowsByEvent_AuthenticatedUser_ReturnsShows() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));
            when(showPersistence.getShowsByEvent(eventId)).thenReturn(Optional.of(new ArrayList<>(List.of(sampleShow()))));

            Optional<ArrayList<Show>> result = showManagement.getShowsByEvent(spectatorId, eventId);

            assertTrue(result.isPresent());
            assertEquals(1, result.get().size());
        }

        @Test
        @DisplayName("getPublicShowsByEvent - Returns shows without authentication")
        void getPublicShowsByEvent_AnonymousUser_ReturnsShows() {
            when(showPersistence.getShowsByEvent(eventId)).thenReturn(Optional.of(new ArrayList<>(List.of(sampleShow()))));

            Optional<ArrayList<Show>> result = showManagement.getPublicShowsByEvent(eventId);

            assertTrue(result.isPresent());
            assertEquals(1, result.get().size());
            verify(userPersistence, never()).getUserById(any());
        }

        @Test
        @DisplayName("getPublicShowsByEvent - Returns empty result when event has no shows")
        void getPublicShowsByEvent_NoShows_ReturnsEmpty() {
            when(showPersistence.getShowsByEvent(eventId)).thenReturn(Optional.empty());

            Optional<ArrayList<Show>> result = showManagement.getPublicShowsByEvent(eventId);

            assertTrue(result.isEmpty());
            verify(userPersistence, never()).getUserById(any());
        }

        @Test
        @DisplayName("getShowByID - Missing user throws UserNotFoundException")
        void getShowById_MissingUser_Throws() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> showManagement.getShowByID(spectatorId, showId));
        }
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
