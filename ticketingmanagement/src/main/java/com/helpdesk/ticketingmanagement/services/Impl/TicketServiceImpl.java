package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.TicketType;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import com.helpdesk.ticketingmanagement.listeners.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.TypeRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.TicketService;
import com.helpdesk.ticketingmanagement.websockets.WebSocketService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TypeRepository typeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RabbitTemplate rabbitTemplate;

    public TicketServiceImpl(TicketRepository ticketRepository, TypeRepository typeRepository, UserRepository userRepository, CommentRepository commentRepository, RabbitTemplate rabbitTemplate) {
        this.ticketRepository = ticketRepository;
        this.typeRepository = typeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Ticket addTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setDescription(ticketDto.getDescription());
        ticket.setTitle(ticketDto.getTitle());
        int randomNumber = 10000000 + RandomUtils.nextInt(90000000);
        String reference = "RT-" + randomNumber;
        ticket.setReference(reference);

        Optional<TicketType> optionalTicketType = typeRepository.getTicketTypeByCode(ticketDto.getType().getCode());
        Optional<User> owner = userRepository.findByUsername(getUsernameFromAuthentication());

        List<User> sharedWith = new ArrayList<>();
        ticketDto.getSharedWith().forEach(userNameDto -> {
            Optional<User> optional = userRepository.findByUsername(userNameDto.getUsername());
            optional.ifPresent(sharedWith::add);
        });

        optionalTicketType.ifPresent(ticket::setType);
        ticket.setSharedWith(sharedWith);
        ticket.setOwner(owner.orElseThrow());

        ticket.setStatus("Open");
        ticket.setPriority(ticketDto.getPriority());

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
    public Ticket getTicketByUserAndId(UserNameDto userNameDto, Long id) {
        return ticketRepository.findByOwnerUsernameAndId(userNameDto.getUsername(), id);
    }

    @Override
    public List<Ticket> getTicketsByUserAndId(UserNameDto userNameDto) {
        return ticketRepository.findAllByOwnerUsername(userNameDto.getUsername());
    }

    @Override
    public void updateTicket(Long id, Ticket ticket) {
        Optional<Ticket> optional = ticketRepository.findById(id);
        Ticket toUpdate;
        if (optional.isPresent()) {
            toUpdate = optional.get();
            toUpdate.setDescription(ticket.getDescription());
            toUpdate.setPriority(ticket.getPriority());
            toUpdate.setType(ticket.getType());
            toUpdate.setStatus(ticket.getStatus());
            toUpdate.setTitle(ticket.getTitle());
            ticketRepository.save(toUpdate);
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
            Comment comment = createAndSaveComment(ticket, user, "Status changed to " + newStatus + ".", TypeActivity.STATUS_CHANGED);
            rabbitTemplate.convertAndSend("commentQueue", comment);
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
        comment.setTypeActivity(TypeActivity.SHARED_WITH.value);
        comment.setShared_with(sharedWithUsers);
        Comment saveComment = commentRepository.save(comment);

        rabbitTemplate.convertAndSend("commentQueue", saveComment);

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

        Comment comment = createAndSaveComment(ticket, user, "Assigned to updated.", TypeActivity.ASSIGNED_TO);
        rabbitTemplate.convertAndSend("commentQueue", comment);
        return ticket;
    }

    private Comment createAndSaveComment(Ticket ticket, User user, String commentText, TypeActivity typeActivity) {
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setTime(new Date());
        comment.setComment("Ticket updated: " + commentText);
        comment.setTypeActivity(typeActivity.value);

        if (typeActivity.equals(TypeActivity.STATUS_CHANGED)) {
            comment.setStatus(ticket.getStatus());
        } else if (typeActivity.equals(TypeActivity.ASSIGNED_TO)) {
            comment.setAssignedTo(ticket.getAssignedTo());
        }

        return commentRepository.save(comment);
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
