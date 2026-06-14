package com.library.controller;

import com.library.entity.Permission;
import com.library.entity.UserRole;
import com.library.service.PermissionService;
import com.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Khu của ADMIN: quản lý tài khoản (CRUD + gán role) và phân quyền (ma trận role × quyền).
 * Không đụng tới sách/đơn mượn/quy tắc — đó là việc của thủ thư.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PermissionService permissionService;

    public AdminController(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    // =========== QUẢN LÝ NGƯỜI DÙNG (CRUD + search/filter) ===========

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) UserRole role,
                            Model model) {
        model.addAttribute("users", userService.search(keyword, role));
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String studentCode, @RequestParam String fullName,
                             @RequestParam UserRole role, @RequestParam String password,
                             RedirectAttributes ra) {
        try {
            userService.create(studentCode, fullName, role, password);
            ra.addFlashAttribute("success", "Đã tạo tài khoản '" + studentCode + "'.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không tạo được tài khoản (mã số có thể đã tồn tại).");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @RequestParam String fullName,
                             @RequestParam UserRole role, RedirectAttributes ra) {
        try {
            userService.update(id, fullName, role);
            ra.addFlashAttribute("success", "Đã cập nhật tài khoản.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, @RequestParam String password,
                                RedirectAttributes ra) {
        if (password == null || password.length() < 6) {
            ra.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return "redirect:/admin/users";
        }
        userService.resetPassword(id, password);
        ra.addFlashAttribute("success", "Đã đặt lại mật khẩu.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("success", "Đã xoá tài khoản.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không xoá được tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleCardStatus(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản.");
        return "redirect:/admin/users";
    }

    // =========== PHÂN QUYỀN (chọn vai trò → bật/tắt quyền) ===========

    @GetMapping("/permissions")
    public String permissions(@RequestParam(required = false) UserRole role, Model model) {
        UserRole selected = (role != null) ? role : UserRole.STUDENT;
        // Các vai trò chọn được (ADMIN có quyền quản trị cố định nên không liệt kê)
        model.addAttribute("roles", new UserRole[]{
                UserRole.STUDENT, UserRole.LECTURER, UserRole.RESEARCHER, UserRole.LIBRARIAN});
        model.addAttribute("selectedRole", selected);
        model.addAttribute("categories", Permission.Category.values());
        model.addAttribute("permissions", Permission.values());
        return "admin/permissions";
    }

    @PostMapping("/permissions")
    public String savePermissions(@RequestParam UserRole role,
                                  @RequestParam(value = "grants", required = false) List<String> grants,
                                  RedirectAttributes ra) {
        Set<Permission> granted = new HashSet<>();
        if (grants != null) {
            for (String g : grants) granted.add(Permission.valueOf(g));
        }
        permissionService.updateGrantsForRole(role, granted);
        ra.addFlashAttribute("success", "Đã lưu phân quyền cho vai trò " + role + ".");
        return "redirect:/admin/permissions?role=" + role.name();
    }
}
