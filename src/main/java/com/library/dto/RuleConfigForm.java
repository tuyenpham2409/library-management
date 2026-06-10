package com.library.dto;

import com.library.entity.BorrowingRule;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Bọc danh sách quy tắc để form trang cấu hình bind theo chỉ số (rules[0], rules[1]...).
 */
@Data
public class RuleConfigForm {
    private List<BorrowingRule> rules = new ArrayList<>();
}
