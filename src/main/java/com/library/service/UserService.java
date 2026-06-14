package com.library.service;

import com.library.entity.CardStatus;
import com.library.entity.NotificationType;
import com.library.entity.User;
import com.library.entity.UserRole;
import com.library.repository.LoanRepository;
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
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository,
                       LoanRepository loanRepository,
                       PasswordEncoder passwordEncoder,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
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

    /** Tìm/lọc người dùng theo từ khoá (mã số hoặc họ tên) và/hoặc vai trò. */
    @Transactional(readOnly = true)
    public List<User> search(String keyword, UserRole role) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        return userRepository.findAll().stream()
                .filter(u -> kw.isEmpty()
                        || u.getStudentCode().toLowerCase().contains(kw)
                        || u.getFullName().toLowerCase().contains(kw))
                .filter(u -> role == null || u.getRole() == role)
                .toList();
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

    /** Admin cập nhật họ tên + vai trò của user. Đổi vai trò sẽ thông báo cho user. */
    @Transactional
    public void update(Long id, String fullName, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        UserRole oldRole = user.getRole();
        user.setFullName(fullName);
        user.setRole(role);
        userRepository.save(user);
        if (oldRole != role) {
            notificationService.notify(user, NotificationType.INFO,
                    "Vai trò của bạn đã được thay đổi từ " + oldRole + " thành " + role + ".",
                    null);
        }
    }

    /** Admin đặt lại mật khẩu cho user. */
    @Transactional
    public void resetPassword(Long id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        notificationService.notify(user, NotificationType.WARNING,
                "Mật khẩu của bạn vừa được quản trị viên đặt lại. Vui lòng đổi lại mật khẩu sau khi đăng nhập.",
                "/account/password");
    }

    /**
     * Admin xoá user. Chặn nếu user còn lịch sử mượn (giữ toàn vẹn dữ liệu) —
     * khuyến nghị dùng khoá thẻ thay vì xoá.
     */
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        if (!loanRepository.findByUserOrderByCreatedAtDesc(user).isEmpty()) {
            throw new IllegalStateException(
                    "Không thể xoá: tài khoản này còn lịch sử mượn. Hãy khoá thẻ thay vì xoá.");
        }
        userRepository.delete(user);
    }

    /**
     * Người dùng tự đổi mật khẩu sau khi đăng nhập bằng mật khẩu mặc định.
     * Phải nhập đúng mật khẩu hiện tại mới đổi được.
     * @return true nếu đổi thành công.
     */
    public boolean changePassword(String studentCode, String currentRaw, String newRaw) {
        User user = userRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("User not found: " + studentCode));
        if (!passwordEncoder.matches(currentRaw, user.getPasswordHash())) {
            return false;
        }
        user.setPasswordHash(passwordEncoder.encode(newRaw));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void toggleCardStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (user.getCardStatus() == CardStatus.ACTIVE) {
            user.setCardStatus(CardStatus.LOCKED);
            userRepository.save(user);
            notificationService.notify(user, NotificationType.DANGER,
                    "Thẻ thư viện của bạn đã bị khoá. Vui lòng liên hệ quản trị viên.", null);
        } else {
            user.setCardStatus(CardStatus.ACTIVE);
            userRepository.save(user);
            notificationService.notify(user, NotificationType.SUCCESS,
                    "Thẻ thư viện của bạn đã được mở khoá.", null);
        }
    }
}
