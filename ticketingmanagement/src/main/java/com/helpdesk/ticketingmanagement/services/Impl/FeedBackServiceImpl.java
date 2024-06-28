package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.FeedBackDto;
import com.helpdesk.ticketingmanagement.dto.FeedBackReqDto;
import com.helpdesk.ticketingmanagement.dto.UserResCommentDto;
import com.helpdesk.ticketingmanagement.entities.FeedBack;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.repositories.FeedBackRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.services.FeedBackService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public FeedBackServiceImpl(FeedBackRepository feedBackRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.feedBackRepository = feedBackRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addFeedBack(Long id, FeedBackReqDto feedBackReqDto) {
        Optional<Ticket> ticketOptional =  ticketRepository.findById(id);
        Optional<User> owner = userRepository.findByUsername(getUsernameFromAuthentication());

        if(ticketOptional.isPresent() && owner.isPresent()) {
            FeedBack feedBack = FeedBack.builder()
                    .ticket(ticketOptional.get())
                    .owner(owner.get())
                    .feedback(feedBackReqDto.getFeedback())
                    .rating(feedBackReqDto.getRating())
                    .build();
            feedBackRepository.save(feedBack);
        }
    }

    @Override
    public FeedBackDto getFeedBackByTicket(Long ticketId) {
        Optional<FeedBack> feedBackOptional = feedBackRepository.findByTicketId(ticketId);
        if (feedBackOptional.isPresent()) {
            FeedBack feedBack = feedBackOptional.get();
            UserResCommentDto userResCommentDto = UserResCommentDto.builder()
                    .id(feedBack.getOwner().getId())
                    .firstName(feedBack.getOwner().getFirstName())
                    .lastName(feedBack.getOwner().getLastName())
                    .username(feedBack.getOwner().getUsername())
                    .docId(feedBack.getOwner().getDocument() != null ? feedBack.getOwner().getDocument().getId() : null)
                    .build();

            return FeedBackDto.builder()
                    .owner(userResCommentDto)
                    .rating(feedBack.getRating())
                    .feedback(feedBack.getFeedback())
                    .createdTime(feedBack.getCreatedTime())
                    .build();
        }
        return null;
    }

    private String getUsernameFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            username = jwt.getClaimAsString("preferred_username");
        }

        return username;
    }
}
