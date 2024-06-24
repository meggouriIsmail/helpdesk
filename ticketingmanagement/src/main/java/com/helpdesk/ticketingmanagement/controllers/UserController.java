package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.dto.*;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public void registerUser(@RequestBody UserDto userDto) {
        userService.saveOrUpdateUser(userDto);
    }

    @GetMapping("/loggedIn")
    @PreAuthorize("hasAuthority('USER')")
    public UserResDto getLoggedInUser() {
        return userService.getLoggedInUser();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public UserResDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/desactivate/{userId}")
    @PreAuthorize("hasAuthority('HELPDESK')")
    public void desactivateUser(@PathVariable Long userId) {
        userService.desactivateUser(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER')")
    public User updateUser(@PathVariable Long userId, @RequestBody UserReqDto userReqDto) {
        return userService.updateUser(userId, userReqDto);
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasAuthority('USER')")
    public void updatePassword(@RequestBody UserReqPasswordDto passwordDto) {
        userService.updatePassword(passwordDto);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<UserRes>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/auth")
    @PreAuthorize("hasAuthority('USER')")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
