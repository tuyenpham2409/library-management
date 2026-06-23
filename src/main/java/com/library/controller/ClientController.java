package com.library.controller;

import com.library.dto.CartItem;
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

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final BookService bookService;
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final RuleService ruleService;
    private final PermissionService permissionService;

    public ClientController(BookService bookService, LoanService loanService,
                            UserRepository userRepository, RuleService ruleService,
                            PermissionService permissionService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.ruleService = ruleService;
        this.permissionService = permissionService;
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
        model.addAttribute("canBorrow", permissionService.has(user.getRole(), Permission.BORROW));
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
        model.addAttribute("hasOverdue", loanService.hasOverdue(user));
        model.addAttribute("canBorrow", permissionService.has(user.getRole(), Permission.BORROW));
        return "client/book-detail";
    }

    // =========== GIỎ SÁCH ===========

    /** Kết quả thêm giỏ dùng chung cho form thường và AJAX. */
    private record CartAddResult(boolean ok, String message, int cartCount) {}

    private CartAddResult addToCartInternal(Long bookId, User user, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (!permissionService.has(user.getRole(), Permission.BORROW)) {
            return new CartAddResult(false, "Bạn không được cấp quyền mượn sách.", cart.size());
        }
        if (cart.stream().anyMatch(c -> c.getBookId().equals(bookId))) {
            return new CartAddResult(false, "Sách này đã có trong giỏ.", cart.size());
        }
        if (cart.size() >= 5) {
            return new CartAddResult(false, "Giỏ sách tối đa 5 cuốn.", cart.size());
        }
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.getDocType() == DocType.RESTRICTED) {
            return new CartAddResult(false, "Tài liệu nội sinh chỉ được đọc tại chỗ.", cart.size());
        }
        if (!ruleService.visibleDocTypes(user.getRole()).contains(book.getDocType())) {
            return new CartAddResult(false, "Loại tài liệu này hiện không khả dụng để mượn.", cart.size());
        }
        if (!book.isAvailable()) {
            return new CartAddResult(false, "Sách '" + book.getTitle() + "' hiện đã hết.", cart.size());
        }
        cart.add(new CartItem(book.getId(), book.getTitle(), book.getAuthor(),
                book.getDocType(), book.getClassificationCode()));
        return new CartAddResult(true, "Đã thêm '" + book.getTitle() + "' vào giỏ.", cart.size());
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId, Authentication auth, HttpSession session,
                            RedirectAttributes redirectAttrs) {
        CartAddResult r = addToCartInternal(bookId, getCurrentUser(auth), session);
        redirectAttrs.addFlashAttribute(r.ok() ? "success" : "error", r.message());
        return "redirect:/client/home";
    }

    /** Phiên bản AJAX: thêm giỏ không reload trang, trả JSON cho JS cập nhật badge + toast. */
    @PostMapping("/cart/add-ajax")
    @ResponseBody
    public Map<String, Object> addToCartAjax(@RequestParam Long bookId, Authentication auth,
                                             HttpSession session) {
        CartAddResult r = addToCartInternal(bookId, getCurrentUser(auth), session);
        Map<String, Object> res = new HashMap<>();
        res.put("ok", r.ok());
        res.put("message", r.message());
        res.put("cartCount", r.cartCount());
        return res;
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

    // =========== NỘI QUY MƯỢN SÁCH ===========

    @GetMapping("/rules")
    public String borrowingRules(Model model, Authentication auth, HttpSession session) {
        User user = getCurrentUser(auth);
        List<BorrowingRule> userRules = ruleService.findAllOrdered().stream()
                .filter(r -> r.getUserRole() == user.getRole())
                .collect(Collectors.toList());

        List<CartItem> cart = getCart(session);
        model.addAttribute("user", user);
        model.addAttribute("userRules", userRules);
        model.addAttribute("cartCount", cart.size());
        model.addAttribute("hasOverdue", loanService.hasOverdue(user));
        return "client/rules";
    }

    // =========== PHIẾU MƯỢN ===========

    @PostMapping("/loans/submit")
    public String submitLoan(Authentication auth, HttpSession session,
                             RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        if (!permissionService.has(user.getRole(), Permission.BORROW)) {
            redirectAttrs.addFlashAttribute("error", "Bạn không được cấp quyền mượn sách.");
            return "redirect:/client/cart";
        }
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
    public String myLoans(Model model, Authentication auth, HttpSession session,
                          @RequestParam(required = false) LoanStatus status) {
        User user = getCurrentUser(auth);
        List<Loan> loans = (status == null)
                ? loanService.findAllByUser(user)
                : loanService.findByUserAndStatus(user, status);
        List<LoanDetail> activeDetails = loanService.findActiveByUser(user);
        List<Loan> awaitingLoans = loanService.findByUserAndStatus(user, LoanStatus.AWAITING_PICKUP);
        List<CartItem> cart = getCart(session);

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);
        model.addAttribute("activeDetails", activeDetails);
        model.addAttribute("awaitingLoans", awaitingLoans);
        model.addAttribute("statuses", LoanStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("cartCount", cart.size());
        model.addAttribute("hasOverdue", loanService.hasOverdue(user));
        model.addAttribute("canRenew", permissionService.has(user.getRole(), Permission.RENEW));
        model.addAttribute("canCancelOwn", permissionService.has(user.getRole(), Permission.CANCEL_OWN));
        return "client/my-loans";
    }

    @PostMapping("/loans/{id}/cancel")
    public String cancelOwnLoan(@PathVariable Long id, Authentication auth,
                                RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        if (!permissionService.has(user.getRole(), Permission.CANCEL_OWN)) {
            redirectAttrs.addFlashAttribute("error", "Bạn không được cấp quyền tự huỷ đơn.");
            return "redirect:/client/my-loans";
        }
        boolean ok = loanService.cancelByReader(id, user);
        if (ok) {
            redirectAttrs.addFlashAttribute("success", "Đã huỷ đơn mượn #" + id + ".");
        } else {
            redirectAttrs.addFlashAttribute("error",
                    "Không thể huỷ. Chỉ huỷ được đơn đang chờ lấy (chưa nhận sách).");
        }
        return "redirect:/client/my-loans";
    }

    @PostMapping("/loans/renew/{detailId}")
    public String renewBook(@PathVariable Long detailId, Authentication auth,
                            RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        if (!permissionService.has(user.getRole(), Permission.RENEW)) {
            redirectAttrs.addFlashAttribute("error", "Bạn không được cấp quyền gia hạn.");
            return "redirect:/client/my-loans";
        }
        try {
            String error = loanService.renewBook(detailId, user);
            if (error == null) {
                redirectAttrs.addFlashAttribute("success", "Gia hạn thành công!");
            } else {
                redirectAttrs.addFlashAttribute("error", error);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Gia hạn thất bại: " + e.getMessage() + ". Vui lòng thử lại hoặc liên hệ thủ thư.");
        }
        return "redirect:/client/my-loans";
    }
}
