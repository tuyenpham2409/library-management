package com.library.controller;

import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Trung tâm thông báo in-app, dùng chung cho mọi vai trò.
 */
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService,
                                  UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByStudentCode(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/notifications")
    public String list(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("notifications", notificationService.findAll(user));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id, Authentication auth) {
        notificationService.markRead(id, getCurrentUser(auth));
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(Authentication auth) {
        notificationService.markAllRead(getCurrentUser(auth));
        return "redirect:/notifications";
    }
}
