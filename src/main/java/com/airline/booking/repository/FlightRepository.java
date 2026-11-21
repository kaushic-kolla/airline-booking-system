package com.airline.booking.repository;

import com.airline.booking.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FlightRepository
       extends JpaRepository<Flight, Long>{
}
