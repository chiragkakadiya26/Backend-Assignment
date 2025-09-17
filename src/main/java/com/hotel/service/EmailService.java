package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;

public interface EmailService {
    public void sendBookingNotification(Hotel hotel, Booking booking);
}
