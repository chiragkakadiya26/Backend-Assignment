package com.hotel;

import com.hotel.model.Hotel;
import com.hotel.model.RoomType;
import com.hotel.model.User;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class BackendAssignmentApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final HotelRepository hotelRepository;

	public BackendAssignmentApplication(UserRepository userRepository, HotelRepository hotelRepository) {
		this.userRepository = userRepository;
		this.hotelRepository = hotelRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendAssignmentApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        RoomType deluxe = new RoomType("DELUXE", 120.0, 10, 8);
        RoomType suite = new RoomType("SUITE", 200.0, 5, 5);

        // Seed only if not already present (by name)
        String seedName = "Grand Plaza";
        Optional<Hotel> existing = hotelRepository.findAll().stream()
                .filter(h -> seedName.equals(h.getName()))
                .findFirst();

        if (existing.isPresent()) {
            System.out.println("✅ Mock Hotel already exists with id: " + existing.get().getId());
            return;
        }

        Hotel hotel = new Hotel();
        // Let Mongo generate the _id
        hotel.setName(seedName);
        hotel.setAddress("123 Main St");
        hotel.setCity("Cityville");
        hotel.setCountry("Wonderland");
        hotel.setRating(4.5);
        hotel.setRoomTypes(List.of(deluxe, suite));
        System.out.println(">>> Inserting mock hotel data...");

        Hotel saved = hotelRepository.save(hotel);
        System.out.println("✅ Mock Hotel data inserted successfully! Use hotel id: " + saved.getId());
    }
}
