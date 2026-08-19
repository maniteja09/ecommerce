package com.datalcott.ecommerce.repository;

import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
