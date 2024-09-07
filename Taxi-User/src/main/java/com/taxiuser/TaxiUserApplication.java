package com.taxiuser;

import com.github.javafaker.Faker;
import com.taxiuser.dto.PointDTO;
import com.taxiuser.enums.Role;
import com.taxiuser.enums.VehicleType;
import com.taxiuser.model.Driver;
import com.taxiuser.model.User;
import com.taxiuser.repository.DriverRepository;
import com.taxiuser.repository.UserRepository;
import com.taxiuser.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;

@Slf4j
@SpringBootApplication
public class TaxiUserApplication implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public TaxiUserApplication(PasswordEncoder passwordEncoder, LocationService locationService, UserRepository userRepository, DriverRepository driverRepository) {
        this.passwordEncoder = passwordEncoder;
        this.locationService = locationService;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaxiUserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 3; i++) {
            Faker faker = new Faker();

            User user = new User();
            user.setBalance(0);
            user.setStatus("driving");
            user.setCreatedAt(Date.from(Instant.now()));
            user.setUpdatedAt(Date.from(Instant.now()));
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setRole(Role.ROLE_DRIVER);
            user.setPhone(faker.phoneNumber().phoneNumber());
            user.setUsername(faker.name().username());
            user.setPassword(passwordEncoder.encode(faker.random().hex(6)));
            User userWithId = userRepository.save(user);

            Driver driver = new Driver();
            driver.setCreatedAt(Date.from(Instant.now()));
            driver.setUser(userWithId);
            driver.setLocation(locationService.pointByPointDTO(new PointDTO(36.303427, 59.581199)));
            driver.setIsWorking(true);
            driver.setIsAvailable(true);
            driver.setVehiclePlate(faker.random().hex(8));
            driver.setVehicleModel(faker.random().hex(8));
            driver.setVehicleName(faker.random().hex(8));
            driver.setVehicleColor(faker.color().name());
            VehicleType vehicleType = VehicleType.values()[faker.random().nextInt(VehicleType.values().length)];
            driver.setVehicleType(vehicleType);

            driverRepository.save(driver);
        }
    }
}
