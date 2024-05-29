package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TicketService {
    Ticket addTicket(Ticket ticket);
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    Ticket updateTicket(Long id, Ticket ticket);
    void updateTicketStatus(Long ticketId, String newStatus);
}
