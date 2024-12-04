// src/main/java/com/example/loginapp/repository/UserRepository.java

package com.example.loginapp.repository;

import com.example.loginapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
