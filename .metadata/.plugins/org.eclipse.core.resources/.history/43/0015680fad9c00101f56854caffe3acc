package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.OrderDTO;
import com.farmchainx.backend.dto.OrderRequestDTO;
import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/consumer/{consumerId}")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest,
                                       @PathVariable Long consumerId) {
        try {
            Order order = orderService.createOrder(orderRequest, consumerId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/consumer/{consumerId}")
    public ResponseEntity<List<OrderDTO>> getConsumerOrders(@PathVariable Long consumerId) {
        List<OrderDTO> orders = orderService.getOrdersByConsumer(consumerId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/distributor/{distributorId}")
    public ResponseEntity<List<OrderDTO>> getDistributorOrders(@PathVariable Long distributorId) {
        List<OrderDTO> orders = orderService.getOrdersByDistributor(distributorId);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, 
                                             @RequestParam String status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}