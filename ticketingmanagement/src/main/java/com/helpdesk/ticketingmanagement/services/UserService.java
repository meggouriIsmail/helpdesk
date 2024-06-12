package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.dto.UserReqDto;
import com.helpdesk.ticketingmanagement.dto.UserReqPasswordDto;
import com.helpdesk.ticketingmanagement.dto.UserResDto;
import com.helpdesk.ticketingmanagement.entities.User;

import java.util.List;

public interface UserService {
    void saveOrUpdateUser(UserDto userDto);

    UserResDto getLoggedInUser();

    User getUserById(Long id);

    List<User> getUsers();

    void desactivateUser(Long userId);

    User updateUser(Long userId, UserReqDto userReqDto);

    void updatePassword(UserReqPasswordDto password);
}
