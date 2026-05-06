package com.library.service;

import com.library.dto.CartItem;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.*;
import com.library.util.RuleEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    /**
     * Step 3: Tạo phiếu mượn từ giỏ sách (sau khi đã validate bằng RuleEngine).
     */
    public ValidationResult submitLoan(User user, List<CartItem> cart) {
        // Validate
        ValidationResult result = ruleEngine.validate(user, cart);
        if (!result.isValid()) {
            return result;
        }

        // Tạo Loan header
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setStatus(LoanStatus.PENDING);
        loan.setCreatedAt(LocalDateTime.now());
        loan = loanRepository.save(loan);

        // Tạo LoanDetails
        List<LoanDetail> details = new ArrayList<>();
        for (CartItem item : cart) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("Sách không tồn tại: " + item.getBookId()));

            if (!book.isAvailable()) {
                loanRepository.delete(loan);
                return ValidationResult.fail("Sách '" + book.getTitle() + "' hiện không còn sẵn có.");
            }

            LoanDetail detail = new LoanDetail();
            detail.setLoan(loan);
            detail.setBook(book);
            detail.setRenewalCount(0);
            detail.setStatus(LoanDetailStatus.BORROWING);
            detail.setFineAmount(BigDecimal.ZERO);
            details.add(detail);

            // Giảm available_copies ngay khi tạo phiếu
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }
        loanDetailRepository.saveAll(details);

        return ValidationResult.ok();
    }

    /**
     * Step 5: Admin duyệt phiếu mượn.
     * Set due_date theo RuleEngine.
     */
    public void approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedAt(LocalDateTime.now());
        loanRepository.save(loan);

        LocalDate approvedDate = LocalDate.now();
        UserRole role = loan.getUser().getRole();

        List<LoanDetail> details = loanDetailRepository.findByLoan(loan);
        for (LoanDetail detail : details) {
            LocalDate dueDate = ruleEngine.calculateDueDate(role, detail.getBook().getDocType(), approvedDate);
            detail.setDueDate(dueDate);
            loanDetailRepository.save(detail);
        }
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
    public long countPendingLoans() {
        return loanRepository.countByStatus(LoanStatus.PENDING);
    }

    public long countActiveLoans() {
        return loanRepository.countByStatus(LoanStatus.APPROVED);
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
