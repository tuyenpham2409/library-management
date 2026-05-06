package com.library.repository;

import com.library.entity.BorrowingRule;
import com.library.entity.DocType;
import com.library.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BorrowingRuleRepository extends JpaRepository<BorrowingRule, Long> {
    Optional<BorrowingRule> findByUserRoleAndDocType(UserRole userRole, DocType docType);
}
