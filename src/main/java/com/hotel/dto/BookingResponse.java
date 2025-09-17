package com.hotel.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private String id;
    private String hotelId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String roomType;
    private Integer numberOfGuests;
    private Double totalAmount;
    private String status;
    private Data createdAt;
}
