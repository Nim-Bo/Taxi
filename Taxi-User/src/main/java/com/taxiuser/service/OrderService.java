package com.taxiuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.dto.request.OrderRequestDTO;
import com.taxiuser.dto.response.NotifyDriverDTO;
import com.taxiuser.dto.response.OrderResponseDTO;
import com.taxiuser.exception.DriverNotFound;
import com.taxiuser.exception.IncompleteOrderExists;
import com.taxiuser.exception.OrderNotFound;
import com.taxiuser.exception.UserIsDriving;
import com.taxiuser.mapper.OrderMapper;
import com.taxiuser.model.Driver;
import com.taxiuser.model.Order;
import com.taxiuser.model.User;
import com.taxiuser.repository.OrderRepository;
import com.taxiuser.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    JwtService jwtService;
    UserRepository userRepository;
    LocationService locationService;
    PropertiesService propertiesService;
    OrderRepository orderRepository;
    NotifyDriverProducer notifyDriverProducer;

    public OrderResponseDTO order(OrderRequestDTO orderDTO, String authentication) throws IncompleteOrderExists, DriverNotFound, UserIsDriving, JsonProcessingException {
        String token = authentication.substring(7);
        String username = jwtService.extractSubject(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getStatus().equals("onTheWay"))
            throw new IncompleteOrderExists();
        if (!user.getStatus().equals("free"))
            throw new UserIsDriving();

        Point origin = locationService.pointByPointDTO(orderDTO.origin());
        Driver driver = locationService.findTheNearestDriver(origin, orderDTO.vehicleType());
        Point destination = locationService.pointByPointDTO(orderDTO.destination());

        Order order = new Order();
        order.setDriver(driver);
        order.setTraveler(user);
        order.setDestination(destination);
        order.setOrigin(origin);
        order.setStatus("notPaid");
        order.setCreatedAt(Date.from(Instant.now()));
        double distance = locationService.haversine(orderDTO.origin().latitude(), orderDTO.origin().longitude(), orderDTO.destination().latitude(), orderDTO.destination().longitude());
        double price = distance*propertiesService.getPricePerMeter(driver.getVehicleType());
        double finalPrice = Math.round((propertiesService.getBasePrice() + price) / 1000f) * 1000;
        order.setPrice((int) finalPrice);
        User driverUser = driver.getUser();
        driverUser.setStatus("driving");
        userRepository.save(driverUser);

        Order orderWithId = orderRepository.save(order);

        user.setStatus("onTheWay");
        List<Order> userOrders = user.getOrders();
        userOrders.add(order);
        user.setOrders(userOrders);
        userRepository.save(user);

        NotifyDriverDTO notifyDriverDTO = OrderMapper.orderToNotifyDriverDTO(order);
        notifyDriverProducer.notify(notifyDriverDTO);

        return OrderMapper.orderToOrderDTO(orderWithId);
    }

    public OrderResponseDTO userOrderInfo(String authentication) throws OrderNotFound {
        String token = authentication.substring(7);
        String username = jwtService.extractSubject(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.getStatus().equals("onTheWay"))
            throw new OrderNotFound();

        Order order = orderRepository.findByTravelerIdAndTravelerStatusAndStatus(user.getId(), "onTheWay", "notPaid")
                .orElseThrow(OrderNotFound::new);
        return OrderMapper.orderToOrderDTO(order);
    }

}
