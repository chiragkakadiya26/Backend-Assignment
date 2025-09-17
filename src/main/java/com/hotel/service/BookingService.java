package com.hotel.service;

import com.hotel.dto.BookingDto;
import com.hotel.dto.BookingResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String hotelId, BookingDto bookingDto, Authentication authentication);
    List<BookingResponse> getBookings(String hotelId, LocalDate startDate, LocalDate endDate);
}
