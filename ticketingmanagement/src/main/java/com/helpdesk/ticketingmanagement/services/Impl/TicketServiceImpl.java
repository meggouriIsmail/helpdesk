package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.TicketType;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.rabbitmq.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.TypeRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusProducer ticketStatusProducer;
    private final TypeRepository typeRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketStatusProducer ticketStatusProducer, TypeRepository typeRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketStatusProducer = ticketStatusProducer;
        this.typeRepository = typeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Ticket addTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setDescription(ticketDto.getDescription());
        ticket.setTitle(ticketDto.getTitle());

        Optional<TicketType> optionalTicketType = typeRepository.getTicketTypeByCode(ticketDto.getType().getCode());
        Optional<User> assignedTo = userRepository.findByUsername(ticketDto.getAssignedTo().getUsername());
        Optional<User> owner = userRepository.findByUsername(ticketDto.getOwner().getUsername());
        Optional<User> sharedWith = userRepository.findByUsername(ticketDto.getSharedWith().getUsername());

        ticket.setType(optionalTicketType.orElseThrow());
        ticket.setAssignedTo(assignedTo.orElseThrow());
        ticket.setSharedWith(sharedWith.orElseThrow());
        ticket.setOwner(owner.orElseThrow());

        ticket.setStatus(ticketDto.getStatus());
        ticket.setPriority(ticketDto.getPriority());
        ticket.setImpact(ticketDto.getImpact());

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

    @Override
    @Transactional
    public void updateTicketStatus(Long ticketId, TicketStatusDto TicketStatusDto) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        Ticket ticket = null;
        String oldStatus = "";
        String newStatus = TicketStatusDto.getStatus();
        if (optional.isPresent()) {
            ticket = optional.get();
            oldStatus = ticket.getStatus();
            ticket.setStatus(newStatus);

            ticketRepository.save(ticket);
        }

        // Publish the event if the status changes
        if (!oldStatus.equals(newStatus)) {
            ticketStatusProducer.publishTicketStatusChange(ticket);
        }
    }


}
