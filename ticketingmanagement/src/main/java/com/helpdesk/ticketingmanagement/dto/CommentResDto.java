package com.helpdesk.ticketingmanagement.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentResDto {
    private Long id;
    private UserResCommentDto author;
    private CommentTicketResDto ticket;
    private Date time;
    private String typeActivity;
    private String comment;
    private String status;
    private UserResCommentDto assignedTo;
    private List<UserResCommentDto> shared_with;
}
