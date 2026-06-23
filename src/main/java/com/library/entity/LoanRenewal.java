package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_renewals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRenewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_detail_id", nullable = false)
    private LoanDetail loanDetail;

    @Column(name = "renewed_at", nullable = false)
    private LocalDateTime renewedAt;

    @Column(name = "new_due_date")
    private LocalDate newDueDate;
}
