package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.dto.CommentResDto;
import com.helpdesk.ticketingmanagement.dto.CommentTicketResDto;
import com.helpdesk.ticketingmanagement.dto.UserResCommentDto;
import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import com.helpdesk.ticketingmanagement.listeners.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.CommentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketStatusProducer ticketStatusProducer;
    private final RabbitTemplate rabbitTemplate;

    public CommentServiceImpl(CommentRepository commentRepository, TicketRepository ticketRepository, UserRepository userRepository, TicketStatusProducer ticketStatusProducer, RabbitTemplate rabbitTemplate) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketStatusProducer = ticketStatusProducer;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void addComment(Long id, CommentDto commentDto) {
        Optional<Ticket> optional = ticketRepository.findById(id);
        Optional<User> optionalUser = userRepository.findByUsername(commentDto.getAuthor().getUsername());
        if (optional.isEmpty() && optionalUser.isEmpty()){
            return;
        }
        Ticket ticket = optional.get();
        User author = optionalUser.get();
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setTime(new Date());
        comment.setComment(commentDto.getComment());
        comment.setAuthor(author);
        comment.setTypeActivity(TypeActivity.COMMENT.value);

        Comment commentSaved = commentRepository.save(comment);
        rabbitTemplate.convertAndSend("commentQueue", commentSaved);
        ticketStatusProducer.sendCommentNotification(commentDto);
    }

    @Override
    public List<CommentResDto> getAllCommentsByUser(String username) {
        return commentRepository.findAllByAuthorUsername(username).stream().map(this::convertCommentToCommentResDto).toList();
    }

    @Override
    public List<CommentResDto> getAllCommentsByTicket(Long ticket_id) {
        return commentRepository.findAllByTicketId(ticket_id).stream().map(this::convertCommentToCommentResDto).toList();
    }

    private CommentResDto convertCommentToCommentResDto(Comment comment) {
        CommentResDto commentResDto = new CommentResDto();
        commentResDto.setId(comment.getId());
        commentResDto.setComment(comment.getComment());
        commentResDto.setStatus(comment.getStatus());
        commentResDto.setTime(comment.getTime());
        commentResDto.setTypeActivity(comment.getTypeActivity());
        if (Optional.ofNullable(comment.getTicket()).isPresent()) {
            commentResDto.setTicket(new CommentTicketResDto(comment.getTicket().getId(), comment.getTicket().getReference(), comment.getTicket().getTitle()));
        }
        if (Optional.ofNullable(comment.getAuthor()).isPresent()){
            commentResDto.setAuthor(getUserResCommentDto(comment.getAuthor()));
        }
        if (Optional.ofNullable(comment.getAssignedTo()).isPresent()){
            commentResDto.setAssignedTo(getUserResCommentDto(comment.getAssignedTo()));
        }
        if (Optional.ofNullable(comment.getShared_with()).isPresent()){
            List<UserResCommentDto> sharedWith = comment.getShared_with().stream().map(this::getUserResCommentDto).toList();
            commentResDto.setShared_with(sharedWith);
        }
        return commentResDto;
    }

    private UserResCommentDto getUserResCommentDto(User user) {
        return UserResCommentDto.builder()
                .username(user.getUsername())
                .docId(user.getDocument() != null ? user.getDocument().getId() : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .id(user.getId())
                .build();
    }
}
