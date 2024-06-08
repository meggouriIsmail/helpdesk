package com.helpdesk.ticketingmanagement.listeners;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentListener {

    private final SimpMessagingTemplate messagingTemplate;

    public CommentListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "commentQueue")
    public void handleCommentNotification(CommentDto commentDto) {
        // Forward the comment to the frontend via WebSocket
        messagingTemplate.convertAndSend("/topic/comments", commentDto);
        System.err.println("Received Comment Notification: " + commentDto);
    }
}

