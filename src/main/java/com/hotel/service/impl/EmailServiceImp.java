package com.hotel.service.impl;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.support.email}")
    private String supportEmail;

    public EmailServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendBookingNotification(Hotel hotel, Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("kakadiay076@gmail.com");
            message.setSubject("New Booking Created - " + hotel.getName());
            message.setText(buildBookingNotificationText(hotel, booking));
            mailSender.send(message);
            if (booking.getGuestEmail() != null) {
                SimpleMailMessage guestMsg = new SimpleMailMessage();
                guestMsg.setTo(booking.getGuestEmail());
                guestMsg.setSubject("Booking Confirmation - " + hotel.getName());
                guestMsg.setText("Dear " + booking.getGuestName() + ",\n\n"
                        + "Your booking at " + hotel.getName() + " is confirmed.\n\n"
                        + "Check-in: " + booking.getCheckIn() + "\n"
                        + "Check-out: " + booking.getCheckOut() + "\n\n"
                        + "Thank you!");
                mailSender.send(guestMsg);
            }
            log.info("Booking notification sent for booking ID: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking notification: {}", e.getMessage());
        }
    }

    private String buildBookingNotificationText(Hotel hotel, Booking booking) {
        return String.format(
                "New booking created:\n\n" +
                        "Hotel: %s\n" +
                        "Guest: %s (%s)\n" +
                        "Check-in: %s\n" +
                        "Check-out: %s\n" +
                        "Room Type: %s\n" +
                        "Guests: %d\n" +
                        "Total Amount: $%.2f\n" +
                        "Booking ID: %s\n" +
                        "Status: %s",
                hotel.getName(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getRoomType(),
                booking.getNumberOfGuests(),
                booking.getTotalAmount(),
                booking.getId(),
                booking.getStatus()
        );
    }
}
