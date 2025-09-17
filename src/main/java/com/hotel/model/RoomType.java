package com.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    private String type;
    private Double pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
}
