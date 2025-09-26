package com.csi_rscoe.csi_backend.controller;

import com.csi_rscoe.csi_backend.model.Member;
import com.csi_rscoe.csi_backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody Member member) {
        try {
            // Check if email already exists
            if (memberRepository.existsByEmail(member.getEmail())) {
                return ResponseEntity.badRequest().body("Email already registered");
            }
            
            // Save the new member
            Member savedMember = memberRepository.save(member);
            return ResponseEntity.ok(savedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }
}