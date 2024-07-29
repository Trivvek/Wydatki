package com.example.wydatki.categories;

import com.example.wydatki.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    Category findByNameAndUser(String name, User user);
}
