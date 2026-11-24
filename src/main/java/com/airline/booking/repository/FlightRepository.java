
package com.airline.booking.repository;

import com.airline.booking.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import java.util.List;
import java.util.Optional;

public interface FlightRepository
        extends JpaRepository<Flight, Long> {

    // Search by route
    List<Flight> findByOriginIgnoreCaseAndDestinationIgnoreCase(
            String origin,
            String destination
    );

    // Advanced flight filtering
    @Query("""
        SELECT f FROM Flight f
        WHERE LOWER(f.origin) = LOWER(:origin)
        AND LOWER(f.destination) = LOWER(:destination)
        AND f.departureTime >= :startUtc
        AND f.departureTime < :endUtc
        AND f.priceUsd <= :maxPrice
        AND f.availableSeats >= :minSeats
        ORDER BY f.departureTime ASC
        """)
    List<Flight> filterFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startUtc") OffsetDateTime startUtc,
            @Param("endUtc") OffsetDateTime endUtc,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minSeats") Integer minSeats
    );


    @Query("""
    SELECT f FROM Flight f
    JOIN FETCH f.originAirport
    JOIN FETCH f.destinationAirport
    WHERE f.id = :id
    """)
    Optional<Flight> findByIdWithAirports(
            @Param("id") Long id
    );

}
