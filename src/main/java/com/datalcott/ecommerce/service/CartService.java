package com.datalcott.ecommerce.service;

import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.CartItemRepository;
import com.datalcott.ecommerce.repository.CartRepository;
import com.datalcott.ecommerce.repository.ProductRepository;
import com.datalcott.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }


    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }


    public Cart getCartById(Long id) {
        return cartRepository.findById(id).orElse(null);
    }


    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }


    // =========================
    // GET OR CREATE USER CART
    // =========================

    public Cart getOrCreateCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }


    // =========================
    // ADD TO CART
    // =========================

    public void addToCart(
            String email,
            Long productId,
            int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than 0");
        }

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (quantity > product.getStock()) {
            throw new RuntimeException(
                    "Not enough stock available");
        }

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository
                        .findByCartAndProduct(cart, product)
                        .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Not enough stock available");
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }


    // =========================
    // GET CART ITEMS
    // =========================

    public List<CartItem> getCartItems(Long cartId) {

        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        return cartItemRepository.findByCart(cart);
    }


    public List<CartItem> getCartItemsByEmail(String email) {

        Cart cart = getOrCreateCart(email);

        return cartItemRepository.findByCart(cart);
    }


    // =========================
    // REMOVE FROM CART
    // =========================

    public void removeFromCart(
            String email,
            Long cartItemId) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository
                        .findById(cartItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart item not found"));

        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {

            throw new RuntimeException(
                    "You cannot remove another user's cart item");
        }

        cartItemRepository.delete(cartItem);
    }


    // =========================
    // UPDATE QUANTITY
    // =========================

    public void updateQuantity(
            String email,
            Long cartItemId,
            int quantity) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository
                        .findById(cartItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart item not found"));

        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {

            throw new RuntimeException(
                    "You cannot update another user's cart item");
        }

        if (quantity <= 0) {

            cartItemRepository.delete(cartItem);
            return;
        }

        if (quantity >
                cartItem.getProduct().getStock()) {

            throw new RuntimeException(
                    "Not enough stock available");
        }

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
    }


    // =========================
    // CLEAR CART
    // =========================

    public void clearCart(Long cartId) {

        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        cartItemRepository.deleteAll(cartItems);
    }


    public void clearCartByEmail(String email) {

        Cart cart = getOrCreateCart(email);

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        cartItemRepository.deleteAll(cartItems);
    }
}