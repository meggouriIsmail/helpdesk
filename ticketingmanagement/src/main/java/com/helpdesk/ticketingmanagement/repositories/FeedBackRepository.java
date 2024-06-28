package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
    Optional<FeedBack> findByTicketId(Long ticketId);
}
