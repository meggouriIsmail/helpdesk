package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public User getCurrentUser() {
        return userService.getLoggedInUser();
    }
}
