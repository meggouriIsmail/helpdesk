package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.User;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public interface UserService {
    User register(KeycloakAuthenticationToken token);
    User getLoggedInUser();
}
