package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;

public interface EmailService {
    void sendBookingNotification(Hotel hotel, Booking booking);
}
