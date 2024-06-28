package com.helpdesk.ticketingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedBackDto {

    private UserResCommentDto owner;

    private String feedback;

    private double rating;

    private Date createdTime;
}
