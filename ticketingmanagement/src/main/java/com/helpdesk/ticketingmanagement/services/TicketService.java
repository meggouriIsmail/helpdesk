package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.entities.Ticket;

import java.util.List;

public interface TicketService {
    Ticket addTicket(TicketDto ticket);
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    Ticket updateTicket(Long id, Ticket ticket);
    void updateTicketStatus(Long ticketId, TicketStatusDto ticketStatusDto);
}
