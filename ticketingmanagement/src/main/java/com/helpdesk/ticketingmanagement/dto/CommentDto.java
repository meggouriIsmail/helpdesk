package com.helpdesk.ticketingmanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDto {
    private String content;
    private UserNameDto author;
}
