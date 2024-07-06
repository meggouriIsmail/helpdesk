package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOwnerUsername(String username);
    Ticket findByOwnerUsernameAndId(String username, Long id);
    @Query("SELECT t FROM Ticket t JOIN t.sharedWith u WHERE u.username = :username")
    Optional<List<Ticket>> findAllTicketsBySharedWithUsername(@Param("username") String username);
}
