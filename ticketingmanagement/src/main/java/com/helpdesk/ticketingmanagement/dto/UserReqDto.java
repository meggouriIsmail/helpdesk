package com.helpdesk.ticketingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReqDto {
    private String firstName;
    private String lastName;
    private String post;
    private String phoneNumber;
    private String location;
    private String aboutMe;
    private DepartmentDto departmentDto;
}
