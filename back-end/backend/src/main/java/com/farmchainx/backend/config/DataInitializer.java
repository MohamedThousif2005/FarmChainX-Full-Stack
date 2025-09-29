package com.farmchainx.backend.config;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Data Initializer Started ===");
        
        // Fix all existing users' passwords
        List<User> allUsers = userService.getAllUsers();
        int fixedCount = 0;
        
        for (User user : allUsers) {
            // Check if password is not BCrypt encoded
            if (!user.getPassword().startsWith("$2a$")) {
                System.out.println("Fixing password for user: " + user.getEmail());
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userService.updateUser(user);
                fixedCount++;
            }
            
            // Ensure all users are approved
            if (!user.getApproved()) {
                user.setApproved(true);
                userService.updateUser(user);
                System.out.println("Approved user: " + user.getEmail());
            }
        }
        
        System.out.println("=== Data Initializer Completed ===");
        System.out.println("Fixed " + fixedCount + " user passwords");
        System.out.println("Total users: " + allUsers.size());
        
        // Create admin user if it doesn't exist
        if (!userService.emailExists("admin@farmchainx.com")) {
            User adminUser = new User();
            adminUser.setFullName("Admin User");
            adminUser.setEmail("admin@farmchainx.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");
            adminUser.setApproved(true);
            
            userService.createUser(adminUser);
            System.out.println("Admin user created: admin@farmchainx.com / admin123");
        }
    }
}