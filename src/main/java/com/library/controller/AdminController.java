package com.library.controller;

import com.library.dto.BookForm;

import com.library.entity.*;
import com.library.service.BookService;
import com.library.service.LoanService;
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

    public AdminController(BookService bookService, LoanService loanService,
                           UserService userService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userService = userService;
    }

    // =========== DASHBOARD ===========

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("pendingCount", loanService.countPendingLoans());
        model.addAttribute("activeCount", loanService.countActiveLoans());
        model.addAttribute("overdueList", loanService.findAllOverdue());
        model.addAttribute("borrowingCount", loanService.countBorrowingDetails());
        return "admin/dashboard";
    }

    // =========== QUẢN LÝ PHIẾU MƯỢN ===========

    @GetMapping("/loans/pending")
    public String pendingLoans(Model model) {
        List<Loan> loans = loanService.findByStatus(LoanStatus.PENDING);
        model.addAttribute("loans", loans);
        return "admin/loans-pending";
    }

    @PostMapping("/loans/{id}/approve")
    public String approveLoan(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            loanService.approveLoan(id);
            redirectAttrs.addFlashAttribute("success", "Phiếu mượn đã được duyệt!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loans/pending";
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

    // =========== QUẢN LÝ USERS ===========

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.toggleCardStatus(id);
        redirectAttrs.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản.");
        return "redirect:/admin/users";
    }
}
