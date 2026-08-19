package com.datalcott.ecommerce;

import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.CartItemRepository;
import com.datalcott.ecommerce.repository.CartRepository;
import com.datalcott.ecommerce.repository.ProductRepository;
import com.datalcott.ecommerce.repository.UserRepository;
import com.datalcott.ecommerce.service.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;


    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;


    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(50000);
        product.setStock(10);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }


    // =========================================================
    // 1. SAVE CART
    // =========================================================

    @Test
    void saveCart_shouldReturnSavedCart() {

        when(cartRepository.save(cart))
                .thenReturn(cart);

        Cart result = cartService.saveCart(cart);

        assertNotNull(result);
        assertEquals(cart, result);

        verify(cartRepository)
                .save(cart);
    }


    // =========================================================
    // 2. GET ALL CARTS
    // =========================================================

    @Test
    void getAllCarts_shouldReturnAllCarts() {

        Cart cart2 = new Cart();
        cart2.setId(2L);

        List<Cart> carts = List.of(cart, cart2);

        when(cartRepository.findAll())
                .thenReturn(carts);

        List<Cart> result =
                cartService.getAllCarts();

        assertEquals(2, result.size());
        assertEquals(carts, result);

        verify(cartRepository)
                .findAll();
    }


    // =========================================================
    // 3. GET CART BY ID
    // =========================================================

    @Test
    void getCartById_shouldReturnCart() {

        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        Cart result =
                cartService.getCartById(1L);

        assertNotNull(result);
        assertEquals(cart, result);

        verify(cartRepository)
                .findById(1L);
    }


    // =========================================================
    // 4. GET CART BY ID - NOT FOUND
    // =========================================================

    @Test
    void getCartById_shouldReturnNullWhenNotFound() {

        when(cartRepository.findById(99L))
                .thenReturn(Optional.empty());

        Cart result =
                cartService.getCartById(99L);

        assertNull(result);

        verify(cartRepository)
                .findById(99L);
    }


    // =========================================================
    // 5. DELETE CART
    // =========================================================

    @Test
    void deleteCart_shouldDeleteCart() {

        cartService.deleteCart(1L);

        verify(cartRepository)
                .deleteById(1L);
    }


    // =========================================================
    // 6. GET OR CREATE CART - EXISTING
    // =========================================================

    @Test
    void getOrCreateCart_shouldReturnExistingCart() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        Cart result =
                cartService.getOrCreateCart("test@example.com");

        assertNotNull(result);
        assertEquals(cart, result);

        verify(userRepository)
                .findByEmail("test@example.com");

        verify(cartRepository)
                .findByUser(user);

        verify(cartRepository, never())
                .save(any(Cart.class));
    }


    // =========================================================
    // 7. GET OR CREATE CART - NEW
    // =========================================================

    @Test
    void getOrCreateCart_shouldCreateNewCart() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);

        Cart result =
                cartService.getOrCreateCart("test@example.com");

        assertNotNull(result);
        assertEquals(cart, result);

        verify(cartRepository)
                .save(any(Cart.class));
    }


    // =========================================================
    // 8. GET OR CREATE CART - USER NOT FOUND
    // =========================================================

    @Test
    void getOrCreateCart_shouldThrowWhenUserNotFound() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.getOrCreateCart(
                                "unknown@example.com"
                        )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(cartRepository, never())
                .findByUser(any(User.class));
    }


    // =========================================================
    // 9. ADD TO CART - NEW ITEM
    // =========================================================

    @Test
    void addToCart_shouldAddNewItem() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository
                .findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());

        cartService.addToCart(
                "test@example.com",
                1L,
                2
        );

        verify(cartItemRepository)
                .save(any(CartItem.class));
    }


    // =========================================================
    // 10. ADD TO CART - EXISTING ITEM
    // =========================================================

    @Test
    void addToCart_shouldIncreaseExistingItemQuantity() {

        cartItem.setQuantity(2);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository
                .findByCartAndProduct(cart, product))
                .thenReturn(Optional.of(cartItem));

        cartService.addToCart(
                "test@example.com",
                1L,
                3
        );

        assertEquals(
                5,
                cartItem.getQuantity()
        );

        verify(cartItemRepository)
                .save(cartItem);
    }


    // =========================================================
    // 11. ADD TO CART - INVALID QUANTITY
    // =========================================================

    @Test
    void addToCart_shouldThrowForInvalidQuantity() {

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.addToCart(
                                "test@example.com",
                                1L,
                                0
                        )
                );

        assertEquals(
                "Quantity must be greater than 0",
                exception.getMessage()
        );

        verifyNoInteractions(
                productRepository,
                userRepository,
                cartRepository,
                cartItemRepository
        );
    }


    // =========================================================
    // 12. ADD TO CART - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void addToCart_shouldThrowWhenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.addToCart(
                                "test@example.com",
                                99L,
                                1
                        )
                );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );
    }


    // =========================================================
    // 13. ADD TO CART - INSUFFICIENT STOCK
    // =========================================================

    @Test
    void addToCart_shouldThrowWhenStockIsInsufficient() {

        product.setStock(2);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.addToCart(
                                "test@example.com",
                                1L,
                                5
                        )
                );

        assertEquals(
                "Not enough stock available",
                exception.getMessage()
        );
    }


    // =========================================================
    // 14. GET CART ITEMS
    // =========================================================

    @Test
    void getCartItems_shouldReturnItems() {

        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        List<CartItem> result =
                cartService.getCartItems(1L);

        assertEquals(1, result.size());
        assertEquals(cartItem, result.get(0));

        verify(cartItemRepository)
                .findByCart(cart);
    }


    // =========================================================
    // 15. GET CART ITEMS - CART NOT FOUND
    // =========================================================

    @Test
    void getCartItems_shouldThrowWhenCartNotFound() {

        when(cartRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.getCartItems(99L)
                );

        assertEquals(
                "Cart not found",
                exception.getMessage()
        );
    }


    // =========================================================
    // 16. GET CART ITEMS BY EMAIL
    // =========================================================

    @Test
    void getCartItemsByEmail_shouldReturnItems() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        List<CartItem> result =
                cartService.getCartItemsByEmail(
                        "test@example.com"
                );

        assertEquals(1, result.size());
        assertEquals(cartItem, result.get(0));
    }


    // =========================================================
    // 17. REMOVE FROM CART
    // =========================================================

    @Test
    void removeFromCart_shouldRemoveItem() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        cartService.removeFromCart(
                "test@example.com",
                1L
        );

        verify(cartItemRepository)
                .delete(cartItem);
    }


    // =========================================================
    // 18. REMOVE FROM CART - WRONG USER
    // =========================================================

    @Test
    void removeFromCart_shouldRejectAnotherUsersItem() {

        Cart anotherCart = new Cart();
        anotherCart.setId(99L);

        cartItem.setCart(anotherCart);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.removeFromCart(
                                "test@example.com",
                                1L
                        )
                );

        assertEquals(
                "You cannot remove another user's cart item",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));
    }


    // =========================================================
    // 19. UPDATE QUANTITY
    // =========================================================

    @Test
    void updateQuantity_shouldUpdateQuantity() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        cartService.updateQuantity(
                "test@example.com",
                1L,
                5
        );

        assertEquals(
                5,
                cartItem.getQuantity()
        );

        verify(cartItemRepository)
                .save(cartItem);
    }


    // =========================================================
    // 20. UPDATE QUANTITY - ZERO
    // =========================================================

    @Test
    void updateQuantity_shouldDeleteWhenQuantityIsZero() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        cartService.updateQuantity(
                "test@example.com",
                1L,
                0
        );

        verify(cartItemRepository)
                .delete(cartItem);

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }


    // =========================================================
    // 21. UPDATE QUANTITY - INSUFFICIENT STOCK
    // =========================================================

    @Test
    void updateQuantity_shouldThrowWhenStockIsInsufficient() {

        product.setStock(3);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.updateQuantity(
                                "test@example.com",
                                1L,
                                10
                        )
                );

        assertEquals(
                "Not enough stock available",
                exception.getMessage()
        );
    }


    // =========================================================
    // 22. CLEAR CART
    // =========================================================

    @Test
    void clearCart_shouldDeleteAllItems() {

        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        cartService.clearCart(1L);

        verify(cartItemRepository)
                .deleteAll(List.of(cartItem));
    }


    // =========================================================
    // 23. CLEAR CART - NOT FOUND
    // =========================================================

    @Test
    void clearCart_shouldThrowWhenCartNotFound() {

        when(cartRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> cartService.clearCart(99L)
                );

        assertEquals(
                "Cart not found",
                exception.getMessage()
        );
    }


    // =========================================================
    // 24. CLEAR CART BY EMAIL
    // =========================================================

    @Test
    void clearCartByEmail_shouldDeleteAllItems() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        cartService.clearCartByEmail(
                "test@example.com"
        );

        verify(cartItemRepository)
                .deleteAll(List.of(cartItem));
    }
}