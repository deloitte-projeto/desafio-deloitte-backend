package com.projeto.deloitte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
} 