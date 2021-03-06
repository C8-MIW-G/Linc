package com.softCare.Linc.service;

import com.softCare.Linc.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

public interface LincUserDetailServiceInterface {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Collection<? extends User> findAll();

    void save(User user);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long userId);

    void delete(User user);
}
