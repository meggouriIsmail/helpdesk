package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.entities.DocType;
import com.helpdesk.ticketingmanagement.entities.Document;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.repositories.DocTypeRepository;
import com.helpdesk.ticketingmanagement.repositories.DocumentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.services.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DocTypeRepository typeRepository;
    private final DocumentRepository documentRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, DocTypeRepository typeRepository, DocumentRepository documentRepository) {
        this.ticketRepository = ticketRepository;
        this.typeRepository = typeRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public Ticket addTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(Long id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        return ticket.orElse(null);
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticket) {
        Optional<Ticket> optional = ticketRepository.findById(id);
        Ticket toUpdate = null;
        if (optional.isPresent()) {
            toUpdate = optional.get();
            toUpdate.setDescription(ticket.getDescription());
            toUpdate.setImpact(ticket.getImpact());
            toUpdate.setPriority(ticket.getPriority());
            toUpdate.setType(ticket.getType());
            toUpdate.setStatus(ticket.getStatus());
            toUpdate.setTitle(ticket.getTitle());
        }
        return toUpdate;
    }


}
