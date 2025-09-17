package com.hotel.service;

import com.hotel.model.Hotel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface HotelService {
    Hotel createHotel(Hotel hotel);
}
