package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loan_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "renewal_count")
    private Integer renewalCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", length = 10)
    private BookCondition conditionStatus = BookCondition.GOOD;

    @Column(name = "fine_amount", precision = 10, scale = 0)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanDetailStatus status = LoanDetailStatus.BORROWING;

    // Helper methods
    public boolean isOverdue() {
        if (dueDate == null || status != LoanDetailStatus.BORROWING) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public long getDaysRemaining() {
        if (dueDate == null) return Long.MAX_VALUE; // unlimited
        if (status != LoanDetailStatus.BORROWING) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public boolean isDueSoon() {
        long remaining = getDaysRemaining();
        return remaining >= 0 && remaining <= 3;
    }
}
