package com.datalcott.ecommerce;

import com.datalcott.ecommerce.controller.ProductController;
import com.datalcott.ecommerce.entity.Category;
import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.Review;
import com.datalcott.ecommerce.service.CategoryService;
import com.datalcott.ecommerce.service.ProductService;
import com.datalcott.ecommerce.service.ReviewService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ReviewService reviewService;


    // ---------------------------------------------------------
    // GET /products
    // ---------------------------------------------------------

    @Test
    void getAllProducts_shouldReturnProductsPage() throws Exception {

        Product product = createProduct();

        when(productService.getAllProducts())
                .thenReturn(List.of(product));

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("categories"));

        verify(productService).getAllProducts();
        verify(categoryService).getAllCategories();
    }


    // ---------------------------------------------------------
    // GET /products?keyword=...
    // ---------------------------------------------------------

    @Test
    void searchProducts_shouldReturnSearchResults() throws Exception {

        Product product = createProduct();

        when(productService.searchProducts("Laptop"))
                .thenReturn(List.of(product));

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/products")
                                .param("keyword", "Laptop")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("keyword", "Laptop"));

        verify(productService).searchProducts("Laptop");
    }


    // ---------------------------------------------------------
    // GET /products?categoryId=1
    // ---------------------------------------------------------

    @Test
    void categoryFilter_shouldReturnCategoryProducts() throws Exception {

        Product product = createProduct();

        when(productService.getProductsByCategory(1L))
                .thenReturn(List.of(product));

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/products")
                                .param("categoryId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("categoryId", 1L));

        verify(productService).getProductsByCategory(1L);
    }


    // ---------------------------------------------------------
    // GET /products?keyword=...&categoryId=...
    // ---------------------------------------------------------

    @Test
    void searchAndCategoryFilter_shouldReturnFilteredProducts() throws Exception {

        Product product = createProduct();

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Electronics");

        product.setCategory(category);

        when(productService.searchProducts("Laptop"))
                .thenReturn(List.of(product));

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/products")
                                .param("keyword", "Laptop")
                                .param("categoryId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"));

        verify(productService).searchProducts("Laptop");
    }


    // ---------------------------------------------------------
    // GET /products/new
    // ---------------------------------------------------------

    @Test
    void showProductForm_shouldReturnProductForm() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("categories"));

        verify(categoryService).getAllCategories();
    }


    // ---------------------------------------------------------
    // POST /products/save - VALID
    // ---------------------------------------------------------

    @Test
    void saveProduct_shouldRedirectWhenValid() throws Exception {

        mockMvc.perform(
                        post("/products/save")
                                .param("name", "Laptop")
                                .param("description", "Gaming Laptop")
                                .param("price", "50000")
                                .param("stock", "10")
                                .param("imageUrl", "laptop.jpg")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).saveProduct(any(Product.class));
    }


    // ---------------------------------------------------------
    // POST /products/save - INVALID
    // ---------------------------------------------------------

    @Test
    void saveProduct_shouldReturnFormWhenValidationFails() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(
                        post("/products/save")
                                .param("name", "")
                                .param("description", "")
                                .param("price", "-100")
                                .param("stock", "-5")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeExists("categories"));

        verify(productService, never()).saveProduct(any(Product.class));
    }


    // ---------------------------------------------------------
    // GET /products/{id}
    // ---------------------------------------------------------

    @Test
    void getProductById_shouldReturnProductDetails() throws Exception {

        Product product = createProduct();

        when(productService.getProductById(1L))
                .thenReturn(product);

        when(reviewService.getProductReviews(product))
                .thenReturn(List.of());

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("review"));

        verify(productService).getProductById(1L);
        verify(reviewService).getProductReviews(product);
    }


    // ---------------------------------------------------------
    // GET /admin/products
    // ---------------------------------------------------------

    @Test
    void adminProducts_shouldReturnAdminProductsPage() throws Exception {

        Product product = createProduct();

        when(productService.getAllProducts())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-products"))
                .andExpect(model().attributeExists("products"));

        verify(productService).getAllProducts();
    }


    // ---------------------------------------------------------
    // GET /admin/products/edit/{id}
    // ---------------------------------------------------------

    @Test
    void editProduct_shouldReturnProductForm() throws Exception {

        Product product = createProduct();

        when(productService.getProductById(1L))
                .thenReturn(product);

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/products/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("categories"));

        verify(productService).getProductById(1L);
        verify(categoryService).getAllCategories();
    }


    // ---------------------------------------------------------
    // GET /admin/products/delete/{id}
    // ---------------------------------------------------------

    @Test
    void deleteProduct_shouldRedirectToAdminProducts() throws Exception {

        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(get("/admin/products/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).deleteProduct(1L);
    }


    // ---------------------------------------------------------
    // Helper method
    // ---------------------------------------------------------

    private Product createProduct() {

        Product product = new Product();

        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(50000);
        product.setStock(10);
        product.setImageUrl("laptop.jpg");

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Electronics");

        product.setCategory(category);

        return product;
    }
}