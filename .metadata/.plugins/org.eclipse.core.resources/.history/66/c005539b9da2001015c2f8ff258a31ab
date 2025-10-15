package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.CropDTO;
import com.farmchainx.backend.dto.DashboardStatsDTO;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CropDTO> getCropsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return cropRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CropDTO> getCropsByUserIdAndStatus(Long userId, String status) {
        return cropRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CropDTO addCrop(Long userId, CropDTO cropDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Crop crop = convertToEntity(cropDTO);
        crop.setUser(user);
        
        // Set default status if not provided
        if (crop.getStatus() == null) {
            crop.setStatus("Active");
        }
        
        Crop savedCrop = cropRepository.save(crop);
        return convertToDTO(savedCrop);
    }

    public CropDTO updateCropStatus(Long cropId, String status, Long userId) {
        Crop crop = cropRepository.findByIdAndUserId(cropId, userId)
                .orElseThrow(() -> new RuntimeException("Crop not found or access denied"));

        crop.setStatus(status);
        Crop updatedCrop = cropRepository.save(crop);
        return convertToDTO(updatedCrop);
    }

    public void deleteCrop(Long cropId, Long userId) {
        Crop crop = cropRepository.findByIdAndUserId(cropId, userId)
                .orElseThrow(() -> new RuntimeException("Crop not found or access denied"));

        cropRepository.delete(crop);
    }

    public CropDTO getCropById(Long cropId, Long userId) {
        Crop crop = cropRepository.findByIdAndUserId(cropId, userId)
                .orElseThrow(() -> new RuntimeException("Crop not found or access denied"));

        return convertToDTO(crop);
    }

    public DashboardStatsDTO getDashboardStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get basic counts from repository
        Long totalCrops = cropRepository.countByUser(user);
        Long activeCrops = cropRepository.countActiveCropsByUserId(userId);
        Long harvestedCrops = cropRepository.countHarvestedCropsByUserId(userId);
        
        // Calculate upcoming harvests manually in Java
        Long upcomingHarvests = calculateUpcomingHarvests(userId);

        return new DashboardStatsDTO(totalCrops, activeCrops, harvestedCrops, upcomingHarvests);
    }

    private Long calculateUpcomingHarvests(Long userId) {
        // Get all active crops for the user
        List<Crop> activeCrops = cropRepository.findByUserIdAndStatus(userId, "Active");
        
        // Filter crops with harvest dates in the next 7 days
        return activeCrops.stream()
                .filter(crop -> crop.getApproxHarvest() != null)
                .filter(crop -> {
                    LocalDate harvestDate = crop.getApproxHarvest();
                    LocalDate today = LocalDate.now();
                    LocalDate nextWeek = today.plusDays(7);
                    
                    // Check if harvest date is between today and next week (inclusive)
                    return (harvestDate.isEqual(today) || harvestDate.isAfter(today)) 
                           && (harvestDate.isEqual(nextWeek) || harvestDate.isBefore(nextWeek));
                })
                .count();
    }

    private CropDTO convertToDTO(Crop crop) {
        CropDTO dto = new CropDTO();
        dto.setId(crop.getId());
        dto.setName(crop.getName());
        dto.setType(crop.getType());
        dto.setSoil(crop.getSoil());
        dto.setPlace(crop.getPlace());
        dto.setComments(crop.getComments());
        dto.setImage(crop.getImage());
        dto.setSowedDate(crop.getSowedDate());
        dto.setHarvestPeriod(crop.getHarvestPeriod());
        dto.setApproxHarvest(crop.getApproxHarvest());
        dto.setStatus(crop.getStatus());
        return dto;
    }

    private Crop convertToEntity(CropDTO dto) {
        Crop crop = new Crop();
        crop.setName(dto.getName());
        crop.setType(dto.getType());
        crop.setSoil(dto.getSoil());
        crop.setPlace(dto.getPlace());
        crop.setComments(dto.getComments());
        crop.setImage(dto.getImage());
        crop.setSowedDate(dto.getSowedDate());
        crop.setHarvestPeriod(dto.getHarvestPeriod());
        crop.setStatus(dto.getStatus() != null ? dto.getStatus() : "Active");
        return crop;
    }
}