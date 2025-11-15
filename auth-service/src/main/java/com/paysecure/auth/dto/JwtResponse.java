package com.paysecure.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private long expiresIn;
    private String username;
    private String email;
    private String role;
}
