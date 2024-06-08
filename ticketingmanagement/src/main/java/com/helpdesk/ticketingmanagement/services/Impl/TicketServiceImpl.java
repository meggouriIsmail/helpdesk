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
    public void updateTicketStatus(Long ticketId, TicketStatusDto ticketStatusDto) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        Ticket ticket = null;
        String oldStatus = "";
        String newStatus = ticketStatusDto.getStatus();
        if (optional.isPresent()) {
            ticket = optional.get();
            oldStatus = ticket.getStatus();
            ticket.setStatus(newStatus);
        }

        String username = getUsernameFromAuthentication();

        User user = userRepository.findByUsername(username).orElse(null);

        if (!oldStatus.equals(newStatus)) {
            assert ticket != null;
            ticketRepository.save(ticket);
            createAndSaveComment(ticket, user, "Status changed to " + newStatus + ".", TypeActivity.STATUS_CHANGED);
        }
    }

    @Override
    public Ticket updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        Ticket ticket = null;
        List<User> sharedWithUsers = new ArrayList<>();
        if (optional.isPresent()) {
            ticket = optional.get();
            sharedWithUsers = userRepository.findAllById(updateSharedWithDto.getSharedWithUserIds());
            ticket.setSharedWith(sharedWithUsers);
            ticketRepository.save(ticket);
        }

        String username = getUsernameFromAuthentication();

        User user = userRepository.findByUsername(username).orElse(null);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setTime(new Date());
        comment.setComment("Ticket updated: Shared with updated.");
        comment.setTypeActivity(TypeActivity.SHARED_WITH);
        comment.setShared_with(sharedWithUsers);
        commentRepository.save(comment);

        return ticket;
    }

    @Override
    public Ticket updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        Ticket ticket = null;
        if (optional.isPresent()) {
            ticket = optional.get();
            Optional<User> assignedToUserOpt = userRepository.findById(updateAssignedToDto.getAssignedToUserId());
            if (assignedToUserOpt.isPresent()) {
                ticket.setAssignedTo(assignedToUserOpt.get());
                ticketRepository.save(ticket);
            }
        }

        String username = getUsernameFromAuthentication();

        User user = userRepository.findByUsername(username).orElse(null);

        createAndSaveComment(ticket, user, "Assigned to updated.", TypeActivity.ASSIGNED_TO);
        return ticket;
    }

    private void createAndSaveComment(Ticket ticket, User user, String commentText, TypeActivity typeActivity) {
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setTime(new Date());
        comment.setComment("Ticket updated: " + commentText);
        comment.setTypeActivity(typeActivity);

        if (typeActivity.equals(TypeActivity.STATUS_CHANGED)) {
            comment.setStatus(ticket.getStatus());
        } else if (typeActivity.equals(TypeActivity.ASSIGNED_TO)) {
            comment.setAssignedTo(ticket.getAssignedTo());
        }

        commentRepository.save(comment);
    }

    private String getUsernameFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            username = jwt.getClaimAsString("preferred_username");
        }

        return username;
    }

}
