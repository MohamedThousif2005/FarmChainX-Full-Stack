package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class AuthController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            System.out.println("=== REGISTRATION ATTEMPT ===");
            System.out.println("Email: " + user.getEmail());
            System.out.println("Full Name: " + user.getFullName());
            System.out.println("Role: " + user.getRole());
            System.out.println("Farm Name: " + user.getFarmName());
            System.out.println("Farm Size: " + user.getFarmSize());
            System.out.println("Company Name: " + user.getCompanyName());
            System.out.println("Delivery Area: " + user.getDeliveryArea());
            System.out.println("Preferences: " + user.getPreferences());
            
            // Validate required fields
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email is required"));
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
            }
            
            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Full name is required"));
            }
            
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Role is required"));
            }
            
            // Email validation
            if (!isValidEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Please enter a valid email address"));
            }
            
            // Password strength validation
            if (user.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password must be at least 6 characters"));
            }
            
            if (userService.emailExists(user.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Email already exists");
                response.put("code", "EMAIL_EXISTS");
                return ResponseEntity.badRequest().body(response);
            }
            
            User newUser = new User();
            newUser.setFullName(user.getFullName().trim());
            newUser.setEmail(user.getEmail().trim().toLowerCase());
            
            // ENCODE PASSWORD
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            newUser.setPassword(encodedPassword);
            
            // Set role (with validation)
            String role = user.getRole().toUpperCase();
            if (!isValidRole(role)) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid role specified. Must be FARMER, DISTRIBUTOR, or CONSUMER"));
            }
            newUser.setRole(role);
            
            newUser.setApproved(true);
            
            // Set optional fields
            if (user.getPhone() != null) newUser.setPhone(user.getPhone().trim());
            if (user.getAddress() != null) newUser.setAddress(user.getAddress().trim());
            
            // Set role-specific optional fields
            if (user.getFarmName() != null) newUser.setFarmName(user.getFarmName().trim());
            if (user.getFarmSize() != null) newUser.setFarmSize(user.getFarmSize().trim());
            if (user.getCompanyName() != null) newUser.setCompanyName(user.getCompanyName().trim());
            if (user.getDeliveryArea() != null) newUser.setDeliveryArea(user.getDeliveryArea().trim());
            if (user.getPreferences() != null) newUser.setPreferences(user.getPreferences().trim());
            
            User createdUser = userService.createUser(newUser);
            
            // Create response without sensitive data
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", createdUser.getId());
            userResponse.put("email", createdUser.getEmail());
            userResponse.put("fullName", createdUser.getFullName());
            userResponse.put("role", createdUser.getRole());
            userResponse.put("approved", createdUser.getApproved());
            userResponse.put("farmName", createdUser.getFarmName());
            userResponse.put("companyName", createdUser.getCompanyName());
            userResponse.put("createdAt", createdUser.getCreatedAt());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful! You can now login.");
            response.put("user", userResponse);
            response.put("status", "success");
            
            System.out.println("✅ Registration successful for: " + user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "Registration failed: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Email: " + loginRequest.getEmail());
            
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email is required"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
            }
            
            User user = userService.getUserByEmail(loginRequest.getEmail().trim().toLowerCase())
                    .orElseThrow(() -> {
                        System.out.println("User not found: " + loginRequest.getEmail());
                        return new RuntimeException("Invalid email or password");
                    });
            
            System.out.println("User found: " + user.getEmail());
            System.out.println("User role: " + user.getRole());
            System.out.println("User approved: " + user.getApproved());
            
            // Check password match
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            System.out.println("Password matches: " + passwordMatches);
            
            if (!passwordMatches) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid email or password");
                response.put("code", "INVALID_CREDENTIALS");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if user is approved
            if (!user.getApproved()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Your account is pending approval. Please contact administrator.");
                response.put("code", "ACCOUNT_PENDING");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("fullName", user.getFullName());
            userResponse.put("email", user.getEmail());
            userResponse.put("role", user.getRole());
            userResponse.put("approved", user.getApproved());
            userResponse.put("phone", user.getPhone());
            userResponse.put("address", user.getAddress());
            userResponse.put("farmName", user.getFarmName());
            userResponse.put("farmSize", user.getFarmSize());
            userResponse.put("companyName", user.getCompanyName());
            userResponse.put("deliveryArea", user.getDeliveryArea());
            userResponse.put("preferences", user.getPreferences());
            userResponse.put("createdAt", user.getCreatedAt());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", userResponse);
            response.put("role", user.getRole());
            response.put("status", "success");
            
            System.out.println("✅ Login successful for: " + user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "Login failed: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> authStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Auth Controller");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Auth service is running on port 8090");
        response.put("database", "farmchainx_db");
        response.put("corsEnabled", true);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            String email = "test@farmchainx.com";
            if (!userService.emailExists(email)) {
                User testUser = new User();
                testUser.setFullName("Test User");
                testUser.setEmail(email);
                testUser.setPassword(passwordEncoder.encode("password123"));
                testUser.setRole("FARMER");
                testUser.setApproved(true);
                testUser.setPhone("1234567890");
                testUser.setAddress("Test Address, Farm City");
                testUser.setFarmName("Green Valley Farm");
                testUser.setFarmSize("50 acres");
                
                User createdUser = userService.createUser(testUser);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Test user created successfully");
                response.put("user", createdUser.getEmail());
                response.put("credentials", "test@farmchainx.com / password123");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Test user already exists");
                response.put("credentials", "test@farmchainx.com / password123");
                response.put("status", "info");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create test user: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/test-db-connection")
    public ResponseEntity<?> testDbConnection() {
        try {
            long userCount = userService.countUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("database", "farmchainx_db");
            response.put("connection", "Active");
            response.put("totalUsers", userCount);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("database", "farmchainx_db");
            response.put("connection", "Failed");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Helper methods
    private boolean isValidRole(String role) {
        return role.equals("FARMER") || role.equals("DISTRIBUTOR") || 
               role.equals("CONSUMER") || role.equals("ADMIN");
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
    
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("status", "error");
        return response;
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        @Override
        public String toString() {
            return "LoginRequest{email='" + email + "', password='[PROTECTED]'}";
        }
    }
}