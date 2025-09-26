package com.csi_rscoe.csi_backend.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
