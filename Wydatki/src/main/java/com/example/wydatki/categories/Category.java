package com.example.wydatki.categories;

import jakarta.persistence.*;
import javax.validation.constraints.NotEmpty;
import com.example.wydatki.user.User;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Category name cannot be empty")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Category() {}

    public Category(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
