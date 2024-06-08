package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.TicketDto;
import com.helpdesk.ticketingmanagement.dto.TicketStatusDto;
import com.helpdesk.ticketingmanagement.dto.UpdateAssignedToDto;
import com.helpdesk.ticketingmanagement.dto.UpdateSharedWithDto;
import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.TicketType;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import com.helpdesk.ticketingmanagement.rabbitmq.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.TypeRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusProducer ticketStatusProducer;
    private final TypeRepository typeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketStatusProducer ticketStatusProducer, TypeRepository typeRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketStatusProducer = ticketStatusProducer;
        this.typeRepository = typeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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
        ticket.setSharedWith(List.of(sharedWith.orElseThrow()));
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
    public void updateTicket(Long id, Ticket ticket) {
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            jwt = (Jwt) authentication.getPrincipal();
            username = jwt.getClaimAsString("preferred_username");
        }

        User user = userRepository.findByUsername(username).get();

        // Publish the event if the status changes
        if (!oldStatus.equals(newStatus)) {
            Comment comment = new Comment();
            comment.setTicket(ticket);
            comment.setAuthor(user);
            comment.setTime(new Date());
            comment.setComment("Ticket updated: ");
            comment.setTypeActivity(TypeActivity.STATUS_CHANGED);
            comment.setStatus(ticket.getStatus());
            comment.setComment(comment.getComment() + "Status changed to " + ticket.getStatus() + ". ");

            commentRepository.save(comment);

            ticketStatusProducer.publishTicketStatusChange(ticket);
        }
    }

    public Ticket updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            List<User> sharedWithUsers = userRepository.findAllById(updateSharedWithDto.getSharedWithUserIds());
            ticket.getSharedWith().addAll(sharedWithUsers);
            return ticketRepository.save(ticket);
        } else {
            throw new RuntimeException("Ticket not found");
        }
    }

    public Ticket updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            Optional<User> assignedToUserOpt = userRepository.findById(updateAssignedToDto.getAssignedToUserId());
            if (assignedToUserOpt.isPresent()) {
                ticket.setAssignedTo(assignedToUserOpt.get());
                return ticketRepository.save(ticket);
            } else {
                throw new RuntimeException("User not found");
            }
        } else {
            throw new RuntimeException("Ticket not found");
        }
    }

}
