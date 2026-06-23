package com.library.controller;

import com.library.dto.UserBatchItem;
import com.library.entity.Permission;
import com.library.entity.UserRole;
import com.library.service.PermissionService;
import com.library.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

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
                             @RequestParam UserRole role,
                             @RequestParam(required = false) String password,
                             RedirectAttributes ra) {
        try {
            userService.create(studentCode, fullName, role, password);
            ra.addFlashAttribute("success", "Đã tạo tài khoản '" + studentCode + "'.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không tạo được tài khoản (mã số có thể đã tồn tại).");
        }
        return "redirect:/admin/users";
    }

    /** Tạo hàng loạt tài khoản (AJAX, body JSON). */
    @PostMapping("/users/batch")
    @ResponseBody
    public Map<String, Object> createBatch(@RequestBody List<UserBatchItem> items) {
        List<Map<String, Object>> results = userService.createBatch(items);
        long success = results.stream().filter(r -> Boolean.TRUE.equals(r.get("ok"))).count();
        Map<String, Object> resp = new HashMap<>();
        resp.put("results", results);
        resp.put("success", success);
        resp.put("failed", results.size() - success);
        return resp;
    }

    /** Tải file Excel mẫu để nhập danh sách người dùng. */
    @GetMapping("/users/import-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=mau_nguoidung.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sách người dùng");

            CellStyle headerStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle noteStyle = wb.createCellStyle();
            Font italicFont = wb.createFont();
            italicFont.setItalic(true);
            italicFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            noteStyle.setFont(italicFont);

            // Header
            String[] headers = {"Mã số", "Họ tên", "Vai trò", "Mật khẩu"};
            int[] widths     = {4000, 7000, 5000, 5000};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, widths[i]);
            }

            // Dòng mẫu 1
            Row s1 = sheet.createRow(1);
            s1.createCell(0).setCellValue("SV2024001");
            s1.createCell(1).setCellValue("Nguyễn Văn An");
            s1.createCell(2).setCellValue("STUDENT");
            s1.createCell(3).setCellValue("");

            // Dòng mẫu 2
            Row s2 = sheet.createRow(2);
            s2.createCell(0).setCellValue("GV2024001");
            s2.createCell(1).setCellValue("Trần Thị Bình");
            s2.createCell(2).setCellValue("LECTURER");
            s2.createCell(3).setCellValue("matkhau@123");

            // Ghi chú
            Row noteRow = sheet.createRow(4);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue(
                "Ghi chú: Vai trò hợp lệ: STUDENT | LECTURER | RESEARCHER | LIBRARIAN  " +
                "— Cột Mật khẩu để trống sẽ dùng mặc định 'password123'.");
            noteCell.setCellStyle(noteStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(4, 4, 0, 3));

            wb.write(response.getOutputStream());
        }
    }

    /** Parse file Excel → trả về JSON danh sách hàng để hiển thị preview (có thể sửa). */
    @PostMapping("/users/import-preview")
    @ResponseBody
    public Map<String, Object> importPreview(@RequestParam("file") MultipartFile file) {
        Map<String, Object> resp = new HashMap<>();
        if (file.isEmpty()) {
            resp.put("ok", false); resp.put("message", "Chưa chọn file."); return resp;
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String code  = cellStr(row, 0);
                String name  = cellStr(row, 1);
                String role  = cellStr(row, 2).toUpperCase();
                String pass  = cellStr(row, 3);
                if (code.isBlank() && name.isBlank() && role.isBlank()) continue;

                List<String> errors = new ArrayList<>();
                if (code.isBlank()) errors.add("Thiếu mã số");
                if (name.isBlank()) errors.add("Thiếu họ tên");
                if (role.isBlank()) {
                    errors.add("Thiếu vai trò");
                } else {
                    try { UserRole.valueOf(role); }
                    catch (Exception e) { errors.add("Vai trò không hợp lệ"); }
                }
                if (!code.isBlank() && userService.findByStudentCode(code).isPresent()) {
                    errors.add("Mã số đã tồn tại");
                }

                Map<String, Object> r = new LinkedHashMap<>();
                r.put("studentCode", code);
                r.put("fullName", name);
                r.put("role", role);
                r.put("password", pass);
                r.put("errors", errors);
                r.put("valid", errors.isEmpty());
                rows.add(r);
            }
            resp.put("ok", true);
            resp.put("rows", rows);
            resp.put("validCount",   rows.stream().filter(r -> Boolean.TRUE.equals(r.get("valid"))).count());
            resp.put("invalidCount", rows.stream().filter(r -> !Boolean.TRUE.equals(r.get("valid"))).count());
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Không đọc được file: " + e.getMessage());
        }
        return resp;
    }

    private static String cellStr(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
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
