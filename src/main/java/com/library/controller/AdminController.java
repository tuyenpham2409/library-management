package com.library.controller;

import com.library.dto.BookForm;
import com.library.dto.RuleConfigForm;

import com.library.entity.*;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.RuleService;
import com.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final LoanService loanService;
    private final UserService userService;
    private final RuleService ruleService;

    public AdminController(BookService bookService, LoanService loanService,
                           UserService userService, RuleService ruleService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userService = userService;
        this.ruleService = ruleService;
    }

    // =========== DASHBOARD ===========

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("awaitingCount", loanService.countAwaitingPickup());
        model.addAttribute("activeCount", loanService.countActiveLoans());
        model.addAttribute("overdueList", loanService.findAllOverdue());
        model.addAttribute("borrowingCount", loanService.countBorrowingDetails());
        return "admin/dashboard";
    }

    // =========== QUẢN LÝ ĐƠN MƯỢN (THỦ THƯ) ===========

    // Thủ thư chỉ xem danh sách để chuẩn bị sách; SV tự xác nhận nhận sách bằng mã mượn.
    @GetMapping("/loans/pending")
    public String awaitingLoans(Model model) {
        List<Loan> loans = loanService.findByStatus(LoanStatus.AWAITING_PICKUP);
        model.addAttribute("loans", loans);
        return "admin/loans-pending";
    }

    @PostMapping("/loans/{id}/cancel")
    public String cancelLoan(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            loanService.cancelLoan(id);
            redirectAttrs.addFlashAttribute("success", "Phiếu mượn đã bị huỷ.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loans/pending";
    }

    // =========== QUẢN LÝ TRẢ SÁCH ===========

    @GetMapping("/loans/active")
    public String activeLoans(Model model) {
        List<LoanDetail> details = loanService.findBorrowingDetails();
        model.addAttribute("details", details);
        return "admin/loans-active";
    }

    @PostMapping("/loans/details/{id}/return")
    public String returnBook(@PathVariable Long id,
                             @RequestParam(defaultValue = "GOOD") BookCondition condition,
                             RedirectAttributes redirectAttrs) {
        try {
            java.math.BigDecimal fine = loanService.returnBook(id, condition);
            if (fine.compareTo(java.math.BigDecimal.ZERO) > 0) {
                redirectAttrs.addFlashAttribute("warn",
                        "Trả sách thành công. Tiền phạt: " +
                        String.format("%,.0f", fine) + " VND");
            } else {
                redirectAttrs.addFlashAttribute("success", "Trả sách thành công!");
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loans/active";
    }

    // =========== QUẢN LÝ SÁCH ===========

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", true);
        return "admin/book-form";
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute BookForm form, RedirectAttributes redirectAttrs) {
        bookService.save(form);
        redirectAttrs.addFlashAttribute("success", "Đã thêm sách '" + form.getTitle() + "'!");
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        model.addAttribute("bookForm", bookService.toForm(book));
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", false);
        return "admin/book-form";
    }

    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id, @ModelAttribute BookForm form,
                             RedirectAttributes redirectAttrs) {
        bookService.update(id, form);
        redirectAttrs.addFlashAttribute("success", "Đã cập nhật sách '" + form.getTitle() + "'!");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Đã xóa sách '" + book.getTitle() + "'.");
        return "redirect:/admin/books";
    }

    // =========== QUẢN LÝ USERS (CHỈ ADMIN) ===========

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String studentCode, @RequestParam String fullName,
                             @RequestParam UserRole role, @RequestParam String password,
                             RedirectAttributes redirectAttrs) {
        try {
            userService.create(studentCode, fullName, role, password);
            redirectAttrs.addFlashAttribute("success", "Đã tạo tài khoản '" + studentCode + "'.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Không tạo được tài khoản (mã số có thể đã tồn tại).");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.toggleCardStatus(id);
        redirectAttrs.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản.");
        return "redirect:/admin/users";
    }

    // =========== PHÂN QUYỀN / CẤU HÌNH QUY TẮC (CHỈ ADMIN) ===========

    @GetMapping("/config")
    public String configPage(Model model) {
        RuleConfigForm form = new RuleConfigForm();
        form.setRules(ruleService.findAllOrdered());
        model.addAttribute("form", form);
        return "admin/config";
    }

    @PostMapping("/config")
    public String saveConfig(@ModelAttribute RuleConfigForm form, RedirectAttributes redirectAttrs) {
        ruleService.updateRules(form.getRules());
        redirectAttrs.addFlashAttribute("success", "Đã lưu cấu hình quy tắc mượn.");
        return "redirect:/admin/config";
    }
}
