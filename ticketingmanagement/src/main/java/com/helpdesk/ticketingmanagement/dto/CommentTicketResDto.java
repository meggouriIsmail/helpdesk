package com.helpdesk.ticketingmanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentTicketResDto {
    private Long id;
    private String reference;
    private String title;
}
