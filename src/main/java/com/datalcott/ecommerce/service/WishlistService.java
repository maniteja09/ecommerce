package com.datalcott.ecommerce.service;

import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.entity.WishlistItem;
import com.datalcott.ecommerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    // Add product to wishlist
    public void addToWishlist(User user, Product product) {

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Prevent duplicate product
        if (wishlistRepository
                .findByUserAndProductId(user, product.getId())
                .isPresent()) {

            return;
        }

        WishlistItem item = new WishlistItem();

        item.setUser(user);
        item.setProduct(product);

        wishlistRepository.save(item);
    }

    // Get logged-in user's wishlist
    public List<WishlistItem> getWishlist(User user) {

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return wishlistRepository.findByUser(user);
    }

    // Remove wishlist item
    public void removeFromWishlist(Long id, User user) {

        WishlistItem item = wishlistRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Wishlist item not found"));

        // Make sure the item belongs to this user
        if (!item.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to remove this item");
        }

        wishlistRepository.delete(item);
    }
}