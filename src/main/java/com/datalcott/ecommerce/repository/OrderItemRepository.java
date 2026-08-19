package com.datalcott.ecommerce.repository;

import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}