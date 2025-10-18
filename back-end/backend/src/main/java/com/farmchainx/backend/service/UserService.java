package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);
    
    public boolean emailExists(String email) {
        boolean exists = userRepository.existsByEmail(email);
        logger.debug("Email exists check for {}: {}", email, exists);
        return exists;
    }
    
    public User createUser(User user) {
        try {
            // Ensure role consistency
            String role = user.getRole();
            if ("FAWBER".equals(role)) {
                user.setRole("FARMER");
            } else if ("CONSUME".equals(role)) {
                user.setRole("CONSUMER");
            } else if ("DISTRIBUTO".equals(role)) {
                user.setRole("DISTRIBUTOR");
            }
            
            // Auto-approve all users
            user.setApproved(true);
            
            logger.info("Creating user: {} with role: {}", user.getEmail(), user.getRole());
            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {}", savedUser.getId());
            
            return savedUser;
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }
    
    public Optional<User> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        logger.debug("Retrieved user by email {}: {}", email, user.isPresent() ? "Found" : "Not found");
        return user;
    }
    
    public User updateUser(User user) {
        try {
            // Ensure role consistency before updating
            String role = user.getRole();
            if ("FAWBER".equals(role)) {
                user.setRole("FARMER");
            } else if ("CONSUME".equals(role)) {
                user.setRole("CONSUMER");
            } else if ("DISTRIBUTO".equals(role)) {
                user.setRole("DISTRIBUTOR");
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            logger.info("User updated successfully: {}", updatedUser.getEmail());
            
            return updatedUser;
            
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users from database", users.size());
        
        for (User user : users) {
            logger.debug("User ID: {}, Email: {}, FullName: {}, Role: {}, Approved: {}", 
                user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getApproved());
        }
        
        return users;
    }
    
    public List<User> getPendingApprovals() {
        // Since all users are auto-approved, this will return empty list
        return List.of();
    }
    
    public User approveUser(Long userId) {
        // Since all users are auto-approved, just return the user
        return userRepository.findById(userId).orElse(null);
    }
    
    public void rejectUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            if ("ADMIN".equals(user.getRole())) {
                throw new RuntimeException("Cannot delete admin users");
            }
            
            userRepository.delete(user);
            logger.info("User deleted successfully: {}", user.getEmail());
            
        } catch (RuntimeException e) {
            logger.error("Error rejecting user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error rejecting user: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }
    
    public Map<String, Long> getUserStats() {
        try {
            Map<String, Long> stats = new HashMap<>();
            
            stats.put("totalUsers", userRepository.count());
            stats.put("pendingApprovals", 0L); // No pending approvals
            stats.put("approvedUsers", userRepository.count()); // All users are approved
            
            // Use the repository methods for counting
            long farmers = userRepository.countByRole("FARMER");
            long distributors = userRepository.countByRole("DISTRIBUTOR");
            long consumers = userRepository.countByRole("CONSUMER");
            long admins = userRepository.countByRole("ADMIN");
            
            stats.put("totalFarmers", farmers);
            stats.put("totalDistributors", distributors);
            stats.put("totalConsumers", consumers);
            stats.put("totalAdmins", admins);
            
            logger.info("User stats generated: {}", stats);
            return stats;
            
        } catch (Exception e) {
            logger.error("Error generating detailed stats, using fallback: {}", e.getMessage());
            
            Map<String, Long> fallbackStats = new HashMap<>();
            List<User> allUsers = userRepository.findAll();
            
            fallbackStats.put("totalUsers", (long) allUsers.size());
            fallbackStats.put("pendingApprovals", 0L);
            fallbackStats.put("approvedUsers", (long) allUsers.size());
            
            fallbackStats.put("totalFarmers", allUsers.stream()
                .filter(u -> "FARMER".equals(u.getRole())).count());
            fallbackStats.put("totalDistributors", allUsers.stream()
                .filter(u -> "DISTRIBUTOR".equals(u.getRole())).count());
            fallbackStats.put("totalConsumers", allUsers.stream()
                .filter(u -> "CONSUMER".equals(u.getRole())).count());
            fallbackStats.put("totalAdmins", allUsers.stream()
                .filter(u -> "ADMIN".equals(u.getRole())).count());
            
            return fallbackStats;
        }
    }
    
    public long countUsers() {
        long count = userRepository.count();
        logger.debug("Total user count: {}", count);
        return count;
    }
    
    public long countPendingUsers() {
        return 0; // No pending users
    }
    
    public Optional<User> getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        logger.debug("Retrieved user by ID {}: {}", userId, user.isPresent() ? "Found" : "Not found");
        return user;
    }
    
    public long countApprovedUsers() {
        return userRepository.count(); // All users are approved
    }
    
    public List<User> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        users.forEach(user -> {
            String userRole = user.getRole();
            if ("FAWBER".equals(userRole)) {
                user.setRole("FARMER");
            } else if ("CONSUME".equals(userRole)) {
                user.setRole("CONSUMER");
            } else if ("DISTRIBUTO".equals(userRole)) {
                user.setRole("DISTRIBUTOR");
            }
        });
        logger.debug("Retrieved {} users with role: {}", users.size(), role);
        return users;
    }
    
    public boolean validateUserCredentials(String email, String password) {
        Optional<User> user = getUserByEmail(email);
        return user.isPresent() && user.get().getApproved();
    }
}