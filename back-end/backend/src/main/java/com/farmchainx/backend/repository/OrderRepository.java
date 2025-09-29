package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByConsumer(User consumer);
    
    List<Order> findByDistributor(User distributor);
}