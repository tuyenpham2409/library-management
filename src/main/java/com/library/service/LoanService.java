package com.library.service;

import com.library.dto.CartItem;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.*;
import com.library.util.RuleEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final BookRepository bookRepository;
    private final BorrowingRuleRepository ruleRepository;
    private final RuleEngine ruleEngine;

    public LoanService(LoanRepository loanRepository,
                       LoanDetailRepository loanDetailRepository,
                       BookRepository bookRepository,
                       BorrowingRuleRepository ruleRepository,
                       RuleEngine ruleEngine) {
        this.loanRepository = loanRepository;
        this.loanDetailRepository = loanDetailRepository;
        this.bookRepository = bookRepository;
        this.ruleRepository = ruleRepository;
        this.ruleEngine = ruleEngine;
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // bỏ ký tự dễ nhầm (0/O, 1/I)
    private static final int PICKUP_HOURS = 24; // Hạn đến lấy sách

    /**
     * Tạo đơn mượn tự phục vụ: đủ điều kiện thì cho mượn ngay (không cần duyệt).
     * Sinh mã mượn, giữ chỗ sách và đặt hạn lấy 24h.
     */
    public ValidationResult submitLoan(User user, List<CartItem> cart) {
        // Kiểm tra điều kiện mượn
        ValidationResult result = ruleEngine.validate(user, cart);
        if (!result.isValid()) {
            return result;
        }

        // Nạp sách và kiểm tra còn hàng TRƯỚC khi thay đổi gì (tránh giữ chỗ lẻ nếu một cuốn đã hết)
        List<Book> books = new ArrayList<>();
        for (CartItem item : cart) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("Sách không tồn tại: " + item.getBookId()));
            if (!book.isAvailable()) {
                return ValidationResult.fail("Sách '" + book.getTitle() + "' hiện không còn sẵn có.");
            }
            books.add(book);
        }

        // Tạo đơn ở trạng thái CHỜ LẤY + sinh mã mượn + hạn lấy 24h
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setStatus(LoanStatus.AWAITING_PICKUP);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setPickupCode(generatePickupCode());
        loan.setPickupDeadline(LocalDateTime.now().plusHours(PICKUP_HOURS));
        loan = loanRepository.save(loan);

        // Tạo chi tiết: giữ chỗ (RESERVED), chưa có hạn trả cho tới khi lấy sách
        List<LoanDetail> details = new ArrayList<>();
        for (Book book : books) {
            LoanDetail detail = new LoanDetail();
            detail.setLoan(loan);
            detail.setBook(book);
            detail.setRenewalCount(0);
            detail.setStatus(LoanDetailStatus.RESERVED);
            detail.setFineAmount(BigDecimal.ZERO);
            details.add(detail);

            // Giữ chỗ: giảm available_copies ngay khi tạo đơn
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }
        loanDetailRepository.saveAll(details);

        return ValidationResult.ok(loan.getPickupCode());
    }

    /**
     * SV tự xác nhận đã nhận sách bằng cách nhập lại mã mượn (không cần thủ thư duyệt).
     * Kiểm tra: đúng chủ đơn + đơn đang chờ lấy + mã mượn khớp.
     * Khi hợp lệ: chuyển sang ĐANG MƯỢN và set hạn trả theo RuleEngine.
     * @return true nếu xác nhận thành công.
     */
    public boolean confirmPickup(Long loanId, User user, String code) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan == null) return false;
        if (!loan.getUser().getId().equals(user.getId())) return false;
        if (loan.getStatus() != LoanStatus.AWAITING_PICKUP) return false;
        if (code == null || loan.getPickupCode() == null
                || !loan.getPickupCode().equalsIgnoreCase(code.trim())) return false;

        loan.setStatus(LoanStatus.BORROWED);
        loan.setPickedUpAt(LocalDateTime.now());
        loanRepository.save(loan);

        LocalDate pickupDate = LocalDate.now();
        UserRole role = user.getRole();
        for (LoanDetail detail : loanDetailRepository.findByLoan(loan)) {
            detail.setDueDate(ruleEngine.calculateDueDate(role, detail.getBook().getDocType(), pickupDate));
            detail.setStatus(LoanDetailStatus.BORROWING);
            loanDetailRepository.save(detail);
        }
        return true;
    }

    /**
     * Huỷ đơn mượn (thủ thư huỷ tay hoặc tự huỷ quá hạn).
     * Trả lại available_copies cho từng cuốn.
     */
    public void cancelLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        List<LoanDetail> details = loanDetailRepository.findByLoan(loan);
        for (LoanDetail d : details) {
            Book book = d.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }
        loan.setStatus(LoanStatus.CANCELLED);
        loanRepository.save(loan);
    }

    /**
     * Tự huỷ các đơn chờ lấy đã quá hạn 24h. Được scheduler gọi định kỳ.
     * @return số đơn đã huỷ.
     */
    public int cancelExpiredPickups() {
        List<Loan> expired = loanRepository.findByStatusAndPickupDeadlineBefore(
                LoanStatus.AWAITING_PICKUP, LocalDateTime.now());
        for (Loan loan : expired) {
            cancelLoan(loan.getId());
        }
        return expired.size();
    }

    private String generatePickupCode() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Step 6 & 7: Admin xác nhận trả sách từng cuốn.
     */
    public BigDecimal returnBook(Long loanDetailId, BookCondition condition) {
        LoanDetail detail = loanDetailRepository.findById(loanDetailId)
                .orElseThrow(() -> new RuntimeException("LoanDetail not found: " + loanDetailId));

        detail.setReturnDate(LocalDateTime.now());
        detail.setConditionStatus(condition);
        detail.setStatus(LoanDetailStatus.RETURNED);

        // Tính phí phạt
        long fineAmount = ruleEngine.calculateFine(detail);
        detail.setFineAmount(BigDecimal.valueOf(fineAmount));
        loanDetailRepository.save(detail);

        // Tăng available_copies
        Book book = detail.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Kiểm tra nếu tất cả LoanDetail đã RETURNED → Loan = RETURNED
        Loan loan = detail.getLoan();
        List<LoanDetail> allDetails = loanDetailRepository.findByLoan(loan);
        boolean allReturned = allDetails.stream()
                .allMatch(d -> d.getStatus() == LoanDetailStatus.RETURNED);
        if (allReturned) {
            loan.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan);
        }

        return BigDecimal.valueOf(fineAmount);
    }

    /**
     * Gia hạn sách.
     */
    public boolean renewBook(Long loanDetailId, User user) {
        LoanDetail detail = loanDetailRepository.findById(loanDetailId)
                .orElseThrow(() -> new RuntimeException("LoanDetail not found: " + loanDetailId));

        // Kiểm tra quyền sở hữu: chỉ người mượn mới được gia hạn
        if (!detail.getLoan().getUser().getId().equals(user.getId())) {
            return false;
        }

        // Chỉ gia hạn được khi đang ở trạng thái BORROWING
        if (detail.getStatus() != LoanDetailStatus.BORROWING) {
            return false;
        }

        Book book = detail.getBook();
        Optional<BorrowingRule> ruleOpt = ruleRepository
                .findByUserRoleAndDocType(user.getRole(), book.getDocType());

        if (ruleOpt.isEmpty()) return false;
        BorrowingRule rule = ruleOpt.get();

        if (!ruleEngine.canRenew(detail, rule)) return false;
        if (rule.getRenewalDays() == null || rule.getRenewalDays() == 0) return false;

        LocalDate newDueDate = (detail.getDueDate() != null)
                ? detail.getDueDate().plusDays(rule.getRenewalDays())
                : LocalDate.now().plusDays(rule.getRenewalDays());

        detail.setDueDate(newDueDate);
        detail.setRenewalCount(detail.getRenewalCount() == null ? 1 : detail.getRenewalCount() + 1);
        loanDetailRepository.save(detail);
        return true;
    }

    // Queries
    public List<Loan> findAllByUser(User user) {
        return loanRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> findByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public List<Loan> findByUserAndStatus(User user, LoanStatus status) {
        return loanRepository.findByUserAndStatus(user, status);
    }

    public List<LoanDetail> findActiveByUser(User user) {
        return loanDetailRepository.findActiveBorrowingByUserId(user.getId());
    }

    public List<LoanDetail> findAllOverdue() {
        return loanDetailRepository.findOverdueDetails(LocalDate.now());
    }

    public List<LoanDetail> findBorrowingDetails() {
        return loanDetailRepository.findByStatus(LoanDetailStatus.BORROWING);
    }

    public Optional<LoanDetail> findDetailById(Long id) {
        return loanDetailRepository.findById(id);
    }

    // Dashboard stats
    public long countAwaitingPickup() {
        return loanRepository.countByStatus(LoanStatus.AWAITING_PICKUP);
    }

    public long countActiveLoans() {
        return loanRepository.countByStatus(LoanStatus.BORROWED);
    }

    public long countBorrowingDetails() {
        return loanDetailRepository.countByStatus(LoanDetailStatus.BORROWING);
    }

    public boolean hasOverdue(User user) {
        return !loanDetailRepository
                .findOverdueByUserId(user.getId(), LocalDate.now())
                .isEmpty();
    }

    public BorrowingRule getRule(UserRole role, DocType docType) {
        return ruleRepository.findByUserRoleAndDocType(role, docType).orElse(null);
    }
}
