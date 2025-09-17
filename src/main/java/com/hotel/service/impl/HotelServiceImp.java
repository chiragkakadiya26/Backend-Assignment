package com.hotel.service.impl;

import com.hotel.model.Hotel;
import com.hotel.repository.HotelRepository;
import com.hotel.service.HotelService;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImp implements HotelService {
    private final HotelRepository hotelRepository;

    public HotelServiceImp(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
}
