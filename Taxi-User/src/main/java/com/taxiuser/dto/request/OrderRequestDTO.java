package com.taxiuser.dto.request;

import com.taxiuser.dto.PointDTO;
import com.taxiuser.enums.VehicleType;

public record OrderRequestDTO(
        PointDTO origin,
        PointDTO destination,
        VehicleType vehicleType
) {
}
