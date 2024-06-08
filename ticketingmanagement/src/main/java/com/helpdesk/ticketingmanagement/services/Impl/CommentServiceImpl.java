package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.listeners.TicketStatusProducer;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.CommentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketStatusProducer ticketStatusProducer;

    public CommentServiceImpl(CommentRepository commentRepository, TicketRepository ticketRepository, UserRepository userRepository, TicketStatusProducer ticketStatusProducer) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketStatusProducer = ticketStatusProducer;
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
        //comment.setText(commentDto.getContent());
        comment.setAuthor(author);

//        ticket.setComments(List.of(comment));

        commentRepository.save(comment);
        ticketStatusProducer.sendCommentNotification(commentDto);
    }
}
