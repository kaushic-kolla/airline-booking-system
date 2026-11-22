
package com.airline.booking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateFlightRequest(

        @NotBlank
        @Pattern(regexp = "(?i)AA[0-9]{1,4}")
        String flightNumber,

        @NotBlank
        @Pattern(regexp = "(?i)[A-Z]{3}")
        String origin,

        @NotBlank
        @Pattern(regexp = "(?i)[A-Z]{3}")
        String destination,

        @NotNull
        OffsetDateTime departureTime,

        @NotNull
        OffsetDateTime arrivalTime,

        @NotNull
        @PositiveOrZero
        @Digits(integer = 8, fraction = 2)
        BigDecimal priceUsd,

        @NotNull
        @Min(0)
        Integer availableSeats

) {}
