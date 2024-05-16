package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface TicketService {
    void addTicket(Ticket ticket);
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    void deleteTicket(Long id);
    Ticket updateTicket(Long id, Ticket ticket);

    void upload(MultipartFile file, Long ticketId, Long typeId) throws Exception;
}
