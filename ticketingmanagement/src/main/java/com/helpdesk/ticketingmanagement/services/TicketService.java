package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.Ticket;

import java.util.List;

public interface TicketService {
    Ticket addTicket(TicketDto ticket);

    List<Ticket> getAllTickets();

    Ticket getTicketById(Long id);

    Ticket getTicketByUserAndId(UserNameDto userNameDto, Long id);
    List<Ticket> getTicketsByUserAndId(UserNameDto userNameDto);

    void updateTicket(Long id, Ticket ticket);

    void updateTicketStatus(Long ticketId, TicketStatusDto ticketStatusDto);

    Ticket updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto);

    Ticket updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto);
}
