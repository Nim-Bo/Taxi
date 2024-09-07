package com.taxiuser.service;

import com.taxiuser.enums.VehicleType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PropertiesService {

    Environment environment;

    public Double getPricePerMeter(VehicleType vehicleType) {
        String propertyKey = "pricing.vehicle." + vehicleType.name().toUpperCase() + ".price-per-meter";
        return environment.getProperty(propertyKey, Double.class);
    }

    public Integer getCommission() {
        String propertyKey = "pricing.commission-percent";
        return environment.getProperty(propertyKey, Integer.class);
    }

    public Integer getBasePrice() {
        String propertyKey = "pricing.base-price";
        return environment.getProperty(propertyKey, Integer.class);
    }

}