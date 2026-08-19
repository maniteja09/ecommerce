package com.datalcott.ecommerce.repository;

import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}