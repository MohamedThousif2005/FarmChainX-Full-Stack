package com.farmchainx.backend.dto;

public class OrderItemRequestDTO {
    private Long productId;
    private Integer quantity;
    
    // Constructors
    public OrderItemRequestDTO() {}
    
    public OrderItemRequestDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}