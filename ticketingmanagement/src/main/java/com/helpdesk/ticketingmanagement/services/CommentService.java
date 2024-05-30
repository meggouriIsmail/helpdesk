package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.CommentDto;

public interface CommentService {
    void addComment(Long id, CommentDto commentDto);
}
