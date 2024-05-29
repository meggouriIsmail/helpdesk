package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.entities.User;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface UserService {
    User saveOrUpdateUser(UserDto userDto);
    User getLoggedInUser();
}
