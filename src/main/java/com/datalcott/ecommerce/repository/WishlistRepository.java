package com.datalcott.ecommerce.repository;

import com.datalcott.ecommerce.entity.WishlistItem;
import com.datalcott.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository
        extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUser(User user);

    Optional<WishlistItem> findByUserAndProductId(
            User user,
            Long productId
    );
}
