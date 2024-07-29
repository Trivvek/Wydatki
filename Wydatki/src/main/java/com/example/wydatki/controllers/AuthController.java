package com.example.wydatki.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.example.wydatki.user.UserRepository;
import com.example.wydatki.user.User;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/auth/login?logout";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, @RequestParam String confirmPassword) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            model.addAttribute("usernameError", "Username must be between 3 and 20 characters");
            return "register";
        }
        if (user.getPassword().length() < 8) {
            model.addAttribute("passwordError", "Password must be at least 8 characters");
            return "register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Passwords do not match");
            return "register";
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameError", "Username already exists");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
