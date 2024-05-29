package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody UserDto userDto) {
        userService.saveOrUpdateUser(userDto);
    }

    @GetMapping("/me")
    public User getLoggedInUser() {
        return userService.getLoggedInUser();
    }
//    @GetMapping("/user")
//    public ResponseEntity<String> getCurrentUser() {
//        return new ResponseEntity<>(userService.getLoggedInUser(), HttpStatus.OK);
//    }
    @GetMapping("/auth")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
