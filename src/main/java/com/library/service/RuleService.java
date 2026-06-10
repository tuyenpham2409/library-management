package com.library.service;

import com.library.entity.BorrowingRule;
import com.library.entity.DocType;
import com.library.entity.UserRole;
import com.library.repository.BorrowingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Quản lý "ma trận quy tắc mượn" (vai trò × loại tài liệu).
 * Đây là phần admin chỉnh sửa trực tiếp trên trang cấu hình, không sửa code.
 */
@Service
@Transactional
public class RuleService {

    private final BorrowingRuleRepository ruleRepository;

    public RuleService(BorrowingRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /** Toàn bộ quy tắc, sắp xếp cố định để hiển thị trên trang cấu hình. */
    public List<BorrowingRule> findAllOrdered() {
        return ruleRepository.findAllByOrderByUserRoleAscDocTypeAsc();
    }

    /** Các loại tài liệu mà vai trò này được phép nhìn thấy (visible = true). */
    public Set<DocType> visibleDocTypes(UserRole role) {
        return ruleRepository.findByUserRole(role).stream()
                .filter(r -> Boolean.TRUE.equals(r.getVisible()))
                .map(BorrowingRule::getDocType)
                .collect(Collectors.toSet());
    }

    /**
     * Lưu chỉnh sửa từ trang cấu hình.
     * Mỗi dòng được nhận diện bằng id; chỉ cập nhật các trường cấu hình.
     */
    public void updateRules(List<BorrowingRule> edits) {
        for (BorrowingRule edit : edits) {
            if (edit.getId() == null) continue;
            BorrowingRule rule = ruleRepository.findById(edit.getId()).orElse(null);
            if (rule == null) continue;
            rule.setMaxQuantity(edit.getMaxQuantity());
            rule.setBorrowDays(edit.getBorrowDays());
            rule.setMaxRenewals(edit.getMaxRenewals());
            rule.setRenewalDays(edit.getRenewalDays());
            rule.setVisible(Boolean.TRUE.equals(edit.getVisible()));
            ruleRepository.save(rule);
        }
    }
}
