package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
