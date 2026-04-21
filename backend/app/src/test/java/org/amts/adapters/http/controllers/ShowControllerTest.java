package org.amts.adapters.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.amts.application.usecases.event.ShowManagementUseCase;
import org.amts.domain.entities.event.Show;
import org.amts.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowController.class)
@DisplayName("ShowController Tests")
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShowManagementUseCase showManagement;

    @Test
    @DisplayName("getPublicShowsByEvent - Returns show list without userId")
    void getPublicShowsByEvent_ReturnsShows() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        Show show = new Show(
                showId,
                eventId,
                UUID.randomUUID(),
                "Public Show",
                "Visible on the home page",
                "https://example.com/public-show.jpg",
                LocalDateTime.of(2026, 5, 1, 19, 0),
                LocalDateTime.of(2026, 5, 1, 21, 0),
                Money.of(100.0),
                Money.of(150.0),
                100,
                50,
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );
        when(showManagement.getPublicShowsByEvent(eventId))
                .thenReturn(Optional.of(new ArrayList<>(java.util.List.of(show))));

        mockMvc.perform(post("/api/show/public/by-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PublicShowsRequest(eventId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(showId.toString()))
                .andExpect(jsonPath("$[0].eventId").value(eventId.toString()))
                .andExpect(jsonPath("$[0].name").value("Public Show"));

        verify(showManagement).getPublicShowsByEvent(eventId);
    }

    @Test
    @DisplayName("getPublicShowById - Returns show without userId")
    void getPublicShowById_ReturnsShow() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        Show show = new Show(
                showId,
                eventId,
                UUID.randomUUID(),
                "Public Show",
                "Visible on the public show page",
                "https://example.com/public-show.jpg",
                LocalDateTime.of(2026, 5, 1, 19, 0),
                LocalDateTime.of(2026, 5, 1, 21, 0),
                Money.of(100.0),
                Money.of(150.0),
                100,
                50,
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );
        when(showManagement.getPublicShowByID(showId)).thenReturn(Optional.of(show));

        mockMvc.perform(post("/api/show/public/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PublicShowRequest(showId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showId.toString()))
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.name").value("Public Show"));

        verify(showManagement).getPublicShowByID(showId);
    }

    @Test
    @DisplayName("getPublicShowsByEvent - Returns 404 when event has no shows")
    void getPublicShowsByEvent_NoShows_ReturnsNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(showManagement.getPublicShowsByEvent(eventId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/show/public/by-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PublicShowsRequest(eventId))))
                .andExpect(status().isNotFound());

        verify(showManagement).getPublicShowsByEvent(eventId);
    }

    private record PublicShowRequest(UUID showId) {}
    private record PublicShowsRequest(UUID eventId) {}
}
