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
public class UserRes {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean enabled;
    private String referenceUser;
    private Department department;
    private Long docId;
}
