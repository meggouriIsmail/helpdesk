package com.helpdesk.ticketingmanagement.websockets;

import com.helpdesk.ticketingmanagement.entities.Comment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendCommentNotification(Comment comment) {
        messagingTemplate.convertAndSend("/topic/comments", comment);
    }
}
