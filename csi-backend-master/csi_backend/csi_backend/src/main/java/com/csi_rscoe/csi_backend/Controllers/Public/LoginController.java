package com.csi_rscoe.csi_backend.Controllers.Public;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.csi_rscoe.csi_backend.DTOs.LoginRequest;
import com.csi_rscoe.csi_backend.DTOs.LoginResponse;
import com.csi_rscoe.csi_backend.Services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5173" })
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
