package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("service", "Admin Controller");
            response.put("database", "Connected");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            response.put("totalUsers", userService.countUsers());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Health check failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/user-stats")
    public ResponseEntity<?> getUserStats() {
        try {
            Map<String, Long> stats = userService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get user stats: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<?> getPendingApprovals() {
        try {
            // Since all users are auto-approved, return empty list
            List<User> pendingUsers = userService.getPendingApprovals();
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get pending approvals: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            
            // Create response with user data (excluding passwords)
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Users retrieved successfully");
            response.put("users", users);
            response.put("count", users.size());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            System.out.println("✅ Admin: Retrieved " + users.size() + " users from database");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Admin: Error retrieving users: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("error", "Failed to get all users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/approve-user/{userId}")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        try {
            User user = userService.approveUser(userId);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User approved successfully");
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found with ID: " + userId);
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to approve user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/reject-user/{userId}")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {
        try {
            userService.rejectUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User rejected and deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to reject user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // NEW: Test endpoint for admin dashboard
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("service", "Admin Test Endpoint");
        response.put("message", "Admin controller is working!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    // NEW: Get users endpoint for admin dashboard
    @GetMapping("/users")
    public ResponseEntity<?> getUsersForAdmin() {
        try {
            List<User> users = userService.getAllUsers();
            
            // Format users for frontend (remove sensitive data)
            List<Map<String, Object>> formattedUsers = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("fullName", user.getFullName());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());
                userMap.put("phone", user.getPhone());
                userMap.put("address", user.getAddress());
                userMap.put("approved", user.getApproved());
                userMap.put("farmName", user.getFarmName());
                userMap.put("farmSize", user.getFarmSize());
                userMap.put("companyName", user.getCompanyName());
                userMap.put("deliveryArea", user.getDeliveryArea());
                userMap.put("preferences", user.getPreferences());
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("updatedAt", user.getUpdatedAt());
                return userMap;
            }).toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Users retrieved successfully for admin dashboard");
            response.put("users", formattedUsers);
            response.put("count", formattedUsers.size());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            System.out.println("✅ Admin Dashboard: Sent " + formattedUsers.size() + " users to frontend");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Admin Dashboard: Error retrieving users: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("error", "Failed to get users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}