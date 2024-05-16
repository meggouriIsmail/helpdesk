package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
