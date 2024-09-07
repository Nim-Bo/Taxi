package com.taxiuser.mapper;

import com.taxiuser.dto.response.DriverDTO;
import com.taxiuser.model.Driver;

public class DriverMapper {

    public static DriverDTO driverToDriverDTO(Driver driver) {
        return new DriverDTO(
                driver.getUser().getFirstName(),
                driver.getUser().getLastName(),
                driver.getVehicleName(),
                driver.getVehicleModel(),
                driver.getVehicleColor(),
                driver.getVehiclePlate()
        );
    }

}
