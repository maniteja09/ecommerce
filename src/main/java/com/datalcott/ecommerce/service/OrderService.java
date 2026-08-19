package com.datalcott.ecommerce.service;

import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }


    // Admin: get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    // User: get only their orders
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }


    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElse(null);
    }


    public Order getOrderByIdForUser(Long id, User user) {

        Order order = orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to view this order"
            );
        }

        return order;
    }


    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }


    public void updateOrderStatus(Long id,
                                  String status) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"));

        order.setStatus(status);

        orderRepository.save(order);
    }
}