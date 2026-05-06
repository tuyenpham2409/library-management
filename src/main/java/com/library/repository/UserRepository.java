package com.library.repository;

import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentCode(String studentCode);
    boolean existsByStudentCode(String studentCode);
    List<User> findByCardStatus(com.library.entity.CardStatus cardStatus);
}
