package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<TicketType, Long> {
}
