package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return userRepository.existsByEmail(email);
    }
    
    public User createUser(User user) {
        // Ensure role consistency
        if ("FAWBER".equals(user.getRole())) {
            user.setRole("FARMER");
        } else if ("CONSUME".equals(user.getRole())) {
            user.setRole("CONSUMER");
        }
        
        // Auto-approve all users
        user.setApproved(true);
        
        return userRepository.save(user);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User updateUser(User user) {
        // Ensure role consistency before updating
        if ("FAWBER".equals(user.getRole())) {
            user.setRole("FARMER");
        } else if ("CONSUME".equals(user.getRole())) {
            user.setRole("CONSUMER");
        }
        
        return userRepository.save(user);
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
            throw e;
        } catch (Exception e) {
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
        return userRepository.count();
    }
    
    public long countPendingUsers() {
        return 0; // No pending users
    }
    
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    public long countApprovedUsers() {
        return userRepository.count(); // All users are approved
    }
    
    public List<User> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        users.forEach(user -> {
            if ("FAWBER".equals(user.getRole())) {
                user.setRole("FARMER");
            } else if ("CONSUME".equals(user.getRole())) {
                user.setRole("CONSUMER");
            }
        });
        return users;
    }
}