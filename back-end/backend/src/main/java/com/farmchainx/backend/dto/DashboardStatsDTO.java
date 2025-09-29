package com.farmchainx.backend.dto;

public class DashboardStatsDTO {
    private Long totalCrops;
    private Long activeCrops;
    private Long harvestedCrops;
    private Long upcomingHarvests;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(Long totalCrops, Long activeCrops, Long harvestedCrops, Long upcomingHarvests) {
        this.totalCrops = totalCrops;
        this.activeCrops = activeCrops;
        this.harvestedCrops = harvestedCrops;
        this.upcomingHarvests = upcomingHarvests;
    }

    // Getters and Setters
    public Long getTotalCrops() { return totalCrops; }
    public void setTotalCrops(Long totalCrops) { this.totalCrops = totalCrops; }

    public Long getActiveCrops() { return activeCrops; }
    public void setActiveCrops(Long activeCrops) { this.activeCrops = activeCrops; }

    public Long getHarvestedCrops() { return harvestedCrops; }
    public void setHarvestedCrops(Long harvestedCrops) { this.harvestedCrops = harvestedCrops; }

    public Long getUpcomingHarvests() { return upcomingHarvests; }
    public void setUpcomingHarvests(Long upcomingHarvests) { this.upcomingHarvests = upcomingHarvests; }
}