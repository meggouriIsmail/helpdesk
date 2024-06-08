package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.dto.UpdateAssignedToDto;
import com.helpdesk.ticketingmanagement.dto.UpdateSharedWithDto;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }
    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getTicketById(id), HttpStatus.OK);
    }
    @PostMapping("/tickets/new")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Ticket> addTicket(@RequestBody TicketDto ticket) {
        return new ResponseEntity<>(ticketService.addTicket(ticket), HttpStatus.CREATED);
    }
    @PutMapping("/tickets/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updateTicket(@PathVariable Long id,
                             @RequestBody Ticket ticket) {
        ticketService.updateTicket(id, ticket);
    }

    @PutMapping("/tickets/status/{ticketId}")
    @PreAuthorize("hasAuthority('USER')")
    public void updateTicketStatus(@PathVariable Long ticketId,
                             @RequestBody TicketStatusDto ticket) {
        ticketService.updateTicketStatus(ticketId, ticket);
    }

    @PutMapping("/tickets/sharedWith/{ticketId}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<Ticket> updateSharedWith(@PathVariable Long ticketId, @RequestBody UpdateSharedWithDto updateSharedWithDto) {
        Ticket updatedTicket = ticketService.updateSharedWith(ticketId, updateSharedWithDto);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/tickets/assignedTo/{ticketId}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<Ticket> updateAssignedTo(@PathVariable Long ticketId, @RequestBody UpdateAssignedToDto updateAssignedToDto) {
        Ticket updatedTicket = ticketService.updateAssignedTo(ticketId, updateAssignedToDto);
        return ResponseEntity.ok(updatedTicket);
    }
}
