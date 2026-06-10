package com.library.controller;

import com.library.dto.CartItem;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.RuleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final BookService bookService;
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final RuleService ruleService;

    public ClientController(BookService bookService, LoanService loanService,
                            UserRepository userRepository, RuleService ruleService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.ruleService = ruleService;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByStudentCode(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // =========== TRANG CHỦ & TÌM KIẾM ===========

    @GetMapping("/home")
    public String home(Model model, Authentication auth,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) DocType docType,
                       @RequestParam(defaultValue = "false") boolean availableOnly,
                       HttpSession session) {
        User user = getCurrentUser(auth);
        // Chỉ hiển thị các loại tài liệu mà vai trò này được phép xem (theo cấu hình admin)
        List<Book> books = bookService.search(keyword, docType, availableOnly,
                ruleService.visibleDocTypes(user.getRole()));
        List<CartItem> cart = getCart(session);

        model.addAttribute("user", user);
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDocType", docType);
        model.addAttribute("availableOnly", availableOnly);
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("cartCount", cart.size());
        model.addAttribute("hasOverdue", loanService.hasOverdue(user));
        return "client/home";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable Long id, Model model, Authentication auth,
                             HttpSession session) {
        User user = getCurrentUser(auth);
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        List<CartItem> cart = getCart(session);
        boolean inCart = cart.stream().anyMatch(c -> c.getBookId().equals(id));

        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("inCart", inCart);
        model.addAttribute("cartCount", cart.size());
        return "client/book-detail";
    }

    // =========== GIỎ SÁCH ===========

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId, Authentication auth, HttpSession session,
                            RedirectAttributes redirectAttrs) {
        List<CartItem> cart = getCart(session);

        if (cart.stream().anyMatch(c -> c.getBookId().equals(bookId))) {
            redirectAttrs.addFlashAttribute("warn", "Sách này đã có trong giỏ.");
            return "redirect:/client/home";
        }
        if (cart.size() >= 5) {
            redirectAttrs.addFlashAttribute("error", "Giỏ sách tối đa 5 cuốn.");
            return "redirect:/client/home";
        }

        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Chặn thêm loại tài liệu đang bị ẩn với vai trò này
        User user = getCurrentUser(auth);
        if (!ruleService.visibleDocTypes(user.getRole()).contains(book.getDocType())) {
            redirectAttrs.addFlashAttribute("error", "Loại tài liệu này hiện không khả dụng để mượn.");
            return "redirect:/client/home";
        }

        if (!book.isAvailable()) {
            redirectAttrs.addFlashAttribute("error", "Sách '" + book.getTitle() + "' hiện đã hết.");
            return "redirect:/client/home";
        }

        CartItem item = new CartItem(book.getId(), book.getTitle(), book.getAuthor(),
                book.getDocType(), book.getClassificationCode());
        cart.add(item);
        redirectAttrs.addFlashAttribute("success", "Đã thêm '" + book.getTitle() + "' vào giỏ.");
        return "redirect:/client/home";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Authentication auth, HttpSession session) {
        User user = getCurrentUser(auth);
        List<CartItem> cart = getCart(session);
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartCount", cart.size());
        return "client/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long bookId, HttpSession session,
                                 RedirectAttributes redirectAttrs) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(c -> c.getBookId().equals(bookId));
        redirectAttrs.addFlashAttribute("success", "Đã xóa sách khỏi giỏ.");
        return "redirect:/client/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttrs) {
        session.removeAttribute("cart");
        redirectAttrs.addFlashAttribute("success", "Đã xóa toàn bộ giỏ sách.");
        return "redirect:/client/cart";
    }

    // =========== PHIẾU MƯỢN ===========

    @PostMapping("/loans/submit")
    public String submitLoan(Authentication auth, HttpSession session,
                             RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        List<CartItem> cart = getCart(session);

        ValidationResult result = loanService.submitLoan(user, cart);
        if (!result.isValid()) {
            redirectAttrs.addFlashAttribute("errors", result.getErrors());
            return "redirect:/client/cart";
        }

        session.removeAttribute("cart");
        redirectAttrs.addFlashAttribute("success",
                "Tạo đơn mượn thành công! Mã mượn: " + result.getPickupCode() +
                ". Vui lòng đến thư viện lấy sách trong vòng 24 giờ.");
        return "redirect:/client/my-loans";
    }

    // =========== TRANG CÁ NHÂN ===========

    @GetMapping("/my-loans")
    public String myLoans(Model model, Authentication auth, HttpSession session) {
        User user = getCurrentUser(auth);
        List<Loan> loans = loanService.findAllByUser(user);
        List<LoanDetail> activeDetails = loanService.findActiveByUser(user);
        List<Loan> awaitingLoans = loanService.findByUserAndStatus(user, LoanStatus.AWAITING_PICKUP);
        List<CartItem> cart = getCart(session);

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);
        model.addAttribute("activeDetails", activeDetails);
        model.addAttribute("awaitingLoans", awaitingLoans);
        model.addAttribute("cartCount", cart.size());
        model.addAttribute("hasOverdue", loanService.hasOverdue(user));
        return "client/my-loans";
    }

    @PostMapping("/loans/{id}/pickup")
    public String confirmPickup(@PathVariable Long id, @RequestParam String code,
                                Authentication auth, RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        boolean ok = loanService.confirmPickup(id, user, code);
        if (ok) {
            redirectAttrs.addFlashAttribute("success",
                    "Xác nhận nhận sách thành công! Đơn đã chuyển sang Đang mượn.");
        } else {
            redirectAttrs.addFlashAttribute("error",
                    "Mã mượn không đúng hoặc đơn không hợp lệ. Vui lòng kiểm tra lại.");
        }
        return "redirect:/client/my-loans";
    }

    @PostMapping("/loans/renew/{detailId}")
    public String renewBook(@PathVariable Long detailId, Authentication auth,
                            RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        boolean success = loanService.renewBook(detailId, user);
        if (success) {
            redirectAttrs.addFlashAttribute("success", "Gia hạn thành công!");
        } else {
            redirectAttrs.addFlashAttribute("error",
                    "Không thể gia hạn. Bạn đã hết số lần gia hạn hoặc không đủ điều kiện.");
        }
        return "redirect:/client/my-loans";
    }
}
