package com.datalcott.ecommerce;

import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.repository.ProductRepository;
import com.datalcott.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(50000);
        product.setStock(10);
        product.setImageUrl("laptop.jpg");
    }

    @Test
    void saveProduct_shouldSaveProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(50000, result.getPrice());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mobile");
        product2.setPrice(20000);

        List<Product> products = Arrays.asList(product, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mobile", result.get(1).getName());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.getProductById(99L)
        );

        assertEquals(
                "Product not found with ID: 99",
                exception.getMessage()
        );

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchProducts_shouldReturnMatchingProducts() {
        List<Product> products = List.of(product);

        when(productRepository.findByNameContainingIgnoreCase("lap"))
                .thenReturn(products);

        List<Product> result = productService.searchProducts("lap");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        verify(productRepository, times(1))
                .findByNameContainingIgnoreCase("lap");
    }

    @Test
    void getProductsByCategory_shouldReturnProducts() {
        List<Product> products = List.of(product);

        when(productRepository.findByCategoryId(1L))
                .thenReturn(products);

        List<Product> result = productService.getProductsByCategory(1L);

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        verify(productRepository, times(1))
                .findByCategoryId(1L);
    }

    @Test
    void getProducts_shouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchProductsWithPagination_shouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByNameContainingIgnoreCase(
                "lap",
                pageable
        )).thenReturn(page);

        Page<Product> result =
                productService.searchProducts("lap", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository, times(1))
                .findByNameContainingIgnoreCase("lap", pageable);
    }

    @Test
    void getProductsByCategoryWithPagination_shouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByCategoryId(
                1L,
                pageable
        )).thenReturn(page);

        Page<Product> result =
                productService.getProductsByCategory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository, times(1))
                .findByCategoryId(1L, pageable);
    }
}