package com.example.wydatki.controllers;

import com.example.wydatki.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.wydatki.categories.CategoryService;
import com.example.wydatki.categories.Category;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(@AuthenticationPrincipal User user, Model model) {
        System.out.println("Current user id: " + user.getId() + ", username: " + user.getUsername());
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("newCategory", new Category());
        return "categories/list";
    }

    @PostMapping
    public String createCategory(@AuthenticationPrincipal User user, @ModelAttribute("newCategory") Category newCategory, RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(newCategory.getName(), user);
            redirectAttributes.addFlashAttribute("message", "Category created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@AuthenticationPrincipal User user, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (categoryService.deleteCategory(id, user)) {
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to delete category. It may not exist, you may not have permission, or there are expenses associated with it.");
        }
        return "redirect:/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        Category updatedCategory = categoryService.updateCategory(id, name, user);
        if (updatedCategory != null) {
            redirectAttributes.addFlashAttribute("message", "Category updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update category. It may not exist or you may not have permission.");
        }
        return "redirect:/categories";
    }
}