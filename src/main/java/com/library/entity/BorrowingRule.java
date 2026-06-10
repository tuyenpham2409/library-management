package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "borrowing_rules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_role", "doc_type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 20)
    private DocType docType;

    @Column(name = "max_quantity", nullable = false)
    private Integer maxQuantity;

    /**
     * Số ngày mượn. NULL = không giới hạn (chỉ dành cho LECTURER).
     */
    @Column(name = "borrow_days")
    private Integer borrowDays;

    @Column(name = "max_renewals", nullable = false)
    private Integer maxRenewals;

    /**
     * Số ngày gia hạn mỗi lần. NULL nếu maxRenewals = 0.
     */
    @Column(name = "renewal_days")
    private Integer renewalDays;

    /**
     * Có cho vai trò này nhìn thấy/mượn loại tài liệu này không.
     * Admin bật/tắt trực tiếp trên trang cấu hình (không sửa code).
     */
    @Column(name = "visible", nullable = false)
    private Boolean visible = true;
}
