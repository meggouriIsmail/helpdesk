package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.DocType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocTypeRepository extends JpaRepository<DocType, Long> {
}
