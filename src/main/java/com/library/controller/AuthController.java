package com.library.controller;

import com.library.entity.User;
import com.library.entity.UserRole;
import com.library.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String studentCode = authentication.getName();
        User user = userRepository.findByStudentCode(studentCode).orElse(null);
        if (user == null) return "redirect:/login";

        // Admin quản lý người dùng/phân quyền; Thủ thư vận hành (dashboard)
        if (user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/users";
        } else if (user.getRole() == UserRole.LIBRARIAN) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/client/home";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}
