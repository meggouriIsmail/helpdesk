package com.helpdesk.ticketingmanagement.repositories;
import com.helpdesk.ticketingmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

