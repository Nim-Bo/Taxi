package com.taxiuser.repository;

import com.taxiuser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

}
