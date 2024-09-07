package com.taxiuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.dto.response.ChargeReport;
import com.taxiuser.dto.response.NewOrderReport;
import com.taxiuser.dto.response.OrderPaymentReport;
import com.taxiuser.dto.response.ReportDTO;
import com.taxiuser.enums.ReportType;
import com.taxiuser.exception.OrderNotFound;
import com.taxiuser.model.Driver;
import com.taxiuser.model.Order;
import com.taxiuser.model.User;
import com.taxiuser.repository.OrderRepository;
import com.taxiuser.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    JwtService jwtService;
    UserRepository userRepository;
    OrderRepository orderRepository;
    PropertiesService propertiesService;
    ReporterService reporterService;

    public String payOrder(String authentication) throws BadRequestException, JsonProcessingException {
        String token = authentication.substring(7);
        String username = jwtService.extractSubject(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.getStatus().equals("onTheWay"))
            throw new OrderNotFound();

        Order order = orderRepository.findByTravelerIdAndTravelerStatusAndStatus(user.getId(), "onTheWay", "notPaid")
                .orElseThrow(OrderNotFound::new);
        if(user.getBalance() < order.getPrice())
            throw new BadRequestException("Not enough balance, please charge your balance");

        order.setStatus("paid");
        orderRepository.save(order);

        user.setBalance(user.getBalance() - order.getPrice());
        user.setStatus("free");
        userRepository.save(user);

        Driver driver = order.getDriver();
        User driverUser = driver.getUser();
        Integer commission = propertiesService.getCommission();
        driverUser.setBalance(driverUser.getBalance() + (order.getPrice() - (order.getPrice()*commission)/100));
        userRepository.save(driverUser);

        reporterService.report(new ReportDTO(ReportType.ORDER_PAYMENT, new OrderPaymentReport(order.getId())));

        return "Order was paid successfully";
    }

    public String charge(Integer amount, String authentication) throws JsonProcessingException {
        String token = authentication.substring(7);
        String username = jwtService.extractSubject(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        reporterService.report(new ReportDTO(ReportType.CHARGE_BALANCE, new ChargeReport(amount, user.getId())));

        return "Balance charged successfully";
    }

}
