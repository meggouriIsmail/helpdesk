package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.FeedBackDto;
import com.helpdesk.ticketingmanagement.dto.FeedBackReqDto;

public interface FeedBackService {
    void addFeedBack(Long id, FeedBackReqDto feedBackReqDto);
    FeedBackDto getFeedBackByTicket(Long ticketId);
}
