package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }
    @GetMapping("/tickets/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getTicketById(id), HttpStatus.OK);
    }
    @PostMapping("/tickets/new")
    public ResponseEntity<Ticket> addTicket(@RequestBody TicketDto ticket) {
        return new ResponseEntity<>(ticketService.addTicket(ticket), HttpStatus.CREATED);
    }
    @PutMapping("/tickets/{id}")
    public void updateTicket(@PathVariable Long id,
                             @RequestBody Ticket ticket) {
        ticketService.updateTicket(id, ticket);
    }

    @PutMapping("/tickets/status/{id}")
    public void updateTicketStatus(@PathVariable Long id,
                             @RequestBody TicketStatusDto ticket) {
        ticketService.updateTicketStatus(id, ticket);
    }
}
