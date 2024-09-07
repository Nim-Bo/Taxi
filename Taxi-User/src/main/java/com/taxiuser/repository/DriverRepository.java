package com.taxiuser.repository;

import com.taxiuser.enums.VehicleType;
import com.taxiuser.model.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT d.* " +
            "FROM drivers d " +
            "WHERE d.is_available = true " +
            "AND d.is_working = true " +
            "AND d.vehicle_type = :vehicleType " +
            "AND ST_DWithin(ST_SetSRID(d.location, 3857), ST_SetSRID(:point, 3857), :maxDistance) " +
            "ORDER BY ST_Distance(ST_SetSRID(d.location, 3857), ST_SetSRID(:point, 3857)) " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Driver> findNearestAvailableDriverWithVehicleType(Point point, String vehicleType, double maxDistance);

}
