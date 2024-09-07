package com.taxiuser.mapper;

import com.taxiuser.dto.response.DriverDTO;
import com.taxiuser.dto.response.NotifyDriverDTO;
import com.taxiuser.dto.response.OrderResponseDTO;
import com.taxiuser.dto.response.TravelerDTO;
import com.taxiuser.model.Order;

public class OrderMapper {

    public static OrderResponseDTO orderToOrderDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                DriverMapper.driverToDriverDTO(order.getDriver()),
                new TravelerDTO(
                        order.getTraveler().getFirstName(),
                        order.getTraveler().getLastName()
                ),
                order.getOrigin().toString(),
                order.getDestination().toString(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public static NotifyDriverDTO orderToNotifyDriverDTO(Order order) {
        return new NotifyDriverDTO(
                order.getTraveler().getFirstName(),
                order.getTraveler().getLastName(),
                order.getPrice(),
                order.getDriver().getUser().getId(),
                order.getDriver().getUser().getPhone()
        );
    }

}
