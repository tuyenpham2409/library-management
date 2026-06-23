package com.library.repository;

import com.library.entity.LoanRenewal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRenewalRepository extends JpaRepository<LoanRenewal, Long> {
}
