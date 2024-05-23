package com.helpdesk.ticketingmanagement.services.Impl;
import com.helpdesk.ticketingmanagement.entities.Role;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.repositories.RoleRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User saveUser(KeycloakAuthenticationToken token) {
        AccessToken accessToken = token.getAccount().getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(username));
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User user = User.builder()
                .username(username)
                .email(accessToken.getEmail())
                .firstName(accessToken.getGivenName())
                .lastName(accessToken.getFamilyName())
                .enabled(true)
                .build();

        Set<Role> roles = new HashSet<>();
        for (String roleName : token.getAccount().getRoles()) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role = roleRepository.save(role);
            }
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }
}


