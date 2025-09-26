package com.csi_rscoe.csi_backend.Services;

import com.csi_rscoe.csi_backend.DTOs.LoginRequest;
import com.csi_rscoe.csi_backend.DTOs.LoginResponse;
import com.csi_rscoe.csi_backend.DTOs.UserCreateRequest;
import com.csi_rscoe.csi_backend.Models.User;
import com.csi_rscoe.csi_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();

        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())
                && user.getEnabled()) {
            response.setSuccess(true);
            response.setMessage("Login successful!");

            LoginResponse.UserDetails userDetails = new LoginResponse.UserDetails();
            userDetails.setId(user.getId());
            userDetails.setName(user.getName());
            userDetails.setEmail(user.getEmail());
            userDetails.setRole(user.getRole());

            response.setUserDetails(userDetails);
        } else {
            response.setSuccess(false);
            response.setMessage("Invalid email or password or account is disabled!");
        }

        return response;
    }

    public User createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole().toUpperCase());
        user.setEnabled(true);

        // Set password based on role
        String password;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            password = "admin@123";
        } else {
            password = "pass@123";
        }
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserCreateRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = optionalUser.get();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole().toUpperCase());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found!");
        }
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User resetPassword(Long id) {
        User user = getUserById(id);
        String newPassword;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            newPassword = "admin@123";
        } else {
            newPassword = "pass@123";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
