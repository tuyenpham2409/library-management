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

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanDetail> loanDetails;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
