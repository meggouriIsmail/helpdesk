package com.helpdesk.ticketingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private DepartmentDto departmentDto;
}
