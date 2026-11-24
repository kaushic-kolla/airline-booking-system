package com.airline.booking.dto;

import com.airline.booking.entity.Airport;

public record FlightAirportsResponse(
        Long id,
        String flightNumber,
        AirportInfo origin,
        AirportInfo destination
) {

    public record AirportInfo(
            String code,
            String name,
            String city,
            String country,
            String timeZone
    ) {

        public static AirportInfo from(Airport airport) {
            return new AirportInfo(
                    airport.getCode(),
                    airport.getName(),
                    airport.getCity(),
                    airport.getCountry(),
                    airport.getTimeZone()
            );
        }
    }
}

