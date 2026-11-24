
package com.airline.booking.service;

import com.airline.booking.entity.Flight;
import com.airline.booking.dto.CreateFlightRequest;
import com.airline.booking.repository.FlightRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    // Create a sample flight for testing
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

    // Test 1: Retrieve all flights
    @Test
    void testGetAllFlights() {

        when(flightRepository.findAll())
                .thenReturn(List.of(sampleFlight()));

        List<Flight> flights =
                flightService.getAllFlights();

        assertEquals(1, flights.size());
        assertEquals("AA101",
                flights.get(0).getFlightNumber());

        verify(flightRepository).findAll();
    }

    // Test 2: Find an existing flight
    @Test
    void testGetFlightById() {

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(sampleFlight()));

        Flight flight = flightService.getFlightById(1L);

        assertEquals("DFW", flight.getOrigin());
        assertEquals("JFK", flight.getDestination());

        verify(flightRepository).findById(1L);
    }

    // Test 3: Flight does not exist
    @Test
    void testFlightNotFound() {

        when(flightRepository.findById(999999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> flightService
                                .getFlightById(999999L)
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );
    }

    // Test 4: Reject invalid route
    @Test
    void testCreateFlightSameOriginDestination() {

        CreateFlightRequest request =
                new CreateFlightRequest(
                        "AA505",
                        "DFW",
                        "DFW",
                        OffsetDateTime.parse(
                                "2026-12-20T09:00:00-06:00"),
                        OffsetDateTime.parse(
                                "2026-12-20T12:00:00-06:00"),
                        new BigDecimal("350.00"),
                        100
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> flightService.createFlight(request)
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(flightRepository, never())
                .save(any(Flight.class));
    }

    // Test 5: Delete an existing flight
    @Test
    void testDeleteFlight() {

        Flight flight = sampleFlight();

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(flight));

        flightService.deleteFlight(1L);

        verify(flightRepository).delete(flight);
    }

    @Test
    void testCreateValidFlight() {

        CreateFlightRequest request =
                new CreateFlightRequest(
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

        when(flightRepository.save(any(Flight.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Flight created = flightService.createFlight(request);

        assertEquals("AA404", created.getFlightNumber());
        assertEquals("DFW", created.getOrigin());
        assertEquals("MIA", created.getDestination());
        assertEquals(140, created.getAvailableSeats());

        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testUpdateFlightSuccessfully() {

        // 1. Create an existing flight
        Flight existingFlight = sampleFlight();

        // 2. Prepare updated flight details
        CreateFlightRequest request =
                new CreateFlightRequest(
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

        // 3. Mock database lookup
        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(existingFlight));

        // 4. Mock database save
        when(flightRepository.save(any(Flight.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // 5. Call the actual service method
        Flight updatedFlight =
                flightService.updateFlight(1L, request);

        // 6. Verify updated values
        assertEquals("AA404",
                updatedFlight.getFlightNumber());

        assertEquals("DFW",
                updatedFlight.getOrigin());

        assertEquals("MIA",
                updatedFlight.getDestination());

        assertEquals(
                new BigDecimal("420.00"),
                updatedFlight.getPriceUsd()
        );

        assertEquals(
                100,
                updatedFlight.getAvailableSeats()
        );

        // 7. Verify repository interactions
        verify(flightRepository).findById(1L);

        verify(flightRepository)
                .save(existingFlight);
    }

    @Test
    void testUpdateFlightNotFound() {

        CreateFlightRequest request =
                new CreateFlightRequest(
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

        // Simulate a missing flight
        when(flightRepository.findById(999999L))
                .thenReturn(Optional.empty());

        // Expect an exception
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> flightService.updateFlight(
                                999999L, request
                        )
                );

        // Verify HTTP status
        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        // Verify that nothing was saved
        verify(flightRepository, never())
                .save(any(Flight.class));
    }


    @Test
    void testUpdateFlightInvalidRoute() {

        Flight existingFlight = sampleFlight();

        CreateFlightRequest request =
                new CreateFlightRequest(
                        "AA505",
                        "DFW",
                        "DFW",
                        OffsetDateTime.parse(
                                "2026-12-20T09:00:00-06:00"),
                        OffsetDateTime.parse(
                                "2026-12-20T12:00:00-06:00"),
                        new BigDecimal("350.00"),
                        100
                );

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(existingFlight));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> flightService.updateFlight(1L, request)
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        // Original record should remain unchanged
        assertEquals("DFW", existingFlight.getOrigin());
        assertEquals("JFK", existingFlight.getDestination());

        // Invalid update must not be saved
        verify(flightRepository, never())
                .save(any(Flight.class));
    }

    @Test
    void testDeleteFlightNotFound() {

        when(flightRepository.findById(999999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> flightService.deleteFlight(999999L)
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(flightRepository, never())
                .delete(any(Flight.class));
    }




}
