package com.taxiuser.model;

import com.taxiuser.enums.VehicleType;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.locationtech.jts.geom.Point;

import java.util.Date;

@Data
@Entity(name = "Order")
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    Driver driver;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "traveler_id", referencedColumnName = "id")
    User traveler;
    @NotNull
    Point origin;
    @NotNull
    Point destination;
    @NotNull
    Integer price;
    @NotNull
    String status;
    @NotNull
    Date createdAt;

}
