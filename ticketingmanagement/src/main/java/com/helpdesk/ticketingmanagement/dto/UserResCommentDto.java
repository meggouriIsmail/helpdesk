package com.helpdesk.ticketingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResCommentDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Long docId;
}
