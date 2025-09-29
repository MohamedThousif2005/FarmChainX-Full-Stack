package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    
    List<Crop> findByUser(User user);
    
    @Query("SELECT c FROM Crop c WHERE c.user.id = :userId AND c.status = :status")
    List<Crop> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    Optional<Crop> findByIdAndUserId(Long id, Long userId);
    
    Long countByUser(User user);
    
    @Query("SELECT COUNT(c) FROM Crop c WHERE c.user.id = :userId AND c.status = 'Active'")
    Long countActiveCropsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Crop c WHERE c.user.id = :userId AND c.status = 'Harvested'")
    Long countHarvestedCropsByUserId(@Param("userId") Long userId);
}