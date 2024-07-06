package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.*;
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

    @GetMapping("/user-tickets/{username}/")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<TicketResDto>> getAllTicketsByOwner(@PathVariable String username) {
        return new ResponseEntity<>(ticketService.getTicketsByUser(username), HttpStatus.OK);
    }

    @GetMapping("/user-ticket/{username}/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<TicketResDto> getTicketByOwner(@PathVariable String username, @PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getTicketByUserAndId(username, id), HttpStatus.OK);
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<List<TicketResDto>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    @GetMapping("/tickets/shared-with/{username}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<TicketResDto>> getAllTicketsSharedWith(@PathVariable String username) {
        return new ResponseEntity<>(ticketService.findAllTicketsBySharedWithUsername(username), HttpStatus.OK);
    }

    @GetMapping("/favorite-tickets/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<TicketResDto>> getFavouriteTickets(@PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getFavouriteTickets(id), HttpStatus.OK);
    }

    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<TicketResDto> getTicketById(@PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getTicketById(id), HttpStatus.OK);
    }
    @PostMapping("/tickets/new")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<TicketResDto> addTicket(@RequestBody TicketDto ticket) {
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
    public ResponseEntity<TicketResDto> updateSharedWith(@PathVariable Long ticketId, @RequestBody UpdateSharedWithDto updateSharedWithDto) {
        TicketResDto updatedTicket = ticketService.updateSharedWith(ticketId, updateSharedWithDto);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/tickets/assignedTo/{ticketId}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public ResponseEntity<TicketResDto> updateAssignedTo(@PathVariable Long ticketId, @RequestBody UpdateAssignedToDto updateAssignedToDto) {
        TicketResDto updatedTicket = ticketService.updateAssignedTo(ticketId, updateAssignedToDto);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/tickets/isFavorite/{ticketId}")
    @PreAuthorize("hasAuthority('USER')")
    public void updateIsFavourite(@PathVariable Long ticketId, @RequestBody IsFavoriteDto isFavoriteDto) {
        ticketService.updateIsFavourite(ticketId, isFavoriteDto);
    }
}
