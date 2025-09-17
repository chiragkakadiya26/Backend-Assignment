package com.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Document("booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private String id;
    private String hotelId;
    private String userId;
    private String guestName;
    private String guestEmail;
    private int numOfAdults;
    private int numOfChildren;
    private int numberOfGuests;
    private String roomType;
    private String roomNumber;
    private Double totalAmount;
    private String status;
    private LocalDate checkIn;
    private LocalDate checkOut;
    @CreatedDate
    private Date createdAt;

}
