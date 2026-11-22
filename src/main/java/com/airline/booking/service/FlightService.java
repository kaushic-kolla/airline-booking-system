
package com.airline.booking.service;

import com.airline.booking.entity.Flight;
import com.airline.booking.repository.FlightRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

}
