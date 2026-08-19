package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.Review;
import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.service.CategoryService;
import com.datalcott.ecommerce.service.ProductService;
import com.datalcott.ecommerce.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             ReviewService reviewService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }


    // =========================================================
    // PRODUCTS PAGE
    // =========================================================

    @GetMapping("/products")
    public String getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model) {

        /*
         * No pagination here.
         *
         * We want all products to appear continuously:
         *
         * 4 products
         * 4 products
         * 4 products
         * 4 products
         * ...
         *
         * The HTML/CSS controls how many products
         * appear in each row.
         */


        // Remove empty keyword
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }


        // =====================================================
        // SEARCH + CATEGORY FILTER
        // =====================================================

        if (keyword != null && categoryId != null) {

            /*
             * If both keyword and category are selected,
             * first get products matching the keyword,
             * then filter them by category.
             *
             * This avoids pagination and keeps everything
             * on one continuous page.
             */

            model.addAttribute(
                    "products",
                    productService.searchProducts(keyword)
                            .stream()
                            .filter(product ->
                                    product.getCategory() != null
                                            && product.getCategory().getId().equals(categoryId)
                            )
                            .toList()
            );

        }

        // =====================================================
        // SEARCH ONLY
        // =====================================================

        else if (keyword != null) {

            model.addAttribute(
                    "products",
                    productService.searchProducts(keyword)
            );

        }

        // =====================================================
        // CATEGORY ONLY
        // =====================================================

        else if (categoryId != null) {

            model.addAttribute(
                    "products",
                    productService.getProductsByCategory(categoryId)
            );

        }

        // =====================================================
        // ALL PRODUCTS
        // =====================================================

        else {

            model.addAttribute(
                    "products",
                    productService.getAllProducts()
            );

        }


        // =====================================================
        // SEARCH VALUES
        // =====================================================

        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "categoryId",
                categoryId
        );


        // =====================================================
        // CATEGORIES FOR DROPDOWN
        // =====================================================

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );


        return "products";
    }


    // =========================================================
    // NEW PRODUCT
    // =========================================================

    @GetMapping("/products/new")
    public String showProductForm(Model model) {

        model.addAttribute(
                "product",
                new Product()
        );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "product-form";
    }


    // =========================================================
    // SAVE PRODUCT
    // =========================================================

    @PostMapping("/products/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "categories",
                    categoryService.getAllCategories()
            );

            return "product-form";
        }

        productService.saveProduct(product);

        return "redirect:/products";
    }


    // =========================================================
    // PRODUCT DETAILS
    // =========================================================

    @GetMapping("/products/{id}")
    public String getProductById(
            @PathVariable Long id,
            Model model) {

        Product product =
                productService.getProductById(id);

        model.addAttribute(
                "product",
                product
        );

        model.addAttribute(
                "reviews",
                reviewService.getProductReviews(product)
        );

        model.addAttribute(
                "review",
                new Review()
        );

        return "product-details";
    }


    // =========================================================
    // ADMIN PRODUCTS
    // =========================================================

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "admin-products";
    }


    // =========================================================
    // EDIT PRODUCT
    // =========================================================

    @GetMapping("/admin/products/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProductById(id)
        );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "product-form";
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return "redirect:/admin/products";
    }

}