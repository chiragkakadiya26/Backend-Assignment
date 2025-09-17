package com.hotel.service.impl;

import com.hotel.dto.BookingDto;
import com.hotel.dto.BookingResponse;
import com.hotel.execption.BookingConflictException;
import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.model.RoomType;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import com.hotel.service.BookingService;
import com.hotel.service.EmailService;
import com.hotel.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public BookingServiceImp(BookingRepository bookingRepository, HotelRepository hotelRepository, EmailService emailService, ModelMapper modelMapper, JwtService jwtService) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(String hotelId, BookingDto bookingDto, Authentication authentication) {
        log.info("Starting booking creation for hotelId: {}, user: {}", hotelId, jwtService.getUserId(authentication));
        
        // Validate input dates
        validateBookingDates(bookingDto);
        
        // Check for booking conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                hotelId, bookingDto.getRoomType(), bookingDto.getCheckIn(), bookingDto.getCheckOut());
        
        if (!conflicts.isEmpty()) {
            log.warn("Booking conflict detected for hotelId: {}, roomType: {}, dates: {} to {}", 
                    hotelId, bookingDto.getRoomType(), bookingDto.getCheckIn(), bookingDto.getCheckOut());
            throw new BookingConflictException("Room not available for the selected dates");
        }

        // Get hotel details for pricing
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> {
                    log.error("Hotel not found with id: {}", hotelId);
                    return new RuntimeException("Hotel not found");
                });

        // Calculate total amount
        Double pricePerNight = calculatePricePerNight(hotel, bookingDto.getRoomType());
        long nights = ChronoUnit.DAYS.between(bookingDto.getCheckIn(), bookingDto.getCheckOut());
        Double totalAmount = pricePerNight * nights;
        
        log.info("Calculated booking amount: {} nights × ${} = ${}", nights, pricePerNight, totalAmount);

        // Create and save booking
        Booking booking = createBookingEntity(hotelId, bookingDto, authentication, totalAmount);
        Booking savedBooking = bookingRepository.save(booking);
        
        log.info("Booking created successfully with ID: {}", savedBooking.getId());

        // Send notification email
        try {
            emailService.sendBookingNotification(hotel, savedBooking);
            log.info("Booking notification email sent for booking ID: {}", savedBooking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking notification email for booking ID: {}", savedBooking.getId(), e);
            // Don't fail the booking creation if email fails
        }

        return modelMapper.map(savedBooking, BookingResponse.class);
    }

    @Override
    public List<BookingResponse> getBookings(String hotelId, LocalDate startDate, LocalDate endDate) {
        log.info("Retrieving bookings for hotelId: {}, dateRange: {} to {}", hotelId, startDate, endDate);
        
        List<Booking> bookings;
        
        if (startDate != null && endDate != null) {
            validateDateRange(startDate, endDate);
            bookings = bookingRepository.findByHotelIdAndDateRange(hotelId, startDate, endDate);
            log.info("Found {} bookings in date range for hotelId: {}", bookings.size(), hotelId);
        } else {
            bookings = bookingRepository.findByHotelId(hotelId);
            log.info("Found {} total bookings for hotelId: {}", bookings.size(), hotelId);
        }

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponse.class))
                .collect(Collectors.toList());
    }
    
    // Private helper methods
    private void validateBookingDates(BookingDto bookingDto) {
        if (bookingDto.getCheckIn() == null || bookingDto.getCheckOut() == null) {
            log.error("Invalid booking dates: checkIn={}, checkOut={}", bookingDto.getCheckIn(), bookingDto.getCheckOut());
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        
        if (bookingDto.getCheckIn().isBefore(LocalDate.now())) {
            log.error("Check-in date cannot be in the past: {}", bookingDto.getCheckIn());
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        
        if (bookingDto.getCheckOut().isBefore(bookingDto.getCheckIn()) || 
            bookingDto.getCheckOut().isEqual(bookingDto.getCheckIn())) {
            log.error("Invalid date range: checkIn={}, checkOut={}", bookingDto.getCheckIn(), bookingDto.getCheckOut());
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
    
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            log.error("Invalid date range: startDate={}, endDate={}", startDate, endDate);
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
    
    private Double calculatePricePerNight(Hotel hotel, String roomType) {
        return hotel.getRoomTypes().stream()
                .filter(rt -> rt.getType().equals(roomType))
                .findFirst()
                .map(RoomType::getPricePerNight)
                .orElseThrow(() -> {
                    log.error("Room type not found: {} in hotel: {}", roomType, hotel.getName());
                    return new RuntimeException("Room type not found: " + roomType);
                });
    }
    
    private Booking createBookingEntity(String hotelId, BookingDto bookingDto, Authentication authentication, Double totalAmount) {
        Booking booking = new Booking();
        booking.setHotelId(hotelId);
        booking.setUserId(jwtService.getUserId(authentication));
        booking.setGuestName(bookingDto.getGuestName());
        booking.setGuestEmail(bookingDto.getGuestEmail());
        booking.setCheckIn(bookingDto.getCheckIn());
        booking.setCheckOut(bookingDto.getCheckOut());
        booking.setRoomType(bookingDto.getRoomType());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setTotalAmount(totalAmount);
        booking.setStatus("CONFIRMED");
        
        log.debug("Created booking entity for guest: {} at hotel: {}", bookingDto.getGuestName(), hotelId);
        return booking;
    }
}
