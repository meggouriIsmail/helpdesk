package com.helpdesk.ticketingmanagement.dto;

import com.helpdesk.ticketingmanagement.entities.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Department department;
    private Long docId;
    private String status;
    private String post;
    private String phoneNumber;
    private String location;
    private String aboutMe;
    private Date joinDate;
    private String referenceUser;
    private boolean enabled;
}
