package com.hotel.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("hotel")
@Data
public class Hotel {
    @Id
    private String id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Double rating;
    private List<RoomType> roomTypes;
}
