package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.Ticket;

import java.util.List;

public interface TicketService {
    TicketResDto addTicket(TicketDto ticket);

    List<TicketResDto> getAllTickets();

    TicketResDto getTicketById(Long id);

    TicketResDto getTicketByUserAndId(String username, Long id);
    List<TicketResDto> getTicketsByUser(String username);

    void updateTicket(Long id, Ticket ticket);

    void updateTicketStatus(Long ticketId, TicketStatusDto ticketStatusDto);

    TicketResDto updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto);

    TicketResDto updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto);

    TicketResDto updateIsFavourite(Long ticketId, IsFavoriteDto isFavoriteDto);
}
