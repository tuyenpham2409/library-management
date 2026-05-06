package com.library.repository;

import com.library.entity.Loan;
import com.library.entity.LoanStatus;
import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserOrderByCreatedAtDesc(User user);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserAndStatus(User user, LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.user = :user AND l.status IN ('PENDING', 'APPROVED')")
    List<Loan> findActiveLoans(@Param("user") User user);

    long countByStatus(LoanStatus status);
}
