package com.helpdesk.ticketingmanagement.rabbitmq;

import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.utils.TicketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
public class TicketEntityListener {

    private static CommentRepository commentRepository;
    private static TicketRepository ticketRepository;
    private static UserRepository userRepository;
    private static final ThreadLocal<Ticket> originalTicket = new ThreadLocal<>();


    @Autowired
    public void init(CommentRepository commentRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        TicketEntityListener.commentRepository = commentRepository;
        TicketEntityListener.userRepository = userRepository;
        TicketEntityListener.ticketRepository = ticketRepository;
    }

    @PreUpdate
    public void beforeUpdate(Ticket ticket) {
        // Store the original state of the ticket before the update
        originalTicket.set(TicketUtils.cloneTicket(ticket));
    }

    @PostUpdate
    public void onPostUpdate(Ticket ticket) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            jwt = (Jwt) authentication.getPrincipal();
            username = jwt.getClaimAsString("preferred_username");
        }

        User user = userRepository.findByUsername(username).get();

        // Get the original ticket from ThreadLocal
        Optional<Ticket> original = ticketRepository.findById(ticket.getId());
        originalTicket.remove(); // Clean up ThreadLocal

        // Create a comment only if there are changes
        if (original.isEmpty()) {
            Comment comment = new Comment();
            comment.setTicket(ticket);
            comment.setAuthor(user);
            comment.setTime(new Date());
            comment.setComment("Ticket updated: ");

            boolean hasChanges = false;

            // Check for changes and update the comment text accordingly
            if (!Objects.equals(original.get().getStatus(), ticket.getStatus())) {
                comment.setTypeActivity(TypeActivity.STATUS_CHANGED);
                comment.setStatus(ticket.getStatus());
                comment.setComment(comment.getComment() + "Status changed to " + ticket.getStatus() + ". ");
                hasChanges = true;
            }

//            if (!Objects.equals(original.get().getSharedWith(), ticket.getSharedWith())) {
//                comment.setTypeActivity(TypeActivity.SHARED_WITH);
//                comment.setComment(comment.getComment() + "Shared with:  ticket.getSharedWith().get(0) . ");
//                hasChanges = true;
//            }

            // Only save the comment if there are changes
            if (hasChanges) {
                commentRepository.save(comment);
            }
        }
    }
}
