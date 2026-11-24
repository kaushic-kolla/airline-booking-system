
package com.airline.booking.controller;

import com.airline.booking.entity.Flight;
import com.airline.booking.service.FlightService;
import com.airline.booking.dto.CreateFlightRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightService flightService;

    private Flight sampleFlight() {
        return new Flight(
                "AA101",
                "DFW",
                "JFK",
                OffsetDateTime.parse(
                        "2026-12-15T09:00:00-06:00"),
                OffsetDateTime.parse(
                        "2026-12-15T13:15:00-05:00"),
                new BigDecimal("320.00"),
                120
        );
    }

    // Test 1: GET existing flight
    @Test
    void testGetFlightById() throws Exception {

        when(flightService.getFlightById(1L))
                .thenReturn(sampleFlight());

        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber")
                        .value("AA101"))
                .andExpect(jsonPath("$.origin")
                        .value("DFW"));

        verify(flightService).getFlightById(1L);
    }

    // Test 2: GET nonexistent flight
    @Test
    void testFlightNotFound() throws Exception {

        when(flightService.getFlightById(999999L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found with ID: 999999"
                ));

        mockMvc.perform(get("/api/flights/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    // Test 3: POST invalid price
    @Test
    void testCreateFlightInvalidPrice() throws Exception {

        String json = """
            {
              "flightNumber": "AA505",
              "origin": "DFW",
              "destination": "JFK",
              "departureTime": "2026-12-20T09:00:00-06:00",
              "arrivalTime": "2026-12-20T15:00:00-05:00",
              "priceUsd": -200,
              "availableSeats": 100
            }
            """;

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400));

        verifyNoInteractions(flightService);
    }

    // Test 4: DELETE flight
    @Test
    void testDeleteFlight() throws Exception {

        mockMvc.perform(delete("/api/flights/5"))
                .andExpect(status().isNoContent());

        verify(flightService).deleteFlight(5L);
    }

    @Test
    void testCreateFlightSuccessfully() throws Exception {

        // Create a simulated saved flight
        Flight savedFlight = new Flight(
                "AA404",
                "DFW",
                "MIA",
                OffsetDateTime.parse(
                        "2026-12-20T09:00:00-06:00"),
                OffsetDateTime.parse(
                        "2026-12-20T12:45:00-05:00"),
                new BigDecimal("375.50"),
                140
        );

        // Simulate PostgreSQL generating ID 42
        ReflectionTestUtils.setField(
                savedFlight, "id", 42L
        );

        when(flightService.createFlight(
                any(com.airline.booking.dto.CreateFlightRequest.class)))
                .thenReturn(savedFlight);

        String json = """
        {
          "flightNumber": "AA404",
          "origin": "DFW",
          "destination": "MIA",
          "departureTime": "2026-12-20T09:00:00-06:00",
          "arrivalTime": "2026-12-20T12:45:00-05:00",
          "priceUsd": 375.50,
          "availableSeats": 140
        }
        """;

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "/api/flights/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.flightNumber")
                        .value("AA404"))
                .andExpect(jsonPath("$.availableSeats")
                        .value(140));

        verify(flightService).createFlight(
                any(CreateFlightRequest.class) // might need change
        );
    }


    @Test
    void testUpdateFlightSuccessfully() throws Exception {

        Flight updatedFlight = new Flight(
                "AA404",
                "DFW",
                "MIA",
                OffsetDateTime.parse(
                        "2026-12-20T10:00:00-06:00"),
                OffsetDateTime.parse(
                        "2026-12-20T14:45:00-05:00"),
                new BigDecimal("420.00"),
                100
        );

        ReflectionTestUtils.setField(
                updatedFlight, "id", 1L
        );

        when(flightService.updateFlight(
                eq(1L),
                any(CreateFlightRequest.class)))
                .thenReturn(updatedFlight);

        String json = """
        {
          "flightNumber": "AA404",
          "origin": "DFW",
          "destination": "MIA",
          "departureTime": "2026-12-20T10:00:00-06:00",
          "arrivalTime": "2026-12-20T14:45:00-05:00",
          "priceUsd": 420.00,
          "availableSeats": 100
        }
        """;

        mockMvc.perform(put("/api/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.priceUsd")
                        .value(420.00))
                .andExpect(jsonPath("$.availableSeats")
                        .value(100));

        verify(flightService).updateFlight(
                eq(1L),
                any(CreateFlightRequest.class)
        );
    }


}
