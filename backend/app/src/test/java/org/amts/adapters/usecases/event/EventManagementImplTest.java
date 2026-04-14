package org.amts.adapters.usecases.event;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.event.Event;
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
@DisplayName("EventManagementImpl Tests")
class EventManagementImplTest {

    @Mock
    private EventPersistenceUseCase eventPersistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    @InjectMocks
    private EventManagementImpl eventManagement;

    private UUID secretaryId;
    private UUID managerId;
    private UUID spectatorId;
    private UUID eventId;
    private User secretaryUser;
    private User managerUser;
    private User spectatorUser;

    @BeforeEach
    void setUp() {
        secretaryId = UUID.randomUUID();
        managerId = UUID.randomUUID();
        spectatorId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        secretaryUser = new User(secretaryId, "secretary@test.com", Set.of(Role.AUDITORIUM_SECRETARY, Role.SPECTATOR), LocalDateTime.now());
        managerUser = new User(managerId, "manager@test.com", Set.of(Role.SHOW_MANAGER, Role.SPECTATOR), LocalDateTime.now());
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
    }

    @Nested
    @DisplayName("createEvent Tests")
    class CreateEventTests {

        @Test
        @DisplayName("createEvent - Auditorium Secretary succeeds")
        void secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() ->
                    eventManagement.createEvent(secretaryId, "Test Event", "Description", null));

            verify(eventPersistence).createEvent(eq(secretaryId), eq("Test Event"), eq("Description"), eq(null));
        }

        @Test
        @DisplayName("createEvent - Show Manager succeeds")
        void manager_Succeeds() {
            when(userPersistence.getUserById(managerId)).thenReturn(Optional.of(managerUser));

            assertDoesNotThrow(() ->
                    eventManagement.createEvent(managerId, "Test Event", "Description", null));

            verify(eventPersistence).createEvent(eq(managerId), any(), any(), any());
        }

        @Test
        @DisplayName("createEvent - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    eventManagement.createEvent(spectatorId, "Test Event", "Description", null));

            verify(eventPersistence, never()).createEvent(any(), any(), any(), any());
        }

        @Test
        @DisplayName("createEvent - Throws UserNotFoundException when user missing")
        void userNotFound_Throws() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    eventManagement.createEvent(secretaryId, "Test Event", "Description", null));

            verify(eventPersistence, never()).createEvent(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("deleteEvent Tests")
    class DeleteEventTests {

        @Test
        @DisplayName("deleteEvent - Auditorium Secretary succeeds")
        void secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> eventManagement.deleteEvent(secretaryId, eventId));

            verify(eventPersistence).deleteEvent(eq(eventId));
        }

        @Test
        @DisplayName("deleteEvent - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    eventManagement.deleteEvent(spectatorId, eventId));

            verify(eventPersistence, never()).deleteEvent(any());
        }
    }

    @Nested
    @DisplayName("updateEventName Tests")
    class UpdateEventNameTests {

        @Test
        @DisplayName("updateEventName - Auditorium Secretary succeeds")
        void secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> eventManagement.updateEventName(secretaryId, eventId, "New Name"));

            verify(eventPersistence).updateEventName(eq(eventId), eq("New Name"));
        }

        @Test
        @DisplayName("updateEventName - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    eventManagement.updateEventName(spectatorId, eventId, "New Name"));

            verify(eventPersistence, never()).updateEventName(any(), any());
        }
    }

    @Nested
    @DisplayName("updateEventDescription Tests")
    class UpdateEventDescriptionTests {

        @Test
        @DisplayName("updateEventDescription - Auditorium Secretary succeeds")
        void secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> eventManagement.updateEventDescription(secretaryId, eventId, "New Desc"));

            verify(eventPersistence).updateEventDescription(eq(eventId), eq("New Desc"));
        }

        @Test
        @DisplayName("updateEventDescription - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    eventManagement.updateEventDescription(spectatorId, eventId, "New Desc"));

            verify(eventPersistence, never()).updateEventDescription(any(), any());
        }
    }

    @Nested
    @DisplayName("updateEventThumbnail Tests")
    class UpdateEventThumbnailTests {

        @Test
        @DisplayName("updateEventThumbnail - Auditorium Secretary succeeds")
        void secretary_Succeeds() {
            when(userPersistence.getUserById(secretaryId)).thenReturn(Optional.of(secretaryUser));

            assertDoesNotThrow(() -> eventManagement.updateEventThumbnail(secretaryId, eventId, "https://img.example.com/thumb.jpg"));

            verify(eventPersistence).updateEventThumbnail(eq(eventId), eq("https://img.example.com/thumb.jpg"));
        }

        @Test
        @DisplayName("updateEventThumbnail - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () ->
                    eventManagement.updateEventThumbnail(spectatorId, eventId, "https://img.example.com/thumb.jpg"));

            verify(eventPersistence, never()).updateEventThumbnail(any(), any());
        }
    }

    @Nested
    @DisplayName("Query Tests")
    class QueryTests {

        @Test
        @DisplayName("getEventByID - Delegates to persistence (no auth required)")
        void getEventByID_AnyUser_Delegates() {
            Event event = new Event(eventId, secretaryId, "Event Name", null, null,
                    LocalDateTime.now(), LocalDateTime.now().plusHours(1), LocalDateTime.now());
            when(eventPersistence.getEventByID(eventId)).thenReturn(Optional.of(event));

            Optional<Event> result = eventManagement.getEventByID(spectatorId, eventId);

            assertTrue(result.isPresent());
            assertEquals(event, result.get());
            verify(userPersistence, never()).getUserById(any());
        }

        @Test
        @DisplayName("getAllEvents - Delegates to persistence (no auth required)")
        void getAllEvents_AnyUser_Delegates() {
            ArrayList<Event> events = new ArrayList<>(List.of(
                    new Event(UUID.randomUUID(), secretaryId, "Event A", null, null,
                            LocalDateTime.now(), LocalDateTime.now().plusHours(1), LocalDateTime.now())
            ));
            when(eventPersistence.getAllEvents()).thenReturn(events);

            ArrayList<Event> result = eventManagement.getAllEvents(spectatorId);

            assertEquals(1, result.size());
            verify(userPersistence, never()).getUserById(any());
        }
    }
}
