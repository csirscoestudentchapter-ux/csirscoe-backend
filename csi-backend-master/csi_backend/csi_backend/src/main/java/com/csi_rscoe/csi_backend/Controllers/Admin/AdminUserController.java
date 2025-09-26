package com.csi_rscoe.csi_backend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.csi_rscoe.csi_backend.DTOs.UserCreateRequest;
import com.csi_rscoe.csi_backend.Models.User;
import com.csi_rscoe.csi_backend.Services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = { "http://localhost:5173" })
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserCreateRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<User> resetPassword(@PathVariable Long id) {
        User user = userService.resetPassword(id);
        return ResponseEntity.ok(user);
    }
}
