package com.library.controller;

import com.library.repository.UserRepository;
import com.library.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Bơm sẵn dữ liệu chuông thông báo + người dùng hiện tại vào mọi trang (cho navbar fragment),
 * tránh phải lặp lại ở từng controller.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

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
        try {
            userRepository.findByStudentCode(auth.getName()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("notifUnread", notificationService.unreadCount(user));
                model.addAttribute("notifRecent", notificationService.recent(user));
            });
        } catch (Exception e) {
            log.error("Failed to inject global model attributes", e);
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception e,
                                            jakarta.servlet.http.HttpServletRequest request,
                                            RedirectAttributes redirectAttrs) {
        log.error("Unhandled exception on [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        redirectAttrs.addFlashAttribute("error",
                "Đã xảy ra lỗi không mong muốn: " + e.getMessage()
                + ". Vui lòng thử lại hoặc liên hệ quản trị viên.");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/client/home");
    }
}
