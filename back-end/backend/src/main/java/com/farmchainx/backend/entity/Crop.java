package com.farmchainx.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crops")
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String soil;

    @Column(nullable = false)
    private String place;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name = "sowed_date", nullable = false)
    private LocalDate sowedDate;

    @Column(name = "harvest_period", nullable = false)
    private Integer harvestPeriod;

    @Column(name = "approx_harvest")
    private LocalDate approxHarvest;

    @Column(nullable = false)
    private String status = "Active";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Crop() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Crop(String name, String type, String soil, String place, LocalDate sowedDate, 
                Integer harvestPeriod, User user) {
        this();
        this.name = name;
        this.type = type;
        this.soil = soil;
        this.place = place;
        this.sowedDate = sowedDate;
        this.harvestPeriod = harvestPeriod;
        this.user = user;
        calculateHarvestDate();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSoil() { return soil; }
    public void setSoil(String soil) { this.soil = soil; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDate getSowedDate() { return sowedDate; }
    public void setSowedDate(LocalDate sowedDate) { 
        this.sowedDate = sowedDate;
        calculateHarvestDate();
    }

    public Integer getHarvestPeriod() { return harvestPeriod; }
    public void setHarvestPeriod(Integer harvestPeriod) { 
        this.harvestPeriod = harvestPeriod;
        calculateHarvestDate();
    }

    public LocalDate getApproxHarvest() { return approxHarvest; }
    public void setApproxHarvest(LocalDate approxHarvest) { this.approxHarvest = approxHarvest; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    private void calculateHarvestDate() {
        if (this.sowedDate != null && this.harvestPeriod != null) {
            this.approxHarvest = this.sowedDate.plusDays(this.harvestPeriod);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateHarvestDate();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateHarvestDate();
    }

    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", user=" + (user != null ? user.getEmail() : "null") +
                '}';
    }
}