package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.rabbitmq.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.services.CommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}/new")
    public void addComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        // Save comment to the database (implementation not shown)
        commentService.addComment(id, commentDto);
    }
}
