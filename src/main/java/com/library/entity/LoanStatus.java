package com.library.entity;

public enum LoanStatus {
    AWAITING_PICKUP, // Đã tạo đơn, có mã mượn, đang chờ SV đến lấy (giữ chỗ sách)
    BORROWED,        // Thủ thư đã giao sách, đang mượn (kể cả đã trả hết nhưng còn phí chưa thu)
    COMPLETED,       // Đã trả hết VÀ thu xong phí phạt (nếu có) → mã mượn hết hiệu lực
    CANCELLED        // Thủ thư huỷ (kèm lý do) / bạn đọc tự huỷ / tự huỷ do quá 24h chưa lấy
}
