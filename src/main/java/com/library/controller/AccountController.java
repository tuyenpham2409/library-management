package com.library.controller;

import com.library.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Trang tài khoản cá nhân — cho mọi vai trò tự đổi mật khẩu sau khi đăng nhập.
 */
@Controller
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account/password")
    public String changePasswordForm() {
        return "account/change-password";
    }

    @PostMapping("/account/password")
    public String changePassword(Authentication auth,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttrs) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
        } else if (newPassword.length() < 6) {
            redirectAttrs.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
        } else if (userService.changePassword(auth.getName(), currentPassword, newPassword)) {
            redirectAttrs.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } else {
            redirectAttrs.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
        }
        return "redirect:/account/password";
    }
}
