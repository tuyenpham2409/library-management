package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Mã mượn để SV đọc cho thủ thư khi đến lấy sách
    @Column(name = "pickup_code", length = 10)
    private String pickupCode;

    // Hạn lấy sách = lúc tạo đơn + 24h. Quá hạn mà chưa lấy thì đơn tự huỷ.
    @Column(name = "pickup_deadline")
    private LocalDateTime pickupDeadline;

    // Thời điểm thủ thư xác nhận giao sách
    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status = LoanStatus.AWAITING_PICKUP;

    // Phiếu đọc tại chỗ (thủ thư lập cho tài liệu nội sinh) — không mang về, không có mã mượn.
    @Column(name = "in_house", nullable = false)
    private boolean inHouse = false;

    // Lý do huỷ đơn (thủ thư huỷ phải có lý do; bạn đọc tự huỷ / tự huỷ quá hạn ghi mặc định)
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // Ai huỷ đơn: vai trò người thực hiện (null nếu chưa huỷ)
    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by_role", length = 20)
    private UserRole cancelledByRole;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanDetail> loanDetails;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
