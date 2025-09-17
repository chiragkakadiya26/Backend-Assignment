package com.hotel.controller;

import com.hotel.model.Hotel;
import com.hotel.service.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/hotelCreate")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        log.info("Creating hotel: {} in {}", hotel.getName(), hotel.getCity());
        
        try {
            Hotel createdHotel = hotelService.createHotel(hotel);
            log.info("Hotel created successfully with ID: {} - {}", createdHotel.getId(), createdHotel.getName());
            return ResponseEntity.ok(createdHotel);
        } catch (Exception e) {
            log.error("Failed to create hotel: {}", hotel.getName(), e);
            throw e;
        }
    }
}
