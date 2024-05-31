package com.helpdesk.ticketingmanagement.services.Impl;
import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.entities.Role;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.repositories.RoleRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.security.KeycloakRegistration;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    public User saveOrUpdateUser(UserDto userDto) {
        keycloakAdminClientService.createUser(userDto);
        Optional<User> optional = userRepository.findByUsername(userDto.getUsername());
        User user = null;
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

        return userRepository.save(user);
    }

    public User getLoggedInUser() {
        // Assuming you have set up Spring Security correctly and have Keycloak integrated
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername("yana").orElseThrow();
    }
}


