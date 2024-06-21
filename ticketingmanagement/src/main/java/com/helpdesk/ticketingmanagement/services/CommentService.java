package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.CommentDto;
import com.helpdesk.ticketingmanagement.dto.CommentResDto;

import java.util.List;

public interface CommentService {
    void addComment(Long id, CommentDto commentDto);
    List<CommentResDto> getAllCommentsByUser(String username);
    List<CommentResDto> getAllCommentsByTicket(Long ticket_id);
}
