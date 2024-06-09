package com.helpdesk.ticketingmanagement.security;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.ticketingmanagement.dto.UserDto;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class KeycloakRegistration {
    private final RestTemplate restTemplate;

    public KeycloakRegistration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createUser(UserDto userDto) {
        String accessToken = getAccessToken();

        // Prepare headers with Access Token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String requestBody = "{\n" +
                "    \"username\": \"" + userDto.getUsername() + "\",\n" +
                "    \"email\": \"" + userDto.getEmail() + "\",\n" +
                "    \"enabled\": true,\n" +
                "    \"firstName\": \"" + userDto.getFirstName() + "\",\n" +
                "    \"lastName\": \"" + userDto.getLastName() + "\",\n" +
                "    \"credentials\": [\n" +
                "        {\n" +
                "            \"temporary\": false,\n" +
                "            \"type\": \"password\",\n" +
                "            \"value\": \"" + userDto.getPassword() + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Create request entity with headers and body
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request to the URL
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:8085/admin/realms/helpdesk-realm/users",
                requestEntity,
                String.class
        );

        // Handle the response
        if (response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Success");
        } else {
            throw new RuntimeException("Failed to register this user");
        }
    }

    public void updatePassword(String password, String id) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String requestBody = "{\n" +
                "   \"temporary\": false,\n" +
                "   \"type\": \"password\",\n" +
                "   \"value\": \"" + password + "\"\n" +
                "}";

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        restTemplate.put(
                "http://localhost:8085/admin/realms/helpdesk-realm/users/"+ id + "/reset-password",
                requestEntity
        );

//        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
//            System.out.println("Success");
//        } else {
//            throw new RuntimeException("Failed to update password");
//        }
    }

    private String getAccessToken() {
        String tokenUrl = "http://localhost:8085/realms/helpdesk-realm/protocol/openid-connect/token";
        String clientId = "help-desk-client";
        String clientSecret = "KUe28JqHplZaSNg6257h5rQIlJQkRcr0";
        String username = "admin";
        String password = "123456";
        String scope = "openid";

        // Prepare request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("username", username);
        requestBody.add("password", password);
        requestBody.add("scope", scope);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create request entity with headers and body
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request to obtain access token
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, requestEntity, String.class);

        // Parse and return access token from the response
        // For simplicity, assuming the response body is JSON with "access_token" field
        // You may need to parse the JSON response accordingly
        // For better error handling, consider using ResponseEntity with an appropriate response type
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract access token from response", e);
        }
    }
}
