package com.helpdesk.ticketingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTicketResDto {
    private Long id;
    private String username;
    private Long docId;
}
