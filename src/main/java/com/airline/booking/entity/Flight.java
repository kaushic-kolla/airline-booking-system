
package com.airline.booking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "departure_time")
    private OffsetDateTime departureTime;

    @Column(name = "arrival_time")
    private OffsetDateTime arrivalTime;

    @Column(name = "price_usd")
    private BigDecimal priceUsd;

    @Column(name = "available_seats")
    private Integer availableSeats;

    public Flight() {
    }

    public Flight(
            String flightNumber,
            String origin,
            String destination,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            BigDecimal priceUsd,
            Integer availableSeats) {

        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.priceUsd = priceUsd;
        this.availableSeats = availableSeats;
    }

    public void updateDetails(
            String flightNumber,
            String origin,
            String destination,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            BigDecimal priceUsd,
            Integer availableSeats) {

        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.priceUsd = priceUsd;
        this.availableSeats = availableSeats;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "origin",
            referencedColumnName = "code",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Airport originAirport;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "destination",
            referencedColumnName = "code",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Airport destinationAirport;


    @JsonIgnore
    public Airport getOriginAirport() {
        return originAirport;
    }

    @JsonIgnore
    public Airport getDestinationAirport() {
        return destinationAirport;
    }


    public Long getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public OffsetDateTime getArrivalTime() {
        return arrivalTime;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }
}
