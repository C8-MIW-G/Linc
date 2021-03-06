package com.softCare.Linc.service;

import com.softCare.Linc.model.Circle;

import java.util.Optional;

public interface CircleServiceInterface  {

    Object findAll();

    Optional<Circle> findById(Long id);

    void save(Circle circle);

    void delete(Circle circle);

    Circle findByCircleName(String name);
}
