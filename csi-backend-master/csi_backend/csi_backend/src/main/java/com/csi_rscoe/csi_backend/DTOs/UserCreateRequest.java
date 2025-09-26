package com.csi_rscoe.csi_backend.DTOs;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String name;
    private String email;
    private String role = "user"; // default to "user"
}
