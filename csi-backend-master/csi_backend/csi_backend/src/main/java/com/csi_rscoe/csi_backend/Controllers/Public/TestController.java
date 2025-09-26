package com.csi_rscoe.csi_backend.Controllers.Public;

import com.csi_rscoe.csi_backend.Models.User;
import com.csi_rscoe.csi_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:8081")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/create-admin")
    public String createAdmin() {
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@csi.com");
        adminUser.setPassword(passwordEncoder.encode("admin@123"));
        adminUser.setRole("ADMIN");
        adminUser.setEnabled(true);
        userRepository.save(adminUser);
        return "Admin user created: admin@csi.com / admin@123";
    }

    @PostMapping("/create-user")
    public String createUser() {
        User regularUser = new User();
        regularUser.setName("Test User");
        regularUser.setEmail("user@test.com");
        regularUser.setPassword(passwordEncoder.encode("pass@123"));
        regularUser.setRole("MEMBER");
        regularUser.setEnabled(true);
        userRepository.save(regularUser);
        return "Regular user created: user@test.com / pass@123";
    }

    @DeleteMapping("/clear-users")
    public String clearUsers() {
        userRepository.deleteAll();
        return "All users deleted";
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is running!";
    }
}
