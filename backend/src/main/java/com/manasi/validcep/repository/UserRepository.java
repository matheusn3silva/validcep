package com.manasi.validcep.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manasi.validcep.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
