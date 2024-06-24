package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.User;

import java.util.List;

public interface UserService {
    void saveOrUpdateUser(UserDto userDto);

    UserResDto getLoggedInUser();

    UserResDto getUserById(Long id);

    List<User> getUsers();

    List<UserRes> getAllUsers();

    void desactivateUser(Long userId);

    User updateUser(Long userId, UserReqDto userReqDto);

    void updatePassword(UserReqPasswordDto password);
}
