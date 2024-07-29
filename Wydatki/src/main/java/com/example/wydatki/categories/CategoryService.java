package com.example.wydatki.categories;

import com.example.wydatki.expenses.ExpenseRepository;
import com.example.wydatki.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findByUser(user);
    }

    public Category createCategory(String name, User user) {
        Category existingCategory = categoryRepository.findByNameAndUser(name, user);
        if (existingCategory != null) {
            throw new IllegalArgumentException("Category with this name already exists for the user");
        }
        Category newCategory = new Category(name, user);
        System.out.println("Creating new category with name: " + name + " for user id: " + user.getId());
        return categoryRepository.save(newCategory);
    }

    public boolean deleteCategory(Long id, User user) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (category.getUser().getId().equals(user.getId())) {
                if (expenseRepository.existsByCategory(category)) {
                    return false;
                }
                categoryRepository.delete(category);
                return true;
            }
        }
        return false;
    }

    public Category updateCategory(Long id, String newName, User user) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        System.out.println("Trying to update category with id: " + id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            System.out.println("Category found. Category user id: " + category.getUser().getId() + ", Current user id: " + user.getId());
            if (category.getUser().getId().equals(user.getId())) {
                category.setName(newName);
                return categoryRepository.save(category);
            }
        }
        return null;
    }
}