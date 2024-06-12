package com.helpdesk.ticketingmanagement.dto;

import com.helpdesk.ticketingmanagement.entities.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Department department;
    private Long docId;
}
