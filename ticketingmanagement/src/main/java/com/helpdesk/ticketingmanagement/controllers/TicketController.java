package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.services.TicketService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {
    private TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }
    @GetMapping("/tickets/{id}")
    public Ticket getAllTickets(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
    @PostMapping("/tickets/new")
    public String addTicket(@RequestParam("files") MultipartFile[] files) throws IOException {
        try {
            ticketService.addTicket(new Ticket(), files);
            return "Ticket and documents uploaded successfully";
        } catch (IOException e) {
            return "Failed to upload ticket and documents: " + e.getMessage();
        }
    }
    @PutMapping("/tickets/{id}")
    public void updateTicket(@PathVariable Long id,
                             @RequestBody Ticket ticket) {
        ticketService.updateTicket(id, ticket);
    }
}
