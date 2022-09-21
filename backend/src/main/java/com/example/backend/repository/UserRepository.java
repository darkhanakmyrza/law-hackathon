package com.example.backend.repository;

import com.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query(value = "select * from users where phone= :username", nativeQuery = true)
  Optional<User> findByUsername(String username);

  @Query(value = "select exists(select * from users where phone = :username)", nativeQuery = true)
  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}