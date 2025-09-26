package com.csi_rscoe.csi_backend;

import com.csi_rscoe.csi_backend.Models.User;
import com.csi_rscoe.csi_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test") // will not run in 'test' profile
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only create users if no users exist
        if (userRepository.count() == 0) {
            // === Admin User ===
            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setEmail("admin@csi.com");
            adminUser.setPassword(passwordEncoder.encode("admin@123"));
            adminUser.setRole("ADMIN");
            adminUser.setEnabled(true);
            userRepository.save(adminUser);

            // === Member 1 ===
            User member1 = new User();
            member1.setName("John Doe");
            member1.setEmail("john@csi.com");
            member1.setPassword(passwordEncoder.encode("pass@123"));
            member1.setRole("MEMBER");
            member1.setEnabled(true);
            userRepository.save(member1);

            // === Member 2 ===
            User member2 = new User();
            member2.setName("Jane Smith");
            member2.setEmail("jane@csi.com");
            member2.setPassword(passwordEncoder.encode("pass@123"));
            member2.setRole("MEMBER");
            member2.setEnabled(true);
            userRepository.save(member2);

            // === Member 3 ===
            User member3 = new User();
            member3.setName("Rahul Kumar");
            member3.setEmail("rahul@csi.com");
            member3.setPassword(passwordEncoder.encode("pass@123"));
            member3.setRole("MEMBER");
            member3.setEnabled(true);
            userRepository.save(member3);

            System.out.println("=== Default Users Created ===");
            System.out.println("Admin -> admin@csi.com / admin@123");
            System.out.println("Member1 -> john@csi.com / pass@123");
            System.out.println("Member2 -> jane@csi.com / pass@123");
            System.out.println("Member3 -> rahul@csi.com / pass@123");
            System.out.println("==============================");
        } else {
            System.out.println("Users already exist in database. Skipping initialization.");
        }
    }
}
