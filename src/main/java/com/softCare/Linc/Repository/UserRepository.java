package com.softCare.Linc.Repository;

import com.softCare.Linc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAddress(String emailAddress);

    Optional<User> findByUsername(String emailAddress);
}
