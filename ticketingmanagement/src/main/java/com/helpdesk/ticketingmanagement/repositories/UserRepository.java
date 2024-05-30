package com.helpdesk.ticketingmanagement.repositories;
import com.helpdesk.ticketingmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

