package com.helpdesk.ticketingmanagement.repositories;

import com.helpdesk.ticketingmanagement.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
