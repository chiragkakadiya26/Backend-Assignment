package com.hotel.repository;

import com.hotel.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByHotelId(String hotelId);

    @Query("{'hotelId': ?0, 'roomType': ?1, '$or': [" +
            "{'checkIn': {$lt: ?3}, 'checkOut': {$gt: ?2}}, " +
            "{'checkIn': {$gte: ?2, $lt: ?3}}, " +
            "{'checkOut': {$gt: ?2, $lte: ?3}}]}")
    List<Booking> findConflictingBookings(String hotelId, String roomType,
                                          LocalDate checkIn, LocalDate checkOut);
    
    // Return bookings that OVERLAP the given range [startDate, endDate]
    @Query("{'hotelId': ?0, '$or': [" +
            "{'checkIn': {$lt: ?2}, 'checkOut': {$gt: ?1}}, " +
            "{'checkIn': {$gte: ?1, $lt: ?2}}, " +
            "{'checkOut': {$gt: ?1, $lte: ?2}}]}")
    List<Booking> findByHotelIdAndDateRange(String hotelId, LocalDate startDate, LocalDate endDate);
}
