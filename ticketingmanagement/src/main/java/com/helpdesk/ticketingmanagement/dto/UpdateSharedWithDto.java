package com.helpdesk.ticketingmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateSharedWithDto {
    private List<Long> sharedWithUserIds;
}
