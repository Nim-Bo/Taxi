package com.taxiuser.repository;

import com.taxiuser.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByTravelerIdAndTravelerStatusAndStatus(Long userId, String travelerStatus, String orderStatus);

}
