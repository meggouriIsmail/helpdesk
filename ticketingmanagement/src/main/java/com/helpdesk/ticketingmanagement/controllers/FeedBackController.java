package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.dto.CommentResDto;
import com.helpdesk.ticketingmanagement.dto.FeedBackDto;
import com.helpdesk.ticketingmanagement.dto.FeedBackReqDto;
import com.helpdesk.ticketingmanagement.services.CommentService;
import com.helpdesk.ticketingmanagement.services.FeedBackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@EnableMethodSecurity
public class FeedBackController {

    private final FeedBackService feedBackService;

    public FeedBackController(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }


    @GetMapping("/{ticket_id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<FeedBackDto> getFeedBack(@PathVariable Long ticket_id) {
        return new ResponseEntity<>(feedBackService.getFeedBackByTicket(ticket_id), HttpStatus.OK);
    }

    @PostMapping("/{id}/new")
    @PreAuthorize("hasAuthority('USER')")
    public void addComment(@PathVariable Long id, @RequestBody FeedBackReqDto feedBackReqDto) {
        feedBackService.addFeedBack(id, feedBackReqDto);
    }
}
