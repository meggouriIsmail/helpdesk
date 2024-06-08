package com.helpdesk.ticketingmanagement.utils;

import com.helpdesk.ticketingmanagement.entities.Ticket;

import java.util.ArrayList;
import java.util.HashSet;

public class TicketUtils {
    public static Ticket cloneTicket(Ticket ticket) {
        Ticket clone = new Ticket();
        clone.setId(ticket.getId());
        clone.setDescription(ticket.getDescription());
        clone.setTitle(ticket.getTitle());
        clone.setType(ticket.getType());
        clone.setStatus(ticket.getStatus());
        clone.setPriority(ticket.getPriority());
        clone.setImpact(ticket.getImpact());
        clone.setOwner(ticket.getOwner());
        clone.setAssignedTo(ticket.getAssignedTo());
        clone.setSharedWith(new ArrayList<>(ticket.getSharedWith())); // assuming it's a Set
        return clone;
    }
}
