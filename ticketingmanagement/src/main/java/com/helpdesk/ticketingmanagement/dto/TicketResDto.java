package com.helpdesk.ticketingmanagement.dto;

import com.helpdesk.ticketingmanagement.entities.TicketType;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResDto {
    private Long id;
    private String reference;
    private String description;
    private String title;
    private TicketType type;
    private String status;
    private String priority;
    private String isResolved;
    private boolean isFavorite;
    private Date createdTime;
    private Long ownerId;
    private Long assignedToId;
    private List<Long> sharedWithIds;
    private Set<Long> documentIds;
}
