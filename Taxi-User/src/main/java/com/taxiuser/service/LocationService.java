package com.taxiuser.service;

import com.taxiuser.dto.PointDTO;
import com.taxiuser.enums.VehicleType;
import com.taxiuser.exception.DriverNotFound;
import com.taxiuser.model.Driver;
import com.taxiuser.repository.DriverRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    final DriverRepository driverRepository;
    final GeometryFactory geometryFactory = new GeometryFactory();

    @Value("${taxi.distance.max}")
    double maxDistance;

    public Driver findTheNearestDriver(Point point, VehicleType vehicleType) throws DriverNotFound {
        return driverRepository.findNearestAvailableDriverWithVehicleType(point, vehicleType.name(), maxDistance)
                .orElseThrow(DriverNotFound::new);
    }

    public Point pointByPointDTO(PointDTO pointDTO) {
        return geometryFactory.createPoint(new Coordinate(pointDTO.longitude(), pointDTO.latitude()));
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
