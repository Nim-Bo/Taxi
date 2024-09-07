package com.taxiuser.model;

import com.taxiuser.enums.VehicleType;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.locationtech.jts.geom.Point;

import java.util.Date;
import java.util.List;

@Data
@Entity(name = "Driver")
@Table(name = "drivers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
    Point location;
    @OneToMany(mappedBy = "driver")
    List<Order> orders;
    @NotNull
    @Enumerated(EnumType.STRING)
    VehicleType vehicleType;
    @NotNull
    String vehicleName;
    @NotNull
    String vehicleModel;
    @NotNull
    String vehicleColor;
    @NotNull
    String vehiclePlate;
    @NotNull
    Date createdAt;
    @NotNull
    Boolean isWorking;
    @NotNull
    Boolean isAvailable;

}
