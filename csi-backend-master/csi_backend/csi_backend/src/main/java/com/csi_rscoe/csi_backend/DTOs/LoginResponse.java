package com.csi_rscoe.csi_backend.DTOs;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private UserDetails userDetails;

    @Data
    public static class UserDetails {
        private Long id;
        private String name;
        private String email;
        private String role;
    }
}
