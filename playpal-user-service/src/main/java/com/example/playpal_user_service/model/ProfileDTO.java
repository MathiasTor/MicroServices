package com.example.playpal_user_service.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileDTO {

    private Long userId;
    private String name;

}
