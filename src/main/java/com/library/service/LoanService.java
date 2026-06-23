package com.library.service;

import com.library.dto.CartItem;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.*;
import com.library.util.RuleEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.dto.ReturnBatchItem;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final BookRepository bookRepository;
    private final BorrowingRuleRepository ruleRepository;
    private final LoanRenewalRepository loanRenewalRepository;
    private final RuleEngine ruleEngine;
    private final NotificationService notificationService;

    public LoanService(LoanRepository loanRepository,
                       LoanDetailRepository loanDetailRepository,
                       BookRepository bookRepository,
                       BorrowingRuleRepository ruleRepository,
                       LoanRenewalRepository loanRenewalRepository,
                       RuleEngine ruleEngine,
                       NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.loanDetailRepository = loanDetailRepository;
        this.bookRepository = bookRepository;
        this.ruleRepository = ruleRepository;
        this.loanRenewalRepository = loanRenewalRepository;
        this.ruleEngine = ruleEngine;
        this.notificationService = notificationService;
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // bỏ ký tự dễ nhầm (0/O, 1/I)
    private static final int PICKUP_HOURS = 24; // Hạn đến lấy sách
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            detail.setFinePaid(false);
            details.add(detail);

            // Giữ chỗ: giảm available_copies ngay khi tạo đơn
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }
        loanDetailRepository.saveAll(details);

        // Thông báo
        notificationService.notify(user, NotificationType.SUCCESS,
                "Tạo đơn mượn #" + loan.getId() + " thành công. Mã mượn: " + loan.getPickupCode()
                        + ". Vui lòng đến thư viện lấy sách trong vòng 24 giờ.",
                "/client/my-loans");
        notificationService.notifyRole(UserRole.LIBRARIAN, NotificationType.INFO,
                "Đơn mượn mới #" + loan.getId() + " từ " + user.getFullName()
                        + " (" + user.getStudentCode() + ") — " + books.size() + " cuốn.",
                "/librarian/loans?status=AWAITING_PICKUP");

        return ValidationResult.ok(loan.getPickupCode());
    }

    /**
     * Tìm đơn đang chờ lấy theo mã mượn — thủ thư tra cứu trước khi xác nhận bàn giao.
     */
    public Optional<Loan> findByPickupCode(String code) {
        if (code == null || code.trim().isEmpty()) return Optional.empty();
        return loanRepository.findByPickupCodeIgnoreCaseAndStatus(code.trim(), LoanStatus.AWAITING_PICKUP);
    }

    /**
     * Thủ thư xác nhận bàn giao sách: tìm đơn theo mã, chuyển sang ĐANG MƯỢN và tính hạn trả.
     * @return true nếu xác nhận thành công.
     */
    public boolean confirmPickupByLibrarian(String code) {
        if (code == null || code.trim().isEmpty()) return false;
        Loan loan = loanRepository.findByPickupCodeIgnoreCaseAndStatus(code.trim(), LoanStatus.AWAITING_PICKUP)
                .orElse(null);
        if (loan == null) return false;

        loan.setStatus(LoanStatus.BORROWED);
        loan.setPickedUpAt(LocalDateTime.now());
        loanRepository.save(loan);

        User user = loan.getUser();
        LocalDate pickupDate = LocalDate.now();
        UserRole role = user.getRole();
        for (LoanDetail detail : loanDetailRepository.findByLoan(loan)) {
            detail.setDueDate(ruleEngine.calculateDueDate(role, detail.getBook().getDocType(), pickupDate));
            detail.setStatus(LoanDetailStatus.BORROWING);
            loanDetailRepository.save(detail);
        }

        notificationService.notify(user, NotificationType.SUCCESS,
                "Thủ thư đã xác nhận bàn giao sách cho đơn #" + loan.getId()
                        + ". Đơn chuyển sang Đang mượn.",
                "/client/my-loans");
        return true;
    }

    /**
     * Thủ thư huỷ đơn (bắt buộc có lý do, gửi về cho bạn đọc).
     * Trả lại available_copies cho từng cuốn.
     */
    public void cancelLoan(Long loanId, String reason, UserRole byRole) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        if (loan.getStatus() != LoanStatus.AWAITING_PICKUP) {
            throw new RuntimeException("Chỉ có thể huỷ đơn đang ở trạng thái chờ lấy sách.");
        }
        restoreCopiesAndMarkCancelled(loan, reason, byRole);

        notificationService.notify(loan.getUser(), NotificationType.DANGER,
                "Đơn mượn #" + loan.getId() + " đã bị huỷ bởi thủ thư. Lý do: " + reason,
                "/client/my-loans");
    }

    /**
     * Bạn đọc tự huỷ đơn — chỉ được khi đơn còn ở trạng thái chờ lấy (chưa nhận sách).
     * @return true nếu huỷ thành công.
     */
    public boolean cancelByReader(Long loanId, User user) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan == null) return false;
        if (!loan.getUser().getId().equals(user.getId())) return false;
        if (loan.getStatus() != LoanStatus.AWAITING_PICKUP) return false;

        restoreCopiesAndMarkCancelled(loan, "Bạn đọc tự huỷ", user.getRole());

        notificationService.notifyRole(UserRole.LIBRARIAN, NotificationType.WARNING,
                user.getFullName() + " (" + user.getStudentCode() + ") đã tự huỷ đơn #" + loan.getId() + ".",
                "/librarian/loans?status=CANCELLED");
        return true;
    }

    private void restoreCopiesAndMarkCancelled(Loan loan, String reason, UserRole byRole) {
        List<LoanDetail> details = loanDetailRepository.findByLoan(loan);
        for (LoanDetail d : details) {
            Book book = d.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }
        loan.setStatus(LoanStatus.CANCELLED);
        loan.setCancelReason(reason);
        loan.setCancelledByRole(byRole);
        loan.setCancelledAt(LocalDateTime.now());
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
            restoreCopiesAndMarkCancelled(loan, "Tự huỷ do quá 24h chưa đến lấy", null);
            notificationService.notify(loan.getUser(), NotificationType.WARNING,
                    "Đơn mượn #" + loan.getId() + " đã tự huỷ do quá 24 giờ chưa đến lấy sách.",
                    "/client/my-loans");
            notificationService.notifyRole(UserRole.LIBRARIAN, NotificationType.INFO,
                    "Đơn #" + loan.getId() + " tự huỷ do quá hạn lấy — vui lòng xếp lại kho.",
                    "/librarian/loans?status=CANCELLED");
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
     * Thủ thư xác nhận trả sách từng cuốn. Tính phí phạt; nếu không có phí thì coi như đã thu.
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
        detail.setFinePaid(fineAmount == 0); // không phí → coi như đã giải quyết
        loanDetailRepository.save(detail);

        // Tăng available_copies
        Book book = detail.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Loan loan = detail.getLoan();
        if (fineAmount > 0) {
            notificationService.notify(loan.getUser(), NotificationType.WARNING,
                    "Đã trả '" + book.getTitle() + "' (đơn #" + loan.getId() + "). Phí phạt "
                            + String.format("%,d", fineAmount) + " VND — vui lòng thanh toán tại quầy.",
                    "/client/my-loans");
        } else {
            notificationService.notify(loan.getUser(), NotificationType.SUCCESS,
                    "Đã trả '" + book.getTitle() + "' (đơn #" + loan.getId() + ").",
                    "/client/my-loans");
        }

        refreshCompletion(loan);
        return BigDecimal.valueOf(fineAmount);
    }

    /**
     * Thủ thư xác nhận đã thu tiền phạt của một cuốn.
     * @return true nếu cập nhật thành công.
     */
    public boolean payFine(Long loanDetailId) {
        LoanDetail detail = loanDetailRepository.findById(loanDetailId)
                .orElseThrow(() -> new RuntimeException("LoanDetail not found: " + loanDetailId));
        if (!detail.hasFine() || Boolean.TRUE.equals(detail.getFinePaid())) {
            return false;
        }
        detail.setFinePaid(true);
        detail.setFinePaidAt(LocalDateTime.now());
        loanDetailRepository.save(detail);

        Loan loan = detail.getLoan();
        notificationService.notify(loan.getUser(), NotificationType.SUCCESS,
                "Đã thu phí " + String.format("%,d", detail.getFineAmount().longValue())
                        + " VND cho '" + detail.getBook().getTitle() + "' (đơn #" + loan.getId() + ").",
                "/client/my-loans");
        refreshCompletion(loan);
        return true;
    }

    /**
     * Đơn HOÀN THÀNH khi: tất cả cuốn đã RETURNED VÀ tất cả phí đã thu (finePaid).
     */
    private void refreshCompletion(Loan loan) {
        if (loan.getStatus() != LoanStatus.BORROWED) return;
        List<LoanDetail> all = loanDetailRepository.findByLoan(loan);
        boolean allReturned = all.stream().allMatch(d -> d.getStatus() == LoanDetailStatus.RETURNED);
        boolean allSettled  = all.stream().allMatch(LoanDetail::isFineSettled);
        if (allReturned && allSettled) {
            loan.setStatus(LoanStatus.COMPLETED);
            loan.setCompletedAt(LocalDateTime.now());
            loanRepository.save(loan);
            notificationService.notify(loan.getUser(), NotificationType.SUCCESS,
                    "Đơn mượn #" + loan.getId() + " đã hoàn thành. Cảm ơn bạn!",
                    "/client/my-loans");
        }
    }

    /**
     * Trả nhiều quyển cùng lúc (batch). Chỉ xử lý các detail đang ở BORROWING.
     * Trả về danh sách kết quả per-detail để frontend cập nhật UI không cần reload.
     */
    @Transactional
    public List<Map<String, Object>> returnBatch(Long loanId, List<ReturnBatchItem> items) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        List<Map<String, Object>> results = new ArrayList<>();

        for (ReturnBatchItem item : items) {
            LoanDetail detail = loanDetailRepository.findById(item.getDetailId()).orElse(null);
            if (detail == null || !detail.getLoan().getId().equals(loanId)) continue;
            if (detail.getStatus() != LoanDetailStatus.BORROWING) continue;

            BookCondition cond = item.getCondition() != null ? item.getCondition() : BookCondition.GOOD;
            detail.setReturnDate(LocalDateTime.now());
            detail.setConditionStatus(cond);
            detail.setStatus(LoanDetailStatus.RETURNED);

            long fineAmount = ruleEngine.calculateFine(detail);
            detail.setFineAmount(BigDecimal.valueOf(fineAmount));
            detail.setFinePaid(fineAmount == 0);
            loanDetailRepository.save(detail);

            Book book = detail.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);

            if (fineAmount > 0) {
                notificationService.notify(loan.getUser(), NotificationType.WARNING,
                        "Đã trả '" + book.getTitle() + "' (đơn #" + loan.getId() + "). Phí phạt "
                                + String.format("%,d", fineAmount) + " VND — vui lòng thanh toán tại quầy.",
                        "/client/my-loans");
            } else {
                notificationService.notify(loan.getUser(), NotificationType.SUCCESS,
                        "Đã trả '" + book.getTitle() + "' (đơn #" + loan.getId() + ").",
                        "/client/my-loans");
            }

            Map<String, Object> r = new HashMap<>();
            r.put("detailId", detail.getId());
            r.put("fine", fineAmount);
            r.put("fineFormatted", fineAmount > 0 ? String.format("%,d đ", fineAmount) : "--");
            r.put("finePaid", fineAmount == 0);
            r.put("condition", cond.name());
            r.put("bookTitle", book.getTitle());
            r.put("returnDateFormatted", detail.getReturnDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            r.put("conditionDamaged", cond == BookCondition.DAMAGED);
            results.add(r);
        }

        refreshCompletion(loan);
        return results;
    }

    /**
     * Gia hạn sách.
     * @return null nếu thành công; chuỗi thông báo lỗi đầy đủ nếu thất bại.
     */
    public String renewBook(Long loanDetailId, User user) {
        LoanDetail detail = loanDetailRepository.findById(loanDetailId).orElse(null);
        if (detail == null) {
            return "Không tìm thấy thông tin mượn (id=" + loanDetailId + ").";
        }

        Book book = detail.getBook();
        String title = book.getTitle();

        if (!detail.getLoan().getUser().getId().equals(user.getId())) {
            return deny(title, "bạn không có quyền gia hạn sách này");
        }

        if (detail.getStatus() != LoanDetailStatus.BORROWING) {
            String statusReason = switch (detail.getStatus()) {
                case RESERVED -> "sách chưa được bàn giao, chưa thể gia hạn";
                case RETURNED -> "sách đã được trả";
                default       -> "trạng thái mượn không hợp lệ (" + detail.getStatus() + ")";
            };
            return deny(title, statusReason);
        }

        Optional<BorrowingRule> ruleOpt = ruleRepository
                .findByUserRoleAndDocType(user.getRole(), book.getDocType());
        BorrowingRule rule = ruleOpt.orElse(null);

        String denyReason = ruleEngine.getRenewalDenyReason(detail, rule);
        if (denyReason != null) return deny(title, denyReason);

        if (rule.getRenewalDays() == null || rule.getRenewalDays() == 0) {
            return deny(title, "số ngày gia hạn chưa được cấu hình");
        }

        LocalDate newDueDate = (detail.getDueDate() != null)
                ? detail.getDueDate().plusDays(rule.getRenewalDays())
                : LocalDate.now().plusDays(rule.getRenewalDays());

        detail.setDueDate(newDueDate);
        detail.setRenewalCount(detail.getRenewalCount() == null ? 1 : detail.getRenewalCount() + 1);
        loanDetailRepository.save(detail);

        LoanRenewal renewal = new LoanRenewal();
        renewal.setLoanDetail(detail);
        renewal.setRenewedAt(LocalDateTime.now());
        renewal.setNewDueDate(newDueDate);
        loanRenewalRepository.save(renewal);

        notificationService.notify(user, NotificationType.SUCCESS,
                "Gia hạn '" + title + "' thành công. Hạn trả mới: "
                        + newDueDate.format(DATE_FMT) + ".",
                "/client/my-loans");
        return null;
    }

    private static String deny(String bookTitle, String reason) {
        return "Không thể gia hạn quyển \"" + bookTitle + "\": " + reason + ".";
    }

    /**
     * Thủ thư lập phiếu cho mượn ĐỌC TẠI CHỖ — chỉ áp dụng cho tài liệu nội sinh (RESTRICTED).
     * Tạo đơn BORROWED (inHouse), hạn trả trong ngày, không qua kiểm tra quota.
     */
    public ValidationResult lendInHouse(User reader, Long bookId) {
        if (reader.getCardStatus() == CardStatus.LOCKED) {
            return ValidationResult.fail("Thẻ bạn đọc đang bị khoá, không thể cho mượn.");
        }
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return ValidationResult.fail("Không tìm thấy tài liệu.");
        if (book.getDocType() != DocType.RESTRICTED) {
            return ValidationResult.fail("Chỉ tài liệu nội sinh mới áp dụng đọc tại chỗ.");
        }
        if (!book.isAvailable()) {
            return ValidationResult.fail("Tài liệu '" + book.getTitle() + "' hiện không còn sẵn.");
        }

        Loan loan = new Loan();
        loan.setUser(reader);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setInHouse(true);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setPickedUpAt(LocalDateTime.now());
        loan = loanRepository.save(loan);

        LoanDetail d = new LoanDetail();
        d.setLoan(loan);
        d.setBook(book);
        d.setStatus(LoanDetailStatus.BORROWING);
        d.setDueDate(LocalDate.now());
        d.setRenewalCount(0);
        d.setFineAmount(BigDecimal.ZERO);
        d.setFinePaid(false);
        loanDetailRepository.save(d);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        notificationService.notify(reader, NotificationType.INFO,
                "Bạn đang đọc tại chỗ '" + book.getTitle() + "' (ghi nhận ngày "
                        + LocalDate.now().format(DATE_FMT) + "). Vui lòng trả tại quầy trước khi ra về.",
                "/client/my-loans");
        return ValidationResult.ok();
    }

    public List<Loan> findInHouseActive() {
        return loanRepository.findByInHouseTrueAndStatusOrderByCreatedAtDesc(LoanStatus.BORROWED);
    }

    /** Báo nhắc hạn trả (scheduler gọi 1 lần/ngày). Trả về số thông báo đã gửi. */
    public int sendDueReminders() {
        int count = 0;
        for (LoanDetail d : loanDetailRepository.findByStatus(LoanDetailStatus.BORROWING)) {
            if (d.getDueDate() == null) continue;
            User u = d.getLoan().getUser();
            if (d.isOverdue()) {
                notificationService.notify(u, NotificationType.DANGER,
                        "Sách '" + d.getBook().getTitle() + "' đã quá hạn " + d.getDaysOverdue()
                                + " ngày. Vui lòng trả sớm để tránh phí phạt tăng thêm.",
                        "/client/my-loans");
                count++;
            } else if (d.isDueSoon()) {
                notificationService.notify(u, NotificationType.WARNING,
                        "Sách '" + d.getBook().getTitle() + "' sắp đến hạn trả (còn "
                                + d.getDaysRemaining() + " ngày).",
                        "/client/my-loans");
                count++;
            }
        }
        return count;
    }

    // Queries
    public List<Loan> findAllByUser(User user) {
        return loanRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Loan> findAll() {
        return loanRepository.findAllByOrderByCreatedAtDesc();
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
