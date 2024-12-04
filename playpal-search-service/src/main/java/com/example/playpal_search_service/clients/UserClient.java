package com.example.playpal_search_service.clients;


import com.example.playpal_search_service.dtos.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplateBuilder builder, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = builder.build();
        this.userServiceUrl = userServiceUrl;
    }

    public boolean validateUser(Long userId) {
        String url = userServiceUrl + "/api/users/validate/" + userId;
        log.info("Validating user with ID: {}", userId);
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            if (response.getBody() != null && response.getBody()) {
                log.info("User ID {} is valid.", userId);
                return true;
            }
        } catch (Exception e) {
            log.error("Error while validating user ID {}: {}", userId, e.getMessage());
        }
        log.warn("User ID {} is invalid or User Service is unreachable.", userId);
        return false;
    }

    public String getUserName(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        log.info("Fetching user details for ID: {}", userId);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch user details for ID {}: {}", userId, e.getMessage());
            return "Unknown User";
        }
    }



}