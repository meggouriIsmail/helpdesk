package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@EnableMethodSecurity
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public CommentController(CommentService commentService, CommentRepository commentRepository) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<Comment>> getComments() {
        return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.FOUND);
    }

    @PostMapping("/{id}/new")
    @PreAuthorize("hasAuthority('USER')")
    public void addComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        // Save comment to the database (implementation not shown)
        commentService.addComment(id, commentDto);
    }
}
