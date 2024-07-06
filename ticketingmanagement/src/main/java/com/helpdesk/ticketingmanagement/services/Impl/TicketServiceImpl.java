package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.*;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import com.helpdesk.ticketingmanagement.listeners.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.TypeRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.TicketService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public TicketResDto addTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setDescription(ticketDto.getDescription());
        ticket.setTitle(ticketDto.getTitle());
        ticket.setIsResolved("undefined");
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

        Ticket savedTicket = ticketRepository.save(ticket);

        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthor(new UserNameDto(owner.get().getUsername()));
        commentDto.setComment("New Ticket with reference: " + reference);
        comment.setTime(new Date());
        comment.setTicket(savedTicket);
        comment.setComment(commentDto.getComment());
        comment.setAuthor(owner.orElseThrow());
        comment.setTypeActivity(TypeActivity.TICKET_CREATED.value);

        Comment commentSaved = commentRepository.save(comment);
        rabbitTemplate.convertAndSend("commentQueue", commentSaved);

        return getTicketResDto(savedTicket);
    }

    @Override
    public List<TicketResDto> getAllTickets() {
        return ticketRepository.findAll().stream().map(TicketServiceImpl::getTicketResDto).collect(Collectors.toList());
    }

    @Override
    public List<TicketResDto> getFavouriteTickets(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent() && Objects.nonNull(optionalUser.get().getFavoriteTickets())) {
            return optionalUser.get().getFavoriteTickets().stream().map(TicketServiceImpl::getTicketResDto).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public TicketResDto getTicketById(Long id) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            return getTicketResDto(ticket);
        }
        return null;
    }

    private static TicketResDto getTicketResDto(Ticket ticket) {
        TicketResDto ticketResDto = TicketResDto.builder()
                .id(ticket.getId())
                .description(ticket.getDescription())
                .isResolved(ticket.getIsResolved())
                .createdTime(ticket.getCreatedTime())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .title(ticket.getTitle())
                .reference(ticket.getReference())
                .type(ticket.getType())
                .owner(UserTicketResDto.builder()
                        .id(ticket.getOwner().getId())
                        .username(ticket.getOwner().getUsername())
                        .docId(ticket.getOwner().getDocument() != null ? ticket.getOwner().getDocument().getId() : null)
                        .build())
                .build();
        if (!Objects.isNull(ticket.getAssignedTo())) {
            ticketResDto.setAssignedTo(UserTicketResDto.builder()
                    .id(ticket.getAssignedTo().getId())
                    .username(ticket.getAssignedTo().getUsername())
                    .docId(ticket.getAssignedTo().getDocument() != null ? ticket.getAssignedTo().getDocument().getId() : null)
                    .build());
        }
        if (!Objects.isNull(ticket.getSharedWith()) && !ticket.getSharedWith().isEmpty()) {
            List<UserTicketResDto> sharedWith = new ArrayList<>();
            ticket.getSharedWith().forEach(user -> {
                sharedWith.add(UserTicketResDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .docId(user.getDocument() != null ? user.getDocument().getId() : null)
                        .build());
            });
            ticketResDto.setSharedWith(sharedWith);
        }
        if (!Objects.isNull(ticket.getDocuments()) && !ticket.getDocuments().isEmpty()) {
            Set<Long> docsIds = ticket.getDocuments().stream().map(Document::getId).collect(Collectors.toSet());
            ticketResDto.setDocumentIds(docsIds);
        }

        return ticketResDto;
    }

    @Override
    public TicketResDto getTicketByUserAndId(String username, Long id) {
        return getTicketResDto(ticketRepository.findByOwnerUsernameAndId(username, id));
    }

    @Override
    public List<TicketResDto> getTicketsByUser(String username) {
        return ticketRepository.findAllByOwnerUsername(username).stream().map(TicketServiceImpl::getTicketResDto).toList();
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

        User user = userRepository.findByUsername(getUsernameFromAuthentication()).orElse(null);

        if (!oldStatus.equals(newStatus)) {
            assert ticket != null;
            ticketRepository.save(ticket);
            Comment comment = createAndSaveComment(ticket, user, "Status changed to " + newStatus + ".", TypeActivity.STATUS_CHANGED, ticketStatusDto);
            rabbitTemplate.convertAndSend("commentQueue", comment);
        }
    }

    @Override
    public TicketResDto updateSharedWith(Long ticketId, UpdateSharedWithDto updateSharedWithDto) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        Ticket ticket = null;
        List<User> sharedWithUsers = new ArrayList<>();
        if (optional.isPresent()) {
            ticket = optional.get();
            sharedWithUsers = userRepository.findAllById(updateSharedWithDto.getSharedWithUserIds());
            ticket.setSharedWith(sharedWithUsers);
            ticketRepository.save(ticket);
        }

        User user = userRepository.findByUsername(getUsernameFromAuthentication()).orElse(null);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setTime(new Date());
        comment.setComment("Ticket updated: Shared with updated.");
        comment.setTypeActivity(TypeActivity.SHARED_WITH.value);
        comment.setShared_with(sharedWithUsers);
        Comment saveComment = commentRepository.save(comment);

        rabbitTemplate.convertAndSend("commentQueue", saveComment);

        assert ticket != null;
        return getTicketResDto(ticket);
    }

    @Override
    public TicketResDto updateAssignedTo(Long ticketId, UpdateAssignedToDto updateAssignedToDto) {
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

        User user = userRepository.findByUsername(getUsernameFromAuthentication()).orElse(null);

        Comment comment = createAndSaveComment(ticket, user, "Assigned to updated.", TypeActivity.ASSIGNED_TO, null);
        rabbitTemplate.convertAndSend("commentQueue", comment);
        assert ticket != null;
        return getTicketResDto(ticket);
    }

    @Override
    public List<TicketResDto> findAllTicketsBySharedWithUsername(String username) {
        Optional<List<Ticket>> ticketsBySharedWithContainingUsername = ticketRepository.findAllTicketsBySharedWithUsername(username);
        return ticketsBySharedWithContainingUsername.map(tickets -> tickets.stream().map(TicketServiceImpl::getTicketResDto).toList()).orElseGet(List::of);
    }

    @Override
    public void updateIsFavourite(Long ticketId, IsFavoriteDto isFavoriteDto) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            if (isFavoriteDto.isFavorite) {
                userRepository.save(Objects.requireNonNull(addFavourite(ticketOptional.get())));
            } else {
                userRepository.save(Objects.requireNonNull(removeFavourite(ticketOptional.get())));
            }
        }
    }

    private User addFavourite(Ticket ticket) {
        User user = userRepository.findByUsername(getUsernameFromAuthentication()).orElse(null);
        if (user != null) {
            if (Objects.isNull(user.getFavoriteTickets())) {
                user.setFavoriteTickets(new ArrayList<>());
            }
            user.getFavoriteTickets().add(ticket);
            return user;
        }
        return null;
    }

    private User removeFavourite(Ticket ticket) {
        User user = userRepository.findByUsername(getUsernameFromAuthentication()).orElse(null);
        if (user != null) {
            user.getFavoriteTickets().remove(ticket);
            return user;
        }
        return null;
    }

    private Comment createAndSaveComment(Ticket ticket, User user, String commentText, TypeActivity typeActivity, TicketStatusDto ticketStatusDto) {
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setTime(new Date());
        comment.setComment("Ticket updated: " + commentText);
        comment.setTypeActivity(typeActivity.value);

        if (typeActivity.equals(TypeActivity.STATUS_CHANGED)) {
            comment.setStatus(ticket.getStatus());
            if (!ticketStatusDto.getExplication().isEmpty()) {
                comment.setComment(ticketStatusDto.getExplication());
            }
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
