package com.library.repository;

import com.library.entity.LoanDetail;
import com.library.entity.LoanDetailStatus;
import com.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {
    List<LoanDetail> findByLoan(Loan loan);
    List<LoanDetail> findByStatus(LoanDetailStatus status);

    @Query("SELECT ld FROM LoanDetail ld WHERE ld.loan.user.id = :userId AND ld.status = 'BORROWING'")
    List<LoanDetail> findActiveBorrowingByUserId(@Param("userId") Long userId);

    @Query("SELECT ld FROM LoanDetail ld WHERE ld.status = 'BORROWING' AND ld.dueDate IS NOT NULL AND ld.dueDate < :today")
    List<LoanDetail> findOverdueDetails(@Param("today") LocalDate today);

    @Query("SELECT ld FROM LoanDetail ld WHERE ld.loan.user.id = :userId AND ld.status = 'BORROWING' AND ld.dueDate IS NOT NULL AND ld.dueDate < :today")
    List<LoanDetail> findOverdueByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

    long countByStatus(LoanDetailStatus status);
}
