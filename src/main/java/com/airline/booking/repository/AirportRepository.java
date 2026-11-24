
package com.airline.booking.repository;

import com.airline.booking.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository
        extends JpaRepository<Airport, String> {

}
