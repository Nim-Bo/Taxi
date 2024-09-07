package com.taxiuser.model;

import com.taxiuser.enums.Role;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "User")
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "user_username_unique", columnNames = "username"),
            @UniqueConstraint(name = "user_phone_unique", columnNames = "phone"),
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    Role role;
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    @NotNull
    String phone;
    @NotNull
    @Positive
    Integer balance;
    @NotNull
    String status;
    @OneToMany(mappedBy = "traveler")
    List<Order> orders;
    @NotNull
    Date createdAt;
    @NotNull
    Date updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
