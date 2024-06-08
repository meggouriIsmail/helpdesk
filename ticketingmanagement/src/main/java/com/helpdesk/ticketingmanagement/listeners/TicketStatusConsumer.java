package com.helpdesk.ticketingmanagement.listeners;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TicketStatusConsumer {

    @RabbitListener(queues = "ticket-status-change")
    public void consumeTicketStatusChange(Ticket ticket) {
        // Implement your notification logic here
        sendNotification(ticket.getOwner(), ticket);
    }

    private void sendNotification(User user, Ticket ticket) {
        // Implement your notification logic, e.g., sending an email
        String message = String.format("Hello %s, the status of your ticket '%s' has changed to '%s'.",
                user.getFirstName()+" "+user.getLastName(), ticket.getTitle(), ticket.getStatus());

        System.err.println("hereeee : " + message);
        // Example: Send an email (you need to implement the sendEmail method)
        //sendEmail(user.getEmail(), "Ticket Status Changed", message);
    }

    private void sendEmail(String email, String subject, String message) {
        // Implement your email sending logic here
    }
}
