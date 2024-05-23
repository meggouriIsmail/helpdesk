package com.helpdesk.ticketingmanagement.repositories;
import com.helpdesk.ticketingmanagement.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
