package com.helpdesk.ticketingmanagement.rabbitmq;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketStatusProducer {

    private final RabbitTemplate rabbitTemplate;

    public TicketStatusProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTicketStatusChange(Ticket ticket) {
        rabbitTemplate.convertAndSend("ticket-status-change", ticket);
    }
}
