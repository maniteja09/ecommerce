package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        user.setRole("USER");

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        userService.saveUser(user);

        return "redirect:/login";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "users";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin-users";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication,
                          Model model) {

        String email = authentication.getName();

        User user = userService
                .getUserByEmail(email)
                .orElse(null);

        model.addAttribute("user", user);

        return "profile";
    }
}