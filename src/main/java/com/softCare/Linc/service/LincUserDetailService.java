package com.softCare.Linc.service;

import com.softCare.Linc.Repository.UserRepository;
import com.softCare.Linc.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Project: UserService
 * @author Jan Willem vd Wal on 21-6-2022.
 * Beschrijving:
 */
@Service
public class LincUserDetailService implements UserDetailsService, LincUserDetailServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LincUserDetailService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) {
        if (emailAddress.contains("@")){
            return userRepository.findByEmailAddress(emailAddress).orElseThrow(
                    () -> new UsernameNotFoundException("User with email " + emailAddress + " was not found."));
        } else {
            return userRepository.findByUsername(emailAddress).orElseThrow(
                    () -> new UsernameNotFoundException("User with email " + emailAddress + " was not found."));
        }
    }

    @Override
    public Collection<? extends User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAddress(email);
    }

    public Optional<User> findByUserId(Long userId){
        return userRepository.findByUserId(userId);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

}
