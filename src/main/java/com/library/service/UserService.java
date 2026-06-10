package com.library.service;

import com.library.entity.CardStatus;
import com.library.entity.User;
import com.library.entity.UserRole;
import com.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Admin tạo tài khoản mới (vd: thủ thư) và gán vai trò ngay trên giao diện. */
    public void create(String studentCode, String fullName, UserRole role, String rawPassword) {
        User user = new User();
        user.setStudentCode(studentCode);
        user.setFullName(fullName);
        user.setRole(role);
        user.setCardStatus(CardStatus.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByStudentCode(String studentCode) {
        return userRepository.findByStudentCode(studentCode);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void toggleCardStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (user.getCardStatus() == CardStatus.ACTIVE) {
            user.setCardStatus(CardStatus.LOCKED);
        } else {
            user.setCardStatus(CardStatus.ACTIVE);
        }
        userRepository.save(user);
    }
}
