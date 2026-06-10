package com.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    // Mã mượn sinh ra khi tạo đơn thành công (null nếu lỗi)
    private String pickupCode;

    public static ValidationResult ok() {
        return new ValidationResult(true, List.of(), null);
    }

    public static ValidationResult ok(String pickupCode) {
        return new ValidationResult(true, List.of(), pickupCode);
    }

    public static ValidationResult fail(List<String> errors) {
        return new ValidationResult(false, errors, null);
    }

    public static ValidationResult fail(String error) {
        return new ValidationResult(false, List.of(error), null);
    }
}
