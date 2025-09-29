// DatabaseDebugController.java
package com.farmchainx.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DatabaseDebugController {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/connection-test")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("status", "SUCCESS");
            result.put("database", metaData.getDatabaseProductName());
            result.put("version", metaData.getDatabaseProductVersion());
            result.put("url", metaData.getURL());
            result.put("username", metaData.getUserName());
            result.put("driver", metaData.getDriverName());
            
            // Test basic query
            try {
                String testQuery = jdbcTemplate.queryForObject("SELECT 'Database connected successfully' AS test", String.class);
                result.put("queryTest", testQuery);
            } catch (Exception e) {
                result.put("queryTestError", e.getMessage());
            }
            
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            result.put("solution", "Check database credentials, user privileges, and if MySQL is running");
        }
        
        return result;
    }
    
    @GetMapping("/database-info")
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check if database exists
            Boolean dbExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) > 0 FROM information_schema.schemata WHERE schema_name = 'farmchainx_db'",
                Boolean.class
            );
            result.put("databaseExists", dbExists);
            
            if (dbExists) {
                // Check if user has access
                Boolean hasAccess = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) > 0 FROM information_schema.schema_privileges " +
                    "WHERE table_schema = 'farmchainx_db' AND grantee LIKE '%farmchainx_user%'",
                    Boolean.class
                );
                result.put("userHasAccess", hasAccess);
                
                // Check if users table exists
                Boolean tableExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) > 0 FROM information_schema.tables " +
                    "WHERE table_schema = 'farmchainx_db' AND table_name = 'users'",
                    Boolean.class
                );
                result.put("usersTableExists", tableExists);
                
                if (tableExists) {
                    // Count users
                    Integer userCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM farmchainx_db.users",
                        Integer.class
                    );
                    result.put("userCount", userCount);
                    
                    // Get sample users
                    var users = jdbcTemplate.queryForList(
                        "SELECT id, full_name, email, role, is_approved, created_at FROM farmchainx_db.users LIMIT 5"
                    );
                    result.put("sampleUsers", users);
                }
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/fix-database")
    public Map<String, Object> attemptFix() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Try to create database and grant privileges (will fail if user doesn't have permissions)
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS farmchainx_db");
            result.put("databaseCreated", true);
            
            jdbcTemplate.execute("USE farmchainx_db");
            result.put("useDatabase", true);
            
            // Try to create users table if it doesn't exist
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "full_name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(50) NOT NULL, " +
                "is_approved BOOLEAN DEFAULT FALSE, " +
                "farm_name VARCHAR(255), " +
                "company_name VARCHAR(255), " +
                "created_at DATETIME, " +
                "updated_at DATETIME" +
                ")"
            );
            result.put("tableCreated", true);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("solution", "Run the SQL commands manually as root user");
        }
        
        return result;
    }
}