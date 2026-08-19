package com.datalcott.ecommerce;

import org.springframework.web.servlet.view.RedirectView;
import com.datalcott.ecommerce.controller.CategoryController;
import com.datalcott.ecommerce.entity.Category;
import com.datalcott.ecommerce.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setViewResolvers((viewName, locale) -> {

                    if (viewName.startsWith("redirect:")) {

                        return new RedirectView(
                                viewName.substring("redirect:".length())
                        );
                    }

                    return (model, request, response) -> {
                    };
                })
                .build();
    }


    // =========================================================
    // TEST 1 - GET ALL CATEGORIES
    // =========================================================

    @Test
    void getAllCategories_shouldReturnCategoriesPage() throws Exception {

        Category category = new Category();

        when(categoryService.getAllCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/categories"))

                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"));

        verify(categoryService)
                .getAllCategories();
    }


    // =========================================================
    // TEST 2 - SHOW CATEGORY FORM
    // =========================================================

    @Test
    void showCategoryForm_shouldReturnCategoryForm() throws Exception {

        mockMvc.perform(get("/categories/new"))

                .andExpect(status().isOk())
                .andExpect(view().name("category-form"))
                .andExpect(model().attributeExists("category"));
    }


    // =========================================================
    // TEST 3 - SAVE CATEGORY
    // =========================================================

    @Test
    void saveCategory_shouldRedirectToAdminCategories() throws Exception {

        Category category = new Category();

        mockMvc.perform(
                        post("/categories/save")
                                .flashAttr("category", category)
                )

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService)
                .saveCategory(any(Category.class));
    }


    // =========================================================
    // TEST 4 - ADMIN CATEGORIES
    // =========================================================

    @Test
    void adminCategories_shouldReturnAdminCategoriesPage() throws Exception {

        Category category = new Category();

        when(categoryService.getAllCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/admin/categories"))

                .andExpect(status().isOk())
                .andExpect(view().name("admin-categories"))
                .andExpect(model().attributeExists("categories"));

        verify(categoryService)
                .getAllCategories();
    }


    // =========================================================
    // TEST 5 - EDIT CATEGORY
    // =========================================================

    @Test
    void editCategory_shouldReturnCategoryForm() throws Exception {

        Category category = new Category();

        when(categoryService.getCategoryById(1L))
                .thenReturn(category);

        mockMvc.perform(get("/admin/categories/edit/1"))

                .andExpect(status().isOk())
                .andExpect(view().name("category-form"))
                .andExpect(model().attributeExists("category"));

        verify(categoryService)
                .getCategoryById(1L);
    }


    // =========================================================
    // TEST 6 - DELETE CATEGORY
    // =========================================================

    @Test
    void deleteCategory_shouldRedirectToAdminCategories() throws Exception {

        mockMvc.perform(get("/admin/categories/delete/1"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService)
                .deleteCategory(1L);
    }
}