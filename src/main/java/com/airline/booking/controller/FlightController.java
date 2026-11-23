
package com.airline.booking.controller;

import com.airline.booking.entity.Flight;
import com.airline.booking.service.FlightService;
import com.airline.booking.dto.CreateFlightRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    //GET
    // Get all flights
    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    // Get flight by ID
    @GetMapping("/{id}")
    public Flight getFlightById(@PathVariable("id") Long id) {
        return flightService.getFlightById(id);
    }

    //Search flights
    @GetMapping("/search")
    public List<Flight> searchFlight(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination)  {
     return flightService.searchFlights(
             origin,
             destination
         );
    }

    @GetMapping("/filter")
    public List<Flight> filterFlights(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam("maxPrice") BigDecimal maxPrice,
            @RequestParam("minSeats") Integer minSeats) {

        return flightService.filterFlights(
                origin,
                destination,
                date,
                maxPrice,
                minSeats
        );
    }

    //POST
    @PostMapping
    public ResponseEntity<Flight> createFlight(
            @Valid @RequestBody CreateFlightRequest request) {

        Flight createdFlight = flightService.createFlight(request);

        URI location = URI.create(
                "/api/flights/" + createdFlight.getId()
        );

        return ResponseEntity
                .created(location)
                .body(createdFlight);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateFlightRequest request) {

        Flight updatedFlight =
                flightService.updateFlight(id, request);

        return ResponseEntity.ok(updatedFlight);
    }



}
