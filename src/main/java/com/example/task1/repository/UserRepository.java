package com.example.task1.repository;

import com.example.task1.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  Optional<User> findByCustomerId(String email);

  Optional<User> findByEmail(String email);
}
