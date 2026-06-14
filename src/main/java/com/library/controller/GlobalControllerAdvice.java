package com.library.controller;

import com.library.repository.UserRepository;
import com.library.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Bơm sẵn dữ liệu chuông thông báo + người dùng hiện tại vào mọi trang (cho navbar fragment),
 * tránh phải lặp lại ở từng controller.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public GlobalControllerAdvice(UserRepository userRepository,
                                  NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void injectGlobals(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return;
        }
        userRepository.findByStudentCode(auth.getName()).ifPresent(user -> {
            model.addAttribute("currentUser", user);
            model.addAttribute("notifUnread", notificationService.unreadCount(user));
            model.addAttribute("notifRecent", notificationService.recent(user));
        });
    }
}
