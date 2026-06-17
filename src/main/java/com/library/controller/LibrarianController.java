package com.library.controller;

import com.library.dto.BookForm;
import com.library.dto.ReturnBatchItem;
import com.library.dto.RuleConfigForm;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.PermissionService;
import com.library.service.RuleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Khu vận hành của THỦ THƯ: đơn mượn, trả sách, thu phí, quản lý sách, quy tắc mượn.
 * Tách hẳn khỏi khu Admin (tài khoản + phân quyền).
 */
@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private final BookService bookService;
    private final LoanService loanService;
    private final RuleService ruleService;
    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public LibrarianController(BookService bookService, LoanService loanService,
                               RuleService ruleService, PermissionService permissionService,
                               UserRepository userRepository) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.ruleService = ruleService;
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByStudentCode(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /** Trả về true (và set flash error) nếu thủ thư KHÔNG có quyền yêu cầu. */
    private boolean lacks(Authentication auth, Permission p, RedirectAttributes ra) {
        if (!permissionService.has(getCurrentUser(auth).getRole(), p)) {
            ra.addFlashAttribute("error", "Bạn không được cấp quyền thực hiện thao tác này.");
            return true;
        }
        return false;
    }

    // =========== DASHBOARD ===========

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("awaitingCount", loanService.countAwaitingPickup());
        model.addAttribute("activeCount", loanService.countActiveLoans());
        model.addAttribute("overdueList", loanService.findAllOverdue());
        model.addAttribute("borrowingCount", loanService.countBorrowingDetails());
        return "librarian/dashboard";
    }

    // =========== QUẢN LÝ ĐƠN MƯỢN (có filter trạng thái) ===========

    @GetMapping("/loans")
    public String loans(@RequestParam(required = false) LoanStatus status, Model model) {
        List<Loan> loans = (status == null)
                ? loanService.findAll()
                : loanService.findByStatus(status);
        model.addAttribute("loans", loans);
        model.addAttribute("statuses", LoanStatus.values());
        model.addAttribute("selectedStatus", status);
        return "librarian/loans";
    }

    @PostMapping("/loans/{id}/cancel")
    public String cancelLoan(@PathVariable Long id, @RequestParam String reason,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_CANCEL, ra)) return "redirect:/librarian/loans";
        if (reason == null || reason.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập lý do huỷ đơn.");
            return "redirect:/librarian/loans";
        }
        try {
            loanService.cancelLoan(id, reason.trim(), getCurrentUser(auth).getRole());
            ra.addFlashAttribute("success", "Đã huỷ đơn mượn #" + id + " và gửi lý do cho bạn đọc.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/librarian/loans#loan" + id;
    }

    @PostMapping("/loans/details/{id}/return")
    public String returnBook(@PathVariable Long id,
                             @RequestParam(defaultValue = "GOOD") BookCondition condition,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_RETURN, ra)) return "redirect:/librarian/loans";
        try {
            java.math.BigDecimal fine = loanService.returnBook(id, condition);
            if (fine.compareTo(java.math.BigDecimal.ZERO) > 0) {
                ra.addFlashAttribute("warn", "Đã trả sách. Còn phí phạt " +
                        String.format("%,.0f", fine) + " VND — bấm 'Đã thu phí' khi bạn đọc thanh toán.");
            } else {
                ra.addFlashAttribute("success", "Trả sách thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/librarian/loans";
    }

    @PostMapping("/loans/details/{id}/pay-fine")
    @ResponseBody
    public Map<String, Object> payFine(@PathVariable Long id, Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.LOAN_PAY_FINE)) {
            resp.put("ok", false);
            resp.put("message", "Bạn không được cấp quyền thực hiện thao tác này.");
            return resp;
        }
        try {
            boolean ok = loanService.payFine(id);
            resp.put("ok", ok);
            resp.put("message", ok ? "Đã ghi nhận thu phí." : "Không có phí cần thu hoặc đã thu trước đó.");
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Lỗi: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/loans/{loanId}/return-batch")
    @ResponseBody
    public Map<String, Object> returnBatch(@PathVariable Long loanId,
                                           @RequestBody List<ReturnBatchItem> items,
                                           Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.LOAN_RETURN)) {
            resp.put("ok", false);
            resp.put("message", "Bạn không được cấp quyền thực hiện thao tác này.");
            resp.put("results", List.of());
            resp.put("loanCompleted", false);
            return resp;
        }
        try {
            List<Map<String, Object>> results = loanService.returnBatch(loanId, items);

            long totalFine = results.stream()
                    .mapToLong(r -> ((Number) r.get("fine")).longValue()).sum();

            boolean completed = loanService.findById(loanId)
                    .map(l -> l.getStatus() == LoanStatus.COMPLETED).orElse(false);

            String message;
            if (results.isEmpty()) {
                message = "Không có quyển nào ở trạng thái Đang mượn để xử lý.";
            } else if (totalFine > 0) {
                message = "Đã trả " + results.size() + " quyển. Tổng phí phạt: "
                        + String.format("%,d", totalFine) + " VND.";
            } else {
                message = "Đã trả " + results.size() + " quyển thành công.";
            }

            resp.put("ok", true);
            resp.put("results", results);
            resp.put("loanCompleted", completed);
            resp.put("message", message);
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Lỗi: " + e.getMessage());
            resp.put("results", List.of());
            resp.put("loanCompleted", false);
        }
        return resp;
    }

    // =========== QUẢN LÝ SÁCH ===========

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "librarian/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", true);
        return "librarian/book-form";
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute BookForm form, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_CREATE, ra)) return "redirect:/librarian/books";
        bookService.save(form);
        ra.addFlashAttribute("success", "Đã thêm sách '" + form.getTitle() + "'!");
        return "redirect:/librarian/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        model.addAttribute("bookForm", bookService.toForm(book));
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", false);
        return "librarian/book-form";
    }

    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id, @ModelAttribute BookForm form,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_UPDATE, ra)) return "redirect:/librarian/books";
        bookService.update(id, form);
        ra.addFlashAttribute("success", "Đã cập nhật sách '" + form.getTitle() + "'!");
        return "redirect:/librarian/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_DELETE, ra)) return "redirect:/librarian/books";
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookService.delete(id);
        ra.addFlashAttribute("success", "Đã xóa sách '" + book.getTitle() + "'.");
        return "redirect:/librarian/books";
    }

    // =========== QUY TẮC MƯỢN (rule mượn sách) ===========

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        RuleConfigForm form = new RuleConfigForm();
        form.setRules(ruleService.findAllOrdered());
        model.addAttribute("form", form);
        return "librarian/rules";
    }

    @PostMapping("/rules")
    public String saveRules(@ModelAttribute RuleConfigForm form, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.RULE_MANAGE, ra)) return "redirect:/librarian/rules";
        ruleService.updateRules(form.getRules());
        ra.addFlashAttribute("success", "Đã lưu quy tắc mượn.");
        return "redirect:/librarian/rules";
    }

    // =========== CHO MƯỢN ĐỌC TẠI CHỖ (chỉ tài liệu nội sinh) ===========

    @GetMapping("/inhouse")
    public String inhousePage(Model model) {
        // Chỉ tài liệu nội sinh (RESTRICTED) mới đọc tại chỗ
        model.addAttribute("restrictedBooks", bookService.search(null, DocType.RESTRICTED, false));
        model.addAttribute("activeInhouse", loanService.findInHouseActive());
        return "librarian/inhouse";
    }

    @PostMapping("/inhouse")
    public String lendInHouse(@RequestParam String studentCode, @RequestParam Long bookId,
                              Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_LEND_INHOUSE, ra)) return "redirect:/librarian/inhouse";
        User reader = userRepository.findByStudentCode(studentCode.trim()).orElse(null);
        if (reader == null) {
            ra.addFlashAttribute("error", "Không tìm thấy bạn đọc có mã '" + studentCode + "'.");
            return "redirect:/librarian/inhouse";
        }
        ValidationResult r = loanService.lendInHouse(reader, bookId);
        if (r.isValid()) {
            ra.addFlashAttribute("success", "Đã lập phiếu đọc tại chỗ cho " + reader.getFullName() + ".");
        } else {
            ra.addFlashAttribute("errors", r.getErrors());
        }
        return "redirect:/librarian/inhouse";
    }
}
