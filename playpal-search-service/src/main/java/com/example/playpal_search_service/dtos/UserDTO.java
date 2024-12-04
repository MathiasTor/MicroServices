package com.example.playpal_search_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String bio; // Optional user description or profile information
}