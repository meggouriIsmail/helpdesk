package com.helpdesk.ticketingmanagement.services.Impl;
import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.dto.UserReqDto;
import com.helpdesk.ticketingmanagement.entities.Role;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.repositories.RoleRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.security.KeycloakRegistration;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final KeycloakRegistration keycloakAdminClientService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, KeycloakRegistration keycloakAdminClientService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    public void saveOrUpdateUser(UserDto userDto) {
        keycloakAdminClientService.createUser(userDto);
        Optional<User> optional = userRepository.findByUsername(userDto.getUsername());
        User user;
        if (optional.isEmpty()) {
            user = new User();
            user.setUsername(userDto.getUsername());
        } else { user = optional.get(); }
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        userDto.getRoles().forEach(rolee -> {
            Role role = roleRepository.findByName(rolee.getName());
            if (role == null) {
                role = new Role();
                role.setName(rolee.getName());
                roleRepository.save(role);
            }
            roles.add(role);
        });
        user.setRoles(roles);

        userRepository.save(user);
    }

    public User getLoggedInUser() {
        String username = getUsernameFromAuthentication();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void desactivateUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setEnabled(false);
            userRepository.save(user);
        }
    }

    @Override
    public User updateUser(Long userId, UserReqDto userReqDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setFirstName(userReqDto.getFirstName());
            user.setLastName(userReqDto.getLastName());
            return userRepository.save(user);
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


