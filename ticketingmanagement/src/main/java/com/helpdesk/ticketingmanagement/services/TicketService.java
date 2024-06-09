package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.dto.UpdateAssignedToDto;
import com.helpdesk.ticketingmanagement.dto.UpdateSharedWithDto;
import com.helpdesk.ticketingmanagement.entities.Ticket;

import java.util.List;

public interface TicketService {
    Ticket addTicket(TicketDto ticket);

    List<Ticket> getAllTickets();

    Ticket getTicketById(Long id);

    Ticket getTicketByUserAndId(String username, Long id);
    List<Ticket> getTicketsByUserAndId(String username);

    void updateTicket(Long id, Ticket ticket);

    void updateTicketStatus(Long ticketId, TicketStatusDto ticketStatusDto);

    Ticket updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto);

    Ticket updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto);
}
