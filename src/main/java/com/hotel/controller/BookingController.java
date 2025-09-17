package com.hotel.controller;

import com.hotel.dto.BookingDto;
import com.hotel.dto.BookingResponse;
import com.hotel.model.Booking;
import com.hotel.service.BookingService;
import com.hotel.service.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final JwtService jwtService;

    public BookingController(BookingService bookingService, JwtService jwtService) {
        this.bookingService = bookingService;
        this.jwtService = jwtService;
    }
    @PostMapping("/{hotelId}/bookings")
    @PreAuthorize("hasAnyRole('STAFF', 'RECEPTION', 'ADMIN') or hasAuthority('booking:create')")
    public ResponseEntity<BookingResponse> createBooking(@PathVariable String hotelId,
                                                         @Valid @RequestBody BookingDto bookingDto,
                                                         Authentication authentication) {
        
        String userId = jwtService.getUserId(authentication);
        log.info("Creating booking request from user: {} for hotel: {}", userId, hotelId);
        
        try {
            BookingResponse booking = bookingService.createBooking(hotelId, bookingDto, authentication);
            log.info("Booking created successfully with ID: {} for user: {}", booking.getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            log.error("Failed to create booking for hotel: {} by user: {}", hotelId, userId, e);
            throw e;
        }
    }
    
    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings(@PathVariable String hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Retrieving bookings for hotel: {} with date range: {} to {}", hotelId, startDate, endDate);
        
        try {
            List<BookingResponse> bookings = bookingService.getBookings(hotelId, startDate, endDate);
            log.info("Successfully retrieved {} bookings for hotel: {}", bookings.size(), hotelId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Failed to retrieve bookings for hotel: {}", hotelId, e);
            throw e;
        }
    }
}
