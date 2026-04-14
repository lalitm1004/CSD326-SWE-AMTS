package org.amts.adapters.usecases.sales;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.sales.rawinterfaces.SalesPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.event.Show;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesTrackingImpl Tests")
class SalesTrackingImplTest {

    @Mock
    private SalesPersistenceUseCase persistence;

    @Mock
    private UserPersistenceUseCase userPersistence;

    @Mock
    private ShowPersistenceUseCase showPersistence;

    @InjectMocks
    private SalesTrackingImpl salesTracking;

    private UUID presidentId;
    private UUID clerkId;
    private UUID agentId;
    private UUID otherAgentId;
    private UUID spectatorId;
    private UUID showId;
    private UUID eventId;
    private User presidentUser;
    private User clerkUser;
    private User agentUser;
    private User spectatorUser;

    @BeforeEach
    void setUp() {
        presidentId = UUID.randomUUID();
        clerkId = UUID.randomUUID();
        agentId = UUID.randomUUID();
        otherAgentId = UUID.randomUUID();
        spectatorId = UUID.randomUUID();
        showId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        presidentUser = new User(presidentId, "president@test.com", Set.of(Role.PRESIDENT, Role.SPECTATOR), LocalDateTime.now());
        clerkUser = new User(clerkId, "clerk@test.com", Set.of(Role.FINANCIAL_CLERK, Role.SPECTATOR), LocalDateTime.now());
        agentUser = new User(agentId, "agent@test.com", Set.of(Role.SALES_AGENT, Role.SPECTATOR), LocalDateTime.now());
        spectatorUser = new User(spectatorId, "spectator@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
    }

    @Nested
    @DisplayName("Get Revenue By Show Tests")
    class GetRevenueByShowTests {

        @Test
        @DisplayName("getRevenueByShow - President can view revenue")
        void president_CanView() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(persistence.getRevenueByShow(showId)).thenReturn(500.0);

            double result = salesTracking.getRevenueByShow(presidentId, showId);

            assertEquals(500.0, result);
        }

        @Test
        @DisplayName("getRevenueByShow - Financial Clerk can view revenue")
        void clerk_CanView() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.getRevenueByShow(showId)).thenReturn(300.0);

            double result = salesTracking.getRevenueByShow(clerkId, showId);

            assertEquals(300.0, result);
        }

        @Test
        @DisplayName("getRevenueByShow - Sales Agent is not authorized")
        void agent_ThrowsPermissionException() {
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));

            assertThrows(PermissionException.class, () -> salesTracking.getRevenueByShow(agentId, showId));
            verify(persistence, never()).getRevenueByShow(any());
        }

        @Test
        @DisplayName("getRevenueByShow - Spectator is not authorized")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () -> salesTracking.getRevenueByShow(spectatorId, showId));
            verify(persistence, never()).getRevenueByShow(any());
        }

        @Test
        @DisplayName("getRevenueByShow - Missing user throws UserNotFoundException")
        void userNotFound_Throws() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> salesTracking.getRevenueByShow(presidentId, showId));
        }
    }

    @Nested
    @DisplayName("Get Revenue By Event Tests")
    class GetRevenueByEventTests {

        @Test
        @DisplayName("getRevenueByEvent - President iterates shows and collects revenue per show")
        void president_IteratesShows() {
            UUID showId1 = UUID.randomUUID();
            UUID showId2 = UUID.randomUUID();
            Show show1 = new Show(showId1, eventId, presidentId, "Show 1", null, null,
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                    Money.of(10), Money.of(20), 50, 20, LocalDateTime.now());
            Show show2 = new Show(showId2, eventId, presidentId, "Show 2", null, null,
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                    Money.of(10), Money.of(20), 50, 20, LocalDateTime.now());

            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(showPersistence.getShowsByEvent(eventId)).thenReturn(Optional.of(new ArrayList<>(List.of(show1, show2))));
            when(persistence.getRevenueByShow(showId1)).thenReturn(100.0);
            when(persistence.getRevenueByShow(showId2)).thenReturn(200.0);

            var result = salesTracking.getRevenueByEvent(presidentId, eventId);

            assertEquals(2, result.size());
            assertEquals(100.0, result.get(showId1));
            assertEquals(200.0, result.get(showId2));
        }

        @Test
        @DisplayName("getRevenueByEvent - Financial Clerk can view event revenue")
        void clerk_CanView() {
            UUID eventShowId = UUID.randomUUID();
            Show show = new Show(eventShowId, eventId, clerkId, "Show", null, null,
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                    Money.of(10), Money.of(20), 50, 20, LocalDateTime.now());

            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(showPersistence.getShowsByEvent(eventId)).thenReturn(Optional.of(new ArrayList<>(List.of(show))));
            when(persistence.getRevenueByShow(eventShowId)).thenReturn(250.0);

            var result = salesTracking.getRevenueByEvent(clerkId, eventId);

            assertEquals(250.0, result.get(eventShowId));
        }

        @Test
        @DisplayName("getRevenueByEvent - Unauthorized user throws PermissionException")
        void unauthorized_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () -> salesTracking.getRevenueByEvent(spectatorId, eventId));
        }
    }

    @Nested
    @DisplayName("Get Offline Commission By Agent Tests")
    class GetOfflineCommissionByAgentTests {

        @Test
        @DisplayName("getOfflineCommissionByAgent - President can view any agent")
        void president_CanViewAnyAgent() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(persistence.getOfflineRevenueByAgent(agentId)).thenReturn(1000.0);

            double result = salesTracking.getOfflineCommissionByAgent(presidentId, agentId);

            assertEquals(10.0, result, 0.001);
        }

        @Test
        @DisplayName("getOfflineCommissionByAgent - Financial Clerk can view any agent")
        void clerk_CanViewAnyAgent() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.getOfflineRevenueByAgent(agentId)).thenReturn(500.0);

            double result = salesTracking.getOfflineCommissionByAgent(clerkId, agentId);

            assertEquals(5.0, result, 0.001);
        }

        @Test
        @DisplayName("getOfflineCommissionByAgent - Sales Agent can view own commission")
        void agent_CanViewOwnRevenue() {
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));
            when(persistence.getOfflineRevenueByAgent(agentId)).thenReturn(200.0);

            double result = salesTracking.getOfflineCommissionByAgent(agentId, agentId);

            assertEquals(2.0, result, 0.001);
        }

        @Test
        @DisplayName("getOfflineCommissionByAgent - Sales Agent cannot view another agent")
        void agent_CannotViewOtherAgent() {
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));

            assertThrows(SecurityException.class, () -> salesTracking.getOfflineCommissionByAgent(agentId, otherAgentId));
            verify(persistence, never()).getOfflineRevenueByAgent(any());
        }

        @Test
        @DisplayName("getOfflineCommissionByAgent - Spectator throws PermissionException")
        void spectator_ThrowsPermissionException() {
            when(userPersistence.getUserById(spectatorId)).thenReturn(Optional.of(spectatorUser));

            assertThrows(PermissionException.class, () -> salesTracking.getOfflineCommissionByAgent(spectatorId, agentId));
        }

        @Test
        @DisplayName("getOfflineCommissionByAgent - commission is 1 percent of revenue")
        void commission_IsOnePercent() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(persistence.getOfflineRevenueByAgent(agentId)).thenReturn(10000.0);

            double result = salesTracking.getOfflineCommissionByAgent(presidentId, agentId);

            assertEquals(100.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Get Offline Commission By Agent For Event Tests")
    class GetOfflineCommissionByAgentForEventTests {

        @Test
        @DisplayName("getOfflineCommissionByAgentForEvent - Sales Agent can view own commission")
        void agent_CanViewOwnForEvent() {
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));
            when(persistence.getOfflineRevenueByAgentForEvent(agentId, eventId)).thenReturn(400.0);

            double result = salesTracking.getOfflineCommissionByAgentForEvent(agentId, agentId, eventId);

            assertEquals(4.0, result, 0.001);
        }

        @Test
        @DisplayName("getOfflineCommissionByAgentForEvent - Sales Agent cannot view another agent")
        void agent_CannotViewOtherForEvent() {
            when(userPersistence.getUserById(agentId)).thenReturn(Optional.of(agentUser));

            assertThrows(SecurityException.class, () -> salesTracking.getOfflineCommissionByAgentForEvent(agentId, otherAgentId, eventId));
            verify(persistence, never()).getOfflineRevenueByAgentForEvent(any(), any());
        }

        @Test
        @DisplayName("getOfflineCommissionByAgentForEvent - President can view any agent")
        void president_CanViewAnyAgentForEvent() {
            when(userPersistence.getUserById(presidentId)).thenReturn(Optional.of(presidentUser));
            when(persistence.getOfflineRevenueByAgentForEvent(agentId, eventId)).thenReturn(600.0);

            double result = salesTracking.getOfflineCommissionByAgentForEvent(presidentId, agentId, eventId);

            assertEquals(6.0, result, 0.001);
        }

        @Test
        @DisplayName("getOfflineCommissionByAgentForEvent - Financial Clerk can view any agent")
        void clerk_CanViewAnyAgentForEvent() {
            when(userPersistence.getUserById(clerkId)).thenReturn(Optional.of(clerkUser));
            when(persistence.getOfflineRevenueByAgentForEvent(agentId, eventId)).thenReturn(700.0);

            double result = salesTracking.getOfflineCommissionByAgentForEvent(clerkId, agentId, eventId);

            assertEquals(7.0, result, 0.001);
        }
    }
}
