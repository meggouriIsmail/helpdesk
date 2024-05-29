package com.helpdesk.ticketingmanagement.rabbitmq;

import com.helpdesk.ticketingmanagement.entities.Ticket;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketStatusProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishTicketStatusChange(Ticket ticket) {
        rabbitTemplate.convertAndSend("ticket-status-change", ticket);
    }
}
