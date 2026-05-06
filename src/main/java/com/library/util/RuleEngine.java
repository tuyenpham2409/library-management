package com.library.util;

import com.library.dto.CartItem;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.BorrowingRuleRepository;
import com.library.repository.LoanDetailRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RuleEngine {

    private static final int MAX_CART_TOTAL = 5;
    private static final long FINE_PER_DAY = 2000L; // VND

    private final BorrowingRuleRepository ruleRepository;
    private final LoanDetailRepository loanDetailRepository;

    public RuleEngine(BorrowingRuleRepository ruleRepository,
                      LoanDetailRepository loanDetailRepository) {
        this.ruleRepository = ruleRepository;
        this.loanDetailRepository = loanDetailRepository;
    }

    /**
     * Validate 3 tầng:
     * Tầng 1: Tổng giỏ ≤ 5
     * Tầng 2: Từng loại TL không vượt max_quantity
     * Tầng 3: Tài khoản hợp lệ (không LOCKED, không có OVERDUE)
     */
    public ValidationResult validate(User user, List<CartItem> cart) {
        List<String> errors = new ArrayList<>();

        // Tầng 3a: Kiểm tra card_status
        if (user.getCardStatus() == CardStatus.LOCKED) {
            errors.add("Tài khoản của bạn đang bị khoá. Vui lòng liên hệ thủ thư.");
        }

        // Tầng 3b: Kiểm tra OVERDUE
        List<LoanDetail> overdueDetails = loanDetailRepository
                .findOverdueByUserId(user.getId(), LocalDate.now());
        if (!overdueDetails.isEmpty()) {
            errors.add("Bạn đang có " + overdueDetails.size() +
                       " tài liệu quá hạn. Vui lòng trả sách trước khi mượn thêm.");
        }

        // Tầng 1: Tổng giỏ ≤ 5
        if (cart.isEmpty()) {
            errors.add("Giỏ sách trống. Vui lòng thêm sách trước khi gửi phiếu.");
        } else if (cart.size() > MAX_CART_TOTAL) {
            errors.add("Tổng số tài liệu trong 1 lần mượn không được vượt quá " +
                       MAX_CART_TOTAL + " cuốn (hiện tại: " + cart.size() + ").");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.fail(errors);
        }

        // Tầng 2: Từng loại TL
        Map<DocType, Long> countByType = cart.stream()
                .collect(Collectors.groupingBy(CartItem::getDocType, Collectors.counting()));

        for (Map.Entry<DocType, Long> entry : countByType.entrySet()) {
            DocType docType = entry.getKey();
            long count = entry.getValue();

            // Không mượn RESTRICTED về nhà
            if (docType == DocType.RESTRICTED) {
                errors.add("Tài liệu nội sinh (tem đỏ) chỉ được đọc tại chỗ, không được mượn về.");
                continue;
            }

            Optional<BorrowingRule> ruleOpt = ruleRepository
                    .findByUserRoleAndDocType(user.getRole(), docType);

            if (ruleOpt.isEmpty()) {
                errors.add("Không tìm thấy quy tắc cho loại tài liệu: " + docType);
                continue;
            }

            BorrowingRule rule = ruleOpt.get();
            if (rule.getMaxQuantity() == 0) {
                errors.add("Bạn không có quyền mượn tài liệu loại: " +
                           getDocTypeDisplayName(docType));
                continue;
            }

            if (count > rule.getMaxQuantity()) {
                errors.add(String.format("Vượt quá giới hạn cho loại '%s': tối đa %d cuốn, bạn đang chọn %d cuốn.",
                        getDocTypeDisplayName(docType), rule.getMaxQuantity(), count));
            }
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    /**
     * Tính due_date dựa trên rule.
     * Nếu borrow_days IS NULL → trả về null (không giới hạn).
     */
    public LocalDate calculateDueDate(UserRole role, DocType docType, LocalDate startDate) {
        Optional<BorrowingRule> ruleOpt = ruleRepository.findByUserRoleAndDocType(role, docType);
        if (ruleOpt.isEmpty()) {
            return startDate.plusDays(30); // fallback
        }
        BorrowingRule rule = ruleOpt.get();
        if (rule.getBorrowDays() == null) {
            return null; // không giới hạn
        }
        return startDate.plusDays(rule.getBorrowDays());
    }

    /**
     * Kiểm tra có thể gia hạn không.
     */
    public boolean canRenew(LoanDetail detail, BorrowingRule rule) {
        if (rule == null) return false;
        if (rule.getMaxRenewals() == 0) return false;
        if (detail.getRenewalCount() == null) return true;
        return detail.getRenewalCount() < rule.getMaxRenewals();
    }

    /**
     * Tính tiền phạt quá hạn: 2000 VND/ngày.
     */
    public long calculateFine(LoanDetail detail) {
        if (detail.getDueDate() == null) return 0;
        LocalDate returnDate = detail.getReturnDate() != null
                ? detail.getReturnDate().toLocalDate()
                : LocalDate.now();
        if (!returnDate.isAfter(detail.getDueDate())) return 0;
        long daysOverdue = detail.getDueDate().until(returnDate).getDays();
        return daysOverdue * FINE_PER_DAY;
    }

    private String getDocTypeDisplayName(DocType docType) {
        return switch (docType) {
            case TEXTBOOK -> "Giáo trình";
            case SPECIALIZED_REF -> "Tham khảo chuyên ngành";
            case GENERAL_REF -> "Tham khảo tra cứu";
            case RESTRICTED -> "Tài liệu nội sinh";
        };
    }
}
