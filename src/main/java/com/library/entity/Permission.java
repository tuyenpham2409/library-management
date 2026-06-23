package com.library.entity;

/**
 * Các quyền chức năng (chi tiết theo hành động) mà Admin bật/tắt cho từng vai trò
 * trên trang Phân quyền. Mỗi quyền thuộc một nhóm (Category) để hiển thị cho gọn.
 *
 * Lưu ý: MANAGE_USERS / MANAGE_PERMISSIONS là quyền cố định của ADMIN, KHÔNG đưa vào đây
 * để admin không thể tự khoá quyền quản trị của mình.
 */
public enum Permission {
    // Nhóm bạn đọc
    BORROW("Mượn / tạo đơn", Category.READER),
    RENEW("Gia hạn", Category.READER),
    CANCEL_OWN("Tự huỷ đơn chờ lấy", Category.READER),

    // Nhóm quản lý sách
    BOOK_CREATE("Thêm sách", Category.BOOK),
    BOOK_UPDATE("Sửa sách", Category.BOOK),
    BOOK_DELETE("Xoá sách", Category.BOOK),

    // Nhóm xử lý đơn mượn
    LOAN_CONFIRM_PICKUP("Xác nhận nhận sách (bàn giao)", Category.LOAN),
    LOAN_RETURN("Trả sách", Category.LOAN),
    LOAN_PAY_FINE("Thu phí phạt", Category.LOAN),
    LOAN_CANCEL("Huỷ đơn (kèm lý do)", Category.LOAN),
    LOAN_LEND_INHOUSE("Cho mượn đọc tại chỗ", Category.LOAN),

    // Nhóm quy tắc mượn
    RULE_MANAGE("Sửa quy tắc mượn", Category.RULE);

    /** Nhóm quyền — dùng để gom hiển thị trên giao diện ("loại quyền"). */
    public enum Category {
        READER("Mượn & trả (bạn đọc)"),
        BOOK("Quản lý sách"),
        LOAN("Xử lý đơn mượn"),
        RULE("Quy tắc mượn");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final String label;
    private final Category category;

    Permission(String label, Category category) {
        this.label = label;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public Category getCategory() {
        return category;
    }
}
