package com.taxiuser.dto.response;

import com.taxiuser.dto.PointDTO;
import com.taxiuser.enums.VehicleType;
import com.taxiuser.model.User;

import java.util.Date;

public record OrderResponseDTO(
        Long id,
        DriverDTO driver,
        TravelerDTO traveler,
        String origin,
        String destination,
        Integer price,
        String status,
        Date createdAt
) {
}
