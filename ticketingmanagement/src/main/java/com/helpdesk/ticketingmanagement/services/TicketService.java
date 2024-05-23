package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TicketService {
    void addTicket(Ticket ticket, MultipartFile[] files) throws IOException;
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    Ticket updateTicket(Long id, Ticket ticket);

    void upload(MultipartFile file, Long ticketId) throws Exception;
}
