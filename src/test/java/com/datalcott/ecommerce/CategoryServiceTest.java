package com.datalcott.ecommerce;

import com.datalcott.ecommerce.entity.Category;
import com.datalcott.ecommerce.repository.CategoryRepository;
import com.datalcott.ecommerce.service.CategoryService;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;


    // =========================================================
    // TEST 1 - SAVE CATEGORY
    // =========================================================

    @Test
    void saveCategory_shouldReturnSavedCategory() {

        Category category = new Category();

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category result =
                categoryService.saveCategory(category);

        assertNotNull(result);
        assertEquals(category, result);

        verify(categoryRepository)
                .save(category);
    }


    // =========================================================
    // TEST 2 - GET ALL CATEGORIES
    // =========================================================

    @Test
    void getAllCategories_shouldReturnAllCategories() {

        Category category1 = new Category();
        Category category2 = new Category();

        List<Category> categories =
                List.of(category1, category2);

        when(categoryRepository.findAll())
                .thenReturn(categories);

        List<Category> result =
                categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categories, result);

        verify(categoryRepository)
                .findAll();
    }


    // =========================================================
    // TEST 3 - GET CATEGORY BY ID
    // =========================================================

    @Test
    void getCategoryById_shouldReturnCategory() {

        Category category = new Category();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result =
                categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(category, result);

        verify(categoryRepository)
                .findById(1L);
    }


    // =========================================================
    // TEST 4 - DELETE CATEGORY
    // =========================================================

    @Test
    void deleteCategory_shouldDeleteCategory() {

        doNothing()
                .when(categoryRepository)
                .deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository)
                .deleteById(1L);
    }
}