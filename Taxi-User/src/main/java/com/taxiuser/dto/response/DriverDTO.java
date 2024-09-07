package com.taxiuser.dto.response;

public record DriverDTO(
        String firstName,
        String lastName,
        String vehicleName,
        String vehicleModel,
        String vehicleColor,
        String vehiclePlate
) {
}
