package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.dto.CommentResDto;
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

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<CommentResDto>> getComments(@PathVariable String username) {
        return new ResponseEntity<>(commentService.getAllCommentsByUser(username), HttpStatus.FOUND);
    }

    @GetMapping("/{ticket_id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<CommentResDto>> getComments(@PathVariable Long ticket_id) {
        return new ResponseEntity<>(commentService.getAllCommentsByTicket(ticket_id), HttpStatus.FOUND);
    }

    @PostMapping("/{id}/new")
    @PreAuthorize("hasAuthority('USER')")
    public void addComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        commentService.addComment(id, commentDto);
    }
}
