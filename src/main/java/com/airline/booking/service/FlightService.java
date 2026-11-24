
package com.airline.booking.service;

import com.airline.booking.entity.Flight;
import com.airline.booking.repository.FlightRepository;
import com.airline.booking.dto.CreateFlightRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;


import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    //Search flights by Origin and Destination
    public List<Flight> searchFlights(
        String origin,
        String destination) {

        return flightRepository
                .findByOriginIgnoreCaseAndDestinationIgnoreCase(
                        origin.trim(),
                        destination.trim()

                );
    }

    public Flight createFlight(CreateFlightRequest request) {

        String origin = request.origin()
                .trim().toUpperCase(Locale.ROOT);

        String destination = request.destination()
                .trim().toUpperCase(Locale.ROOT);

        // Business rule 1: Airports must be different
        if (origin.equals(destination)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination cannot be the same"
            );
        }

        // Business rule 2: Arrival must be after departure
        if (!request.departureTime()
                .isBefore(request.arrivalTime())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival must be after departure"
            );
        }

        // Create the Flight entity
        Flight flight = new Flight(
                request.flightNumber()
                        .trim().toUpperCase(Locale.ROOT),
                origin,
                destination,
                request.departureTime(),
                request.arrivalTime(),
                request.priceUsd(),
                request.availableSeats()
        );

        // Save it into PostgreSQL
        return flightRepository.save(flight);
    }

    // Get all flights
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    // Get one flight by database ID
    public Flight getFlightById(Long id) {

        return flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Flight not found with ID: " + id
                        )
                );
    }

    public List<Flight> filterFlights(
            String origin,
            String destination,
            LocalDate date,
            BigDecimal maxPrice,
            Integer minSeats) {

        // Clean up airport codes
        String from = origin.trim().toUpperCase(Locale.ROOT);
        String to = destination.trim().toUpperCase(Locale.ROOT);

        // Validate the inputs
        if (!from.matches("[A-Z]{3}") ||
                !to.matches("[A-Z]{3}") ||
                from.equals(to) ||
                maxPrice.signum() < 0 ||
                minSeats < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid flight search filters"
            );
        }

        // Create the start and end of the selected UTC date
        OffsetDateTime startUtc =
                date.atStartOfDay().atOffset(ZoneOffset.UTC);

        OffsetDateTime endUtc =
                date.plusDays(1)
                        .atStartOfDay()
                        .atOffset(ZoneOffset.UTC);

        // Search PostgreSQL using the repository
        return flightRepository.filterFlights(
                from,
                to,
                startUtc,
                endUtc,
                maxPrice,
                minSeats
        );
    }

    @Transactional
    public Flight updateFlight(
            Long id,
            CreateFlightRequest request) {

        // Step 1: Find the existing flight
        Flight flight = getFlightById(id);

        // Step 2: Clean up airport codes
        String origin = request.origin()
                .trim().toUpperCase(Locale.ROOT);

        String destination = request.destination()
                .trim().toUpperCase(Locale.ROOT);

        // Step 3: Validate the route
        if (origin.equals(destination)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination cannot be the same"
            );
        }

        // Step 4: Validate the flight times
        if (!request.departureTime()
                .isBefore(request.arrivalTime())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival must be after departure"
            );
        }

        // Step 5: Update the existing flight
        flight.updateDetails(
                request.flightNumber()
                        .trim().toUpperCase(Locale.ROOT),
                origin,
                destination,
                request.departureTime(),
                request.arrivalTime(),
                request.priceUsd(),
                request.availableSeats()
        );

        // Step 6: Persist the changes
        return flightRepository.save(flight);
    }

    @Transactional
    public void deleteFlight(Long id) {

        // Step 1: Find the flight
        Flight flight = getFlightById(id);

        // Step 2: Delete the flight from PostgreSQL
        flightRepository.delete(flight);
    }

}
