#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Build the all-Vietnamese report BaoCao_QuanLyThuVien_v3.2_vi.docx from v3.2 by
translating every English paragraph / table cell to Vietnamese. The flowchart
section (§10) and the embedded diagrams are already Vietnamese and stay as-is.
Code identifiers, routes, SQL types, enum constants, URLs and proper nouns are kept."""
from docx import Document

SRC = "BaoCao_QuanLyThuVien_v3.2.docx"
DST = "BaoCao_QuanLyThuVien_v3.2_vi.docx"

T = {
 # ---------- title ----------
 "PROJECT REPORT": "BÁO CÁO ĐỒ ÁN",
 "LIBRARY MANAGEMENT SYSTEM": "HỆ THỐNG QUẢN LÝ THƯ VIỆN",
 "Digitizing the Document Borrowing Process at Phenikaa University Library":
   "Số hoá quy trình mượn tài liệu tại Thư viện Trường Đại học Phenikaa",
 # ---------- table 0 ----------
 "Academic year": "Năm học", "Submission date": "Ngày nộp", "June 2026": "Tháng 6/2026",
 "Supervisor": "Giảng viên hướng dẫn", "Team member": "Thành viên nhóm", "Technology": "Công nghệ",
 # ---------- headings ----------
 "1.  Introduction & Project Overview": "1.  Giới thiệu & Tổng quan dự án",
 "1.1  Background": "1.1  Bối cảnh",
 "1.2  Project Objectives": "1.2  Mục tiêu dự án",
 "1.3  Technology Stack": "1.3  Công nghệ sử dụng",
 "1.4  Summary of Changes in This Version": "1.4  Tóm tắt thay đổi trong phiên bản này",
 "2.  Requirements Analysis": "2.  Phân tích yêu cầu",
 "2.1  Stakeholders": "2.1  Các bên liên quan",
 "2.2  Functional Requirements": "2.2  Yêu cầu chức năng",
 "2.3  Non-Functional Requirements": "2.3  Yêu cầu phi chức năng",
 "3.  Business Process Specification": "3.  Đặc tả quy trình nghiệp vụ",
 "3.1  Revised Borrowing Process (Self-Service)": "3.1  Quy trình mượn cải tiến (tự phục vụ)",
 "3.2  Borrowing Rules by Reader Type": "3.2  Quy tắc mượn theo đối tượng bạn đọc",
 "3.3  Validation Rules (RuleEngine.validate)": "3.3  Quy tắc kiểm tra (RuleEngine.validate)",
 "4.  System Architecture": "4.  Kiến trúc hệ thống",
 "4.1  Architectural Pattern — MVC": "4.1  Mẫu kiến trúc — MVC",
 "4.2  Security Architecture (Role Separation)": "4.2  Kiến trúc bảo mật (tách vai trò)",
 "4.3  Session Management": "4.3  Quản lý phiên",
 "4.4  Transaction Management": "4.4  Quản lý giao dịch",
 "4.5  Scheduling": "4.5  Lập lịch",
 "4.6  Request Flow — Self-Service Submit & Pickup": "4.6  Luồng xử lý — Gửi phiếu & nhận sách tự phục vụ",
 "5.  Database Design": "5.  Thiết kế cơ sở dữ liệu",
 "5.1  Entity-Relationship Overview": "5.1  Tổng quan thực thể – quan hệ",
 "5.2  Table Definitions": "5.2  Định nghĩa bảng",
 "6.  Feature Implementation": "6.  Hiện thực tính năng",
 "6.1  Feature List": "6.1  Danh sách tính năng",
 "6.2  Key Business Logic Components": "6.2  Các thành phần logic nghiệp vụ chính",
 "6.3  Changes & Fixes Applied in This Version": "6.3  Các thay đổi & sửa lỗi trong phiên bản này",
 "7.  Task Assignment": "7.  Phân công công việc",
 "8.  Installation & Setup Guide": "8.  Hướng dẫn cài đặt & thiết lập",
 "8.1  Prerequisites": "8.1  Yêu cầu tiên quyết",
 "8.2  Run in Development (H2)": "8.2  Chạy ở môi trường phát triển (H2)",
 "8.3  Sample Accounts (password: password123)": "8.3  Tài khoản mẫu (mật khẩu: password123)",
 "8.4  How to Test the New Flow": "8.4  Cách kiểm thử luồng mới",
 "9.  Testing & Validation": "9.  Kiểm thử & Đánh giá",
 "9.1  Functional Test Scenarios (verified)": "9.1  Kịch bản kiểm thử chức năng (đã xác minh)",
 "9.2  Build & Runtime Verification": "9.2  Kiểm tra build & lúc chạy",
 "10.  Business Process: Flowchart & Walkthrough": "10.  Quy trình nghiệp vụ: Lưu đồ & Diễn giải",
 "10.1  Original Process — Phenikaa University Library (QT.03.TV.DV)": "10.1  Quy trình gốc — Thư viện Trường Đại học Phenikaa (QT.03.TV.DV)",
 "10.2  Our System — Digital Self-Service Process": "10.2  Hệ thống của chúng tôi — Quy trình số tự phục vụ",
 "10.3  Comparison — Manual Process vs. Our System": "10.3  So sánh — Quy trình thủ công với hệ thống của chúng tôi",
 "11.  System Design Models (UML)": "11.  Các mô hình thiết kế hệ thống (UML)",
 "11.1  Use Case Diagram": "11.1  Sơ đồ Use Case",
 "11.2  Class Diagram — Domain Model": "11.2  Sơ đồ lớp — Mô hình miền",
 "11.3  Activity Diagram — Self-Service Borrowing": "11.3  Sơ đồ hoạt động — Mượn sách tự phục vụ",
 "11.4  State Machine Diagram — Loan Lifecycle": "11.4  Sơ đồ trạng thái — Vòng đời đơn mượn",
 "12.  Conclusion & Future Work": "12.  Kết luận & Hướng phát triển",
 "12.1  Achievements": "12.1  Kết quả đạt được",
 "12.2  Limitations": "12.2  Hạn chế",
 "12.3  Future Development Roadmap": "12.3  Lộ trình phát triển",
 "Reader (client) features": "Tính năng bạn đọc (client)",
 "Librarian features (operations)": "Tính năng thủ thư (nghiệp vụ)",
 "Administrator features (users & system)": "Tính năng quản trị viên (người dùng & hệ thống)",
 "Table: users": "Bảng: users", "Table: books": "Bảng: books", "Table: loans": "Bảng: loans",
 "Table: loan_details": "Bảng: loan_details", "Table: borrowing_rules": "Bảng: borrowing_rules",
 # ---------- §1.1 ----------
 "Phenikaa University Library manages the borrowing and returning of academic documents for thousands of students, lecturers, and researchers every semester. The traditional paper-based workflow — physical card presentation, handwritten loan slips, and manual ledger entries — is time-consuming and error-prone.":
   "Thư viện Trường Đại học Phenikaa quản lý việc mượn và trả tài liệu học thuật cho hàng nghìn sinh viên, giảng viên và nghiên cứu viên mỗi học kỳ. Quy trình thủ công truyền thống — xuất trình thẻ, viết phiếu mượn bằng tay và ghi sổ thủ công — vừa tốn thời gian vừa dễ sai sót.",
 "The first version of this project digitised that workflow but still imitated the old counter model: every loan request had to be manually approved by a librarian before the reader could borrow. Following the supervisor's review, the system has been redesigned to be genuinely more convenient than traditional borrowing:":
   "Phiên bản đầu của đồ án đã số hoá quy trình đó nhưng vẫn mô phỏng mô hình quầy cũ: mỗi phiếu mượn phải được thủ thư duyệt thủ công trước khi bạn đọc được mượn. Sau khi giảng viên hướng dẫn góp ý, hệ thống được thiết kế lại để thực sự thuận tiện hơn cách mượn truyền thống:",
 "No librarian/admin approval is required. If the reader satisfies the borrowing rules, the loan is granted immediately.":
   "Không cần thủ thư/quản trị viên phê duyệt. Nếu bạn đọc thoả mãn các quy tắc mượn, đơn mượn được cấp ngay lập tức.",
 "Each loan request generates a unique pickup code (mã mượn). Librarians simply prepare the books for that code.":
   "Mỗi phiếu mượn sinh ra một mã lấy sách (mã mượn) duy nhất. Thủ thư chỉ việc chuẩn bị sách theo mã đó.",
 "The reader picks up the books within 24 hours and self-confirms receipt online by re-entering the pickup code; otherwise the request auto-cancels and the held copies are released.":
   "Bạn đọc đến lấy sách trong vòng 24 giờ và tự xác nhận đã nhận trực tuyến bằng cách nhập lại mã lấy sách; nếu không, phiếu tự huỷ và số lượng đang giữ được hoàn lại.",
 "The roles of Administrator and Librarian are now clearly separated. The administrator manages user accounts and a functional-permission matrix (which actions each role may perform), while the librarian configures the borrowing rules and shows/hides document types per role from a configuration screen — all without editing any code.":
   "Vai trò Quản trị viên và Thủ thư nay được tách bạch rõ ràng. Quản trị viên quản lý tài khoản người dùng và ma trận phân quyền chức năng (mỗi vai trò được thực hiện thao tác nào), còn thủ thư cấu hình quy tắc mượn và bật/tắt hiển thị loại tài liệu theo vai trò ngay trên màn hình cấu hình — tất cả mà không cần sửa mã nguồn.",
 # ---------- §1.2 ----------
 "Provide a self-service, cart-based borrowing experience with no manual approval step.":
   "Cung cấp trải nghiệm mượn tự phục vụ dựa trên giỏ sách, không có bước phê duyệt thủ công.",
 "Issue a pickup code per request, hold the copies, and auto-cancel un-collected requests after 24 hours.":
   "Cấp mã lấy sách cho mỗi phiếu, giữ chỗ số lượng, và tự huỷ phiếu không lấy sau 24 giờ.",
 "Let readers self-confirm pickup by re-entering their pickup code (ownership + code verified).":
   "Cho bạn đọc tự xác nhận nhận sách bằng cách nhập lại mã lấy sách (kiểm tra quyền sở hữu + mã).",
 "Enforce role-based borrowing rules (quota, duration, renewals, late fines) for Students, Lecturers, and Researchers.":
   "Áp dụng quy tắc mượn theo vai trò (hạn mức, thời hạn, gia hạn, phí trễ) cho Sinh viên, Giảng viên và Nghiên cứu viên.",
 "Separate the Administrator (user accounts + functional-permission matrix) from the Librarian (day-to-day operations + borrowing-rule configuration).":
   "Tách Quản trị viên (tài khoản người dùng + ma trận phân quyền chức năng) khỏi Thủ thư (nghiệp vụ hằng ngày + cấu hình quy tắc mượn).",
 "Offer on-screen configuration with no code change and no redeploy: the librarian edits borrowing rules and document-type visibility per role, and the administrator grants or revokes functional permissions per role.":
   "Cho phép cấu hình ngay trên giao diện, không sửa mã và không triển khai lại: thủ thư sửa quy tắc mượn và hiển thị loại tài liệu theo vai trò, còn quản trị viên cấp hoặc thu quyền chức năng theo vai trò.",
 "This revised edition reflects every change requested in the supervisor's feedback. The table below summarises the difference between the first submission (v2) and the current version (v3).":
   "Bản chỉnh sửa này phản ánh mọi thay đổi theo góp ý của giảng viên hướng dẫn. Bảng dưới tóm tắt khác biệt giữa bản nộp đầu (v2) và phiên bản hiện tại (v3).",
 # ---------- §2.2 functional requirements ----------
 "FR-1  Authentication & Routing": "FR-1  Xác thực & Định tuyến",
 "Authenticate users by student/staff code and BCrypt password.": "Xác thực người dùng bằng mã sinh viên/cán bộ và mật khẩu BCrypt.",
 "Deny borrowing to accounts whose card status is LOCKED.": "Từ chối cho mượn với tài khoản có trạng thái thẻ LOCKED.",
 "After login, route ADMIN to user management, LIBRARIAN to the operations dashboard, and readers to the client home page.":
   "Sau khi đăng nhập, đưa ADMIN tới quản lý người dùng, LIBRARIAN tới bảng điều khiển nghiệp vụ, và bạn đọc tới trang chủ client.",
 "FR-2  Catalogue & Search": "FR-2  Danh mục & Tìm kiếm",
 "Display books with title, author, ISBN, document type, classification code, and available copies.":
   "Hiển thị sách kèm nhan đề, tác giả, ISBN, loại tài liệu, ký hiệu phân loại và số bản còn sẵn.",
 "Search by keyword (title / author / ISBN / classification code) and filter by document type and availability.":
   "Tìm theo từ khoá (nhan đề / tác giả / ISBN / ký hiệu phân loại) và lọc theo loại tài liệu và tình trạng còn sẵn.",
 "Show a reader only the document types that are visible for their role (configurable by the librarian).":
   "Chỉ hiển thị cho bạn đọc những loại tài liệu được phép xem theo vai trò (thủ thư cấu hình được).",
 "FR-3  Cart & Self-Service Loan Request": "FR-3  Giỏ sách & Phiếu mượn tự phục vụ",
 "A reader can add up to 5 books to a session cart.": "Bạn đọc có thể thêm tối đa 5 cuốn vào giỏ của phiên.",
 "On submit, the RuleEngine validates the cart; if valid the loan is created immediately (no approval).":
   "Khi gửi, RuleEngine kiểm tra giỏ; nếu hợp lệ thì đơn được tạo ngay (không phê duyệt).",
 "The loan is created with status AWAITING_PICKUP, a unique pickup code, and a 24-hour pickup deadline; held copies are decremented at once.":
   "Đơn được tạo với trạng thái AWAITING_PICKUP, một mã lấy sách duy nhất, và hạn lấy 24 giờ; số lượng giữ chỗ bị trừ ngay.",
 "FR-4  Self-Confirm Pickup (replaces approval)": "FR-4  Tự xác nhận nhận sách (thay cho phê duyệt)",
 "The reader collects the books and re-enters the pickup code on the 'My Loans' page to confirm receipt.":
   "Bạn đọc đến lấy sách và nhập lại mã lấy sách tại trang 'Đơn của tôi' để xác nhận đã nhận.",
 "The system verifies ownership, the AWAITING_PICKUP status, and an exact (case-insensitive) code match.":
   "Hệ thống kiểm tra quyền sở hữu, trạng thái AWAITING_PICKUP, và mã khớp chính xác (không phân biệt hoa/thường).",
 "On success the loan becomes BORROWED and each item's due date is set per the reader's role and document type.":
   "Khi thành công, đơn chuyển BORROWED và hạn trả từng mục được đặt theo vai trò của bạn đọc và loại tài liệu.",
 "FR-5  Auto-Cancellation of Expired Pickups": "FR-5  Tự huỷ đơn quá hạn lấy",
 "A scheduled job runs periodically and cancels every AWAITING_PICKUP loan whose 24-hour deadline has passed.":
   "Một tác vụ định kỳ chạy theo chu kỳ và huỷ mọi đơn AWAITING_PICKUP đã quá hạn 24 giờ.",
 "Cancellation restores the held copies back to the catalogue.": "Việc huỷ hoàn lại số lượng đang giữ về danh mục.",
 "FR-6  Book Return & Fine Calculation": "FR-6  Trả sách & Tính phí",
 "A librarian processes the return of each loan item.": "Thủ thư xử lý việc trả của từng mục trong đơn.",
 "The overdue fine is computed at 2,000 VND per overdue day (full day count).":
   "Phí quá hạn được tính 2.000đ mỗi ngày trễ (đếm trọn ngày).",
 "The returned condition (GOOD / DAMAGED) is recorded; the parent loan auto-closes (status COMPLETED) once all items are returned and all fines are settled.":
   "Tình trạng khi trả (GOOD / DAMAGED) được ghi nhận; đơn cha tự đóng (trạng thái COMPLETED) khi mọi mục đã trả và mọi phí đã thu.",
 "FR-7  Renewal": "FR-7  Gia hạn",
 "A reader can renew an active item within the permitted number of renewals.":
   "Bạn đọc có thể gia hạn một mục đang mượn trong số lần gia hạn cho phép.",
 "A renewal extends the due date by the configured renewal_days.":
   "Mỗi lần gia hạn kéo dài hạn trả thêm renewal_days đã cấu hình.",
 "Renewal is denied for items not owned by the reader, not in BORROWING status, or beyond the renewal limit.":
   "Từ chối gia hạn với mục không thuộc bạn đọc, không ở trạng thái BORROWING, hoặc đã vượt giới hạn gia hạn.",
 "FR-8  Administrator — Users & Permissions": "FR-8  Quản trị viên — Người dùng & Phân quyền",
 "Create, update, search and delete user accounts (including LIBRARIAN) and assign roles from the UI; lock/unlock reader cards and reset passwords.":
   "Tạo, cập nhật, tìm và xoá tài khoản người dùng (gồm cả LIBRARIAN) và gán vai trò ngay trên giao diện; khoá/mở thẻ bạn đọc và đặt lại mật khẩu.",
 "Grant or revoke functional permissions per role on the /admin/permissions page (book CRUD, return, pay-fine, cancel, in-house lending, rule management, borrow / renew / self-cancel).":
   "Cấp hoặc thu quyền chức năng theo vai trò tại trang /admin/permissions (CRUD sách, trả, thu phí, huỷ, cho mượn đọc tại chỗ, quản lý quy tắc, mượn / gia hạn / tự huỷ).",
 "The administrator's own management permissions (users + permission matrix) are fixed and cannot be revoked; the admin does not touch books, loans or borrowing rules.":
   "Các quyền quản trị của chính admin (người dùng + ma trận phân quyền) là cố định và không thể thu hồi; admin không đụng tới sách, đơn mượn hay quy tắc mượn.",
 "FR-9  Librarian — Operations": "FR-9  Thủ thư — Nghiệp vụ",
 "View pickup orders (codes & deadlines) to prepare books.": "Xem đơn chờ lấy (mã & hạn) để chuẩn bị sách.",
 "Process returns, record condition and collect overdue fines; manage the book catalogue and copy counts (CRUD).":
   "Xử lý trả sách, ghi tình trạng và thu phí quá hạn; quản lý danh mục sách và số lượng bản (CRUD).",
 "View the operations dashboard (awaiting pickup, active borrowings, overdue items).":
   "Xem bảng điều khiển nghiệp vụ (chờ lấy, đang mượn, mục quá hạn).",
 "Edit the borrowing-rule matrix and toggle document-type visibility per role on the /librarian/rules page — without modifying code.":
   "Sửa ma trận quy tắc mượn và bật/tắt hiển thị loại tài liệu theo vai trò tại trang /librarian/rules — mà không sửa mã.",
 "Issue in-house reading slips for restricted (internal) documents on the /librarian/inhouse page.":
   "Lập phiếu đọc tại chỗ cho tài liệu nội sinh (RESTRICTED) tại trang /librarian/inhouse.",
 "Cancel a pickup order with a mandatory reason that is sent to the reader; the held copies are released back to stock.":
   "Huỷ đơn chờ lấy kèm lý do bắt buộc được gửi cho bạn đọc; số lượng đang giữ được hoàn về kho.",
 "FR-10  Account Self-Service (seeded accounts & password change)": "FR-10  Tự phục vụ tài khoản (tài khoản nạp sẵn & đổi mật khẩu)",
 "The database is pre-seeded with 82 accounts (2 administrators and 20 each of student, lecturer, researcher and librarian), all using the default password password123.":
   "Cơ sở dữ liệu nạp sẵn 82 tài khoản (2 quản trị viên và 20 mỗi loại sinh viên, giảng viên, nghiên cứu viên và thủ thư), đều dùng mật khẩu mặc định password123.",
 "After signing in, any user can change their own password at /account/password; the current password is verified and the new one (>= 6 characters) is stored as a BCrypt hash.":
   "Sau khi đăng nhập, bất kỳ người dùng nào cũng có thể đổi mật khẩu của mình tại /account/password; mật khẩu hiện tại được xác minh và mật khẩu mới (>= 6 ký tự) được lưu dạng băm BCrypt.",
 "FR-11  In-App Notifications": "FR-11  Thông báo in-app",
 "Business events — loan created, pickup confirmed, return, fine collected, cancellation and completion — raise in-app notifications shown through a navbar bell.":
   "Các sự kiện nghiệp vụ — tạo đơn, xác nhận lấy sách, trả, thu phí, huỷ và hoàn tất — phát thông báo in-app hiển thị qua chuông trên thanh điều hướng.",
 "A reader is notified about their own orders; every librarian is notified about new orders, self-confirmations and auto-cancellations (NotificationService.notify / notifyRole).":
   "Bạn đọc được thông báo về đơn của chính mình; mọi thủ thư được thông báo về đơn mới, lượt tự xác nhận và lượt tự huỷ (NotificationService.notify / notifyRole).",
 "Users open /notifications to read the full list and mark items read individually or all at once.":
   "Người dùng mở /notifications để đọc toàn bộ danh sách và đánh dấu đã đọc từng mục hoặc tất cả.",
 # ---------- §3 ----------
 "The counter-approval step of the original 8-step process has been removed. The digital flow is now:":
   "Bước phê duyệt tại quầy của quy trình 8 bước gốc đã được loại bỏ. Luồng số nay là:",
 "These 12 rows seed the borrowing_rules table and are enforced by the RuleEngine. They match the QT.03.TV.DV regulation and are now editable by the librarian on the /librarian/rules page (including a per-row visibility switch).":
   "12 dòng này nạp vào bảng borrowing_rules và được RuleEngine thực thi. Chúng khớp quy định QT.03.TV.DV và nay thủ thư có thể sửa tại trang /librarian/rules (gồm cả công tắc hiển thị theo từng dòng).",
 "Before a loan is created, the cart is checked in layers. Actual user-facing messages are in Vietnamese.":
   "Trước khi tạo đơn, giỏ được kiểm tra theo nhiều tầng. Thông báo thực tế cho người dùng bằng tiếng Việt.",
 # ---------- §4 ----------
 "Spring Security uses form login backed by CustomUserDetailsService (lookup by student_code, BCrypt passwords). Authorisation is strict — there is no role hierarchy, so ADMIN and LIBRARIAN are disjoint:":
   "Spring Security dùng đăng nhập bằng form với CustomUserDetailsService (tra theo student_code, mật khẩu BCrypt). Phân quyền chặt chẽ — không có phân cấp vai trò, nên ADMIN và LIBRARIAN tách biệt:",
 "Post-login routing: ADMIN → /admin/users, LIBRARIAN → /librarian/dashboard, readers → /client/home.":
   "Định tuyến sau đăng nhập: ADMIN → /admin/users, LIBRARIAN → /librarian/dashboard, bạn đọc → /client/home.",
 "The /notifications/** path needs only an authenticated user, and /account/password falls through to .anyRequest().authenticated(), so every role can read its notifications and change its own password.":
   "Đường dẫn /notifications/** chỉ cần người dùng đã đăng nhập, và /account/password rơi vào .anyRequest().authenticated(), nên mọi vai trò đều đọc được thông báo và đổi được mật khẩu của mình.",
 "The cart is stored in the HTTP session as a List<CartItem> (60-minute timeout) and cleared on a successful submit.":
   "Giỏ được lưu trong HTTP session dưới dạng List<CartItem> (hết hạn sau 60 phút) và được xoá khi gửi thành công.",
 "All mutating service methods are @Transactional. submitLoan first verifies availability for every cart item, then atomically creates the Loan and its LoanDetails and decrements available copies, so a single out-of-stock item never leaves partially-held copies behind.":
   "Mọi phương thức service có thay đổi dữ liệu đều @Transactional. submitLoan trước hết kiểm tra còn sẵn cho từng mục trong giỏ, rồi tạo Loan và các LoanDetail một cách nguyên tử và trừ số bản còn sẵn, nên một mục hết hàng không bao giờ để lại số lượng bị giữ dở dang.",
 "PickupExpiryScheduler is a @Component whose @Scheduled(fixedRate = 600000) method (every 10 minutes) calls LoanService.cancelExpiredPickups(), which cancels AWAITING_PICKUP loans past their deadline and restores the held copies. @EnableScheduling is declared on the application class.":
   "PickupExpiryScheduler là một @Component có phương thức @Scheduled(fixedRate = 600000) (mỗi 10 phút) gọi LoanService.cancelExpiredPickups(), huỷ các đơn AWAITING_PICKUP quá hạn và hoàn lại số lượng đang giữ. @EnableScheduling được khai báo trên lớp ứng dụng.",
 # ---------- §5 ----------
 "One User has many Loans (1–N).": "Một User có nhiều Loan (1–N).",
 "One Loan has many LoanDetails (1–N, cascade ALL).": "Một Loan có nhiều LoanDetail (1–N, cascade ALL).",
 "One LoanDetail references exactly one Book.": "Một LoanDetail tham chiếu đúng một Book.",
 "BorrowingRule is keyed on (user_role, doc_type) and drives quota, due-date and visibility logic.":
   "BorrowingRule khoá theo (user_role, doc_type) và chi phối logic hạn mức, hạn trả và hiển thị.",
 "RolePermission is keyed on (user_role, permission); Notification belongs to one User and backs the in-app alert feed.":
   "RolePermission khoá theo (user_role, permission); Notification thuộc một User và làm nền cho luồng thông báo in-app.",
 # ---------- §6 prose ----------
 "UserService also provides create(...) for admin-created accounts and changePassword(...) for self-service password changes (both using BCrypt).":
   "UserService còn cung cấp create(...) cho tài khoản do admin tạo và changePassword(...) cho việc tự đổi mật khẩu (đều dùng BCrypt).",
 "NotificationService.java (service/) creates in-app notifications for business events via notify(user, …) and notifyRole(role, …), and exposes unreadCount / recent / findAll / markRead / markAllRead consumed by the navbar bell and the /notifications page.":
   "NotificationService.java (service/) tạo thông báo in-app cho các sự kiện nghiệp vụ qua notify(user, …) và notifyRole(role, …), và cung cấp unreadCount / recent / findAll / markRead / markAllRead dùng cho chuông trên thanh điều hướng và trang /notifications.",
 "PermissionService.java (service/) reads and writes the role × permission matrix (has, grantedFor, updateGrantsForRole); LibrarianController calls has(…) to gate each operation and the templates use it to show or hide action buttons.":
   "PermissionService.java (service/) đọc và ghi ma trận vai trò × quyền (has, grantedFor, updateGrantsForRole); LibrarianController gọi has(…) để chốt từng thao tác và các template dùng nó để hiện hoặc ẩn nút thao tác.",
 # ---------- §7 ----------
 "The work is split into four vertical slices (entity → repository → service → controller → view), updated to cover the new self-service and configuration features.":
   "Công việc được chia thành bốn lát cắt dọc (entity → repository → service → controller → view), cập nhật để bao phủ các tính năng tự phục vụ và cấu hình mới.",
 # ---------- §8 ----------
 "Step 1: Clone the repository": "Bước 1: Tải mã nguồn về",
 "Step 2: Verify JDK installation": "Bước 2: Kiểm tra cài đặt JDK",
 "Step 3: Build & run the application": "Bước 3: Build & chạy ứng dụng",
 "Step 4: Access the application": "Bước 4: Truy cập ứng dụng",
 "- Main app: http://localhost:8080": "- Ứng dụng chính: http://localhost:8080",
 "+ User and password have been set up in the interface": "+ Tài khoản và mật khẩu đã được thiết lập sẵn trong giao diện",
 "+ Password: (blank)": "+ Mật khẩu: (để trống)",
 "On startup the system seeds 82 accounts: 2 administrators (admin, admin02) and 20 each of student (student01-20), lecturer (lecturer01-20), researcher (researcher01-20) and librarian (librarian01-20). All share the default password password123 and should change it after first login at /account/password. Representative accounts:":
   "Khi khởi động, hệ thống nạp 82 tài khoản: 2 quản trị viên (admin, admin02) và 20 mỗi loại sinh viên (student01-20), giảng viên (lecturer01-20), nghiên cứu viên (researcher01-20) và thủ thư (librarian01-20). Tất cả dùng chung mật khẩu mặc định password123 và nên đổi sau lần đăng nhập đầu tại /account/password. Một số tài khoản tiêu biểu:",
 "Sign in as student01, add a textbook, submit — note the pickup code shown.":
   "Đăng nhập bằng student01, thêm một giáo trình, gửi phiếu — ghi lại mã lấy sách hiển thị.",
 "On 'My Loans', re-enter the pickup code and click 'Tôi đã nhận sách' → the loan becomes Đang mượn.":
   "Tại 'Đơn của tôi', nhập lại mã lấy sách và bấm 'Tôi đã nhận sách' → đơn chuyển sang Đang mượn.",
 "Sign in as librarian01 to see pickup orders and process returns; admin pages are blocked (403).":
   "Đăng nhập bằng librarian01 để xem đơn chờ lấy và xử lý trả; các trang admin bị chặn (403).",
 "Sign in as librarian01 → /librarian/rules → toggle a document type's visibility off for STUDENT → that type disappears for students. Then sign in as admin → /admin/permissions → revoke a permission for LIBRARIAN and confirm the matching action is blocked.":
   "Đăng nhập bằng librarian01 → /librarian/rules → tắt hiển thị một loại tài liệu với STUDENT → loại đó biến mất với sinh viên. Sau đó đăng nhập bằng admin → /admin/permissions → thu một quyền của LIBRARIAN và xác nhận thao tác tương ứng bị chặn.",
 "To test auto-cancel quickly, lower the scheduler fixedRate or set pickup_deadline to the past in the H2 console.":
   "Để kiểm thử tự huỷ nhanh, giảm fixedRate của scheduler hoặc đặt pickup_deadline về quá khứ trong H2 console.",
 # ---------- §9 ----------
 "mvn clean package — BUILD SUCCESS on Spring Boot 3.4.13 / Java 21.":
   "mvn clean package — BUILD SUCCESS trên Spring Boot 3.4.13 / Java 21.",
 "Application starts on port 8080; data.sql seeds 82 users, 24 books, 12 borrowing rules, the default role-permission matrix and demo loans/notifications without error.":
   "Ứng dụng khởi động ở cổng 8080; data.sql nạp 82 người dùng, 24 sách, 12 quy tắc mượn, ma trận vai trò–quyền mặc định và các đơn mượn/thông báo demo mà không lỗi.",
 "Scheduled auto-cancel job is registered via @EnableScheduling.":
   "Tác vụ tự huỷ định kỳ được đăng ký qua @EnableScheduling.",
 # ---------- §10 / §11 intros (English ones) ----------
 "This section documents the document-borrowing workflow twice: first the original counter-based process of the Phenikaa University Library (regulation QT.03.TV.DV), then the digital self-service process implemented by our system, followed by a concise side-by-side comparison. CBTV = library staff (cán bộ thư viện).":
   "Phần này trình bày quy trình mượn tài liệu theo hai cách: trước hết là quy trình truyền thống tại quầy của Thư viện Trường Đại học Phenikaa (quy định QT.03.TV.DV), sau đó là quy trình số tự phục vụ do hệ thống của chúng tôi hiện thực, rồi đến một bảng so sánh ngắn gọn. CBTV = cán bộ thư viện.",
 "Service hours: Monday–Friday, 08:00–11:30 and 13:30–16:30. The counter-based borrowing procedure has eight steps. The flowchart below reproduces the official regulation; the responsible party and the associated forms (biểu mẫu) are listed per step.":
   "Giờ phục vụ: Thứ Hai–Thứ Sáu, 08:00–11:30 và 13:30–16:30. Quy trình mượn tại quầy gồm tám bước. Lưu đồ dưới đây tái hiện đúng quy định chính thức; người thực hiện và các biểu mẫu liên quan được liệt kê theo từng bước.",
 "Walkthrough (diễn giải):": "Diễn giải:",
 "Our web system keeps the same business intent but removes the counter-approval bottleneck. The reader self-serves the whole borrowing request; trust is enforced by a one-time pickup code instead of staff approval. The flowchart below maps each step to its actor, the system logic involved, and the concrete route / artifact produced.":
   "Hệ thống web của chúng tôi giữ nguyên mục tiêu nghiệp vụ nhưng loại bỏ nút thắt phê duyệt tại quầy. Bạn đọc tự phục vụ toàn bộ phiếu mượn; sự tin cậy được bảo đảm bằng mã lấy sách dùng một lần thay cho việc thủ thư phê duyệt. Lưu đồ dưới đây ánh xạ mỗi bước tới người thực hiện, logic hệ thống liên quan và route / tạo phẩm cụ thể.",
 "The table summarises how each system handles the same business concerns.":
   "Bảng dưới tóm tắt cách mỗi hệ thống xử lý cùng các vấn đề nghiệp vụ.",
 "To present the design from complementary viewpoints, four UML models were selected as the most representative for this system: a Use Case diagram (who does what), a Class diagram (the static domain model behind the database), an Activity diagram (the self-service borrowing flow with its decision points), and a State Machine diagram (the rich lifecycle of a loan). Sequence and deployment diagrams were considered but omitted because the activity and class models already convey the behaviour and structure clearly at this scope.":
   "Để trình bày thiết kế từ những góc nhìn bổ trợ nhau, bốn mô hình UML tiêu biểu nhất cho hệ thống đã được chọn: sơ đồ Use Case (ai làm gì), sơ đồ lớp (mô hình miền tĩnh phía sau cơ sở dữ liệu), sơ đồ hoạt động (luồng mượn tự phục vụ cùng các điểm quyết định), và sơ đồ trạng thái (vòng đời phong phú của đơn mượn). Sơ đồ tuần tự và sơ đồ triển khai đã được cân nhắc nhưng lược bỏ vì sơ đồ hoạt động và sơ đồ lớp đã truyền đạt rõ hành vi và cấu trúc ở phạm vi này.",
 "Actors are grouped exactly as the RBAC design dictates — Reader (Student/Lecturer/Researcher), Librarian, Administrator — plus a non-human Scheduler actor for the background jobs. There is no role inheritance: each actor sees only its own use cases. Đăng nhập and Đổi mật khẩu apply to every authenticated role.":
   "Các tác nhân được nhóm đúng theo thiết kế RBAC — Bạn đọc (Sinh viên/Giảng viên/Nghiên cứu viên), Thủ thư, Quản trị viên — cùng một tác nhân phi-người là Bộ lập lịch cho các tác vụ nền. Không có kế thừa vai trò: mỗi tác nhân chỉ thấy use case của mình. Đăng nhập và Đổi mật khẩu áp dụng cho mọi vai trò đã đăng nhập.",
 "The static model mirrors the JPA entities and their relationships: one User has many Loans; a Loan is composed of one-or-more LoanDetails (cascade ALL); each LoanDetail references one Book; a User receives many Notifications. BorrowingRule and RolePermission are enum-keyed configuration tables that drive the rules and the permission matrix.":
   "Mô hình tĩnh phản ánh các thực thể JPA và quan hệ giữa chúng: một User có nhiều Loan; một Loan gồm một hoặc nhiều LoanDetail (cascade ALL); mỗi LoanDetail tham chiếu một Book; một User nhận nhiều Notification. BorrowingRule và RolePermission là các bảng cấu hình khoá theo enum, chi phối các quy tắc và ma trận phân quyền.",
 "The main flow runs from login through cart checks, three-tier validation, code-based pickup, renewal, return and fine settlement, to COMPLETED. Failure branches loop back to the cart or stay in the awaiting state, and the 24-hour timeout branch shows the scheduler auto-cancelling an un-collected order.":
   "Luồng chính chạy từ đăng nhập qua các bước kiểm tra giỏ, kiểm tra 3 tầng, lấy sách bằng mã, gia hạn, trả sách và thu phí, đến COMPLETED. Các nhánh thất bại quay lại giỏ hoặc giữ ở trạng thái chờ, còn nhánh quá hạn 24 giờ thể hiện scheduler tự huỷ đơn không được lấy.",
 "A Loan starts at AWAITING_PICKUP, becomes BORROWED on a valid pickup, and reaches COMPLETED only after every item is returned and every fine is settled; it goes to CANCELLED on reader/staff cancellation or the 24-hour timeout. The lower strip shows the LoanDetail lifecycle (RESERVED → BORROWING → RETURNED); OVERDUE is computed dynamically and is not stored as a separate state.":
   "Một Loan bắt đầu ở AWAITING_PICKUP, chuyển BORROWED khi lấy sách hợp lệ, và chỉ đạt COMPLETED sau khi mọi mục đã trả và mọi phí đã thu; nó chuyển CANCELLED khi bạn đọc/nhân viên huỷ hoặc quá hạn 24 giờ. Dải dưới thể hiện vòng đời LoanDetail (RESERVED → BORROWING → RETURNED); OVERDUE được tính động và không lưu thành trạng thái riêng.",
 # ---------- §12 ----------
 "Delivered a genuinely self-service borrowing model: no approval, pickup code, 24h window, and reader self-confirmation by code.":
   "Đã xây dựng một mô hình mượn thực sự tự phục vụ: không phê duyệt, có mã lấy sách, thời hạn 24h, và bạn đọc tự xác nhận bằng mã.",
 "Implemented automatic release of un-collected holds through a scheduled background job.":
   "Hiện thực việc tự động hoàn lại các phần giữ chỗ không được lấy thông qua một tác vụ nền định kỳ.",
 "Cleanly separated Administrator (user accounts + functional-permission matrix) and Librarian (operations + borrowing-rule configuration) with strict, non-inheriting RBAC.":
   "Tách bạch rõ Quản trị viên (tài khoản người dùng + ma trận phân quyền chức năng) và Thủ thư (nghiệp vụ + cấu hình quy tắc mượn) với RBAC chặt chẽ, không kế thừa.",
 "Added on-screen configuration — a librarian page for borrowing rules and document-type visibility, and an admin functional-permission matrix per role — requiring no code change.":
   "Bổ sung cấu hình ngay trên giao diện — trang thủ thư cho quy tắc mượn và hiển thị loại tài liệu, và ma trận phân quyền chức năng theo vai trò cho admin — không cần sửa mã.",
 "Fixed two real logic bugs (overdue-fine day count and a stock-hold leak) and upgraded to Spring Boot 3.4.13.":
   "Sửa hai lỗi logic thực sự (đếm ngày phí quá hạn và rò rỉ giữ chỗ tồn kho) và nâng cấp lên Spring Boot 3.4.13.",
 "Pre-seeded a default-password account base (82 accounts) and added self-service password change for every role.":
   "Nạp sẵn nền tài khoản dùng mật khẩu mặc định (82 tài khoản) và thêm chức năng tự đổi mật khẩu cho mọi vai trò.",
 "Added an in-app notification system (navbar bell + /notifications) and a librarian in-house reading workflow for restricted documents.":
   "Thêm hệ thống thông báo in-app (chuông trên thanh điều hướng + /notifications) và quy trình đọc tại chỗ của thủ thư cho tài liệu nội sinh.",
 "Self-confirmation trusts that the reader has physically received the books (mitigated by requiring the pickup code).":
   "Việc tự xác nhận tin rằng bạn đọc đã thực sự nhận sách (được giảm thiểu bằng yêu cầu nhập mã lấy sách).",
 "H2 in-memory storage loses data on restart; production needs MySQL.":
   "Lưu trữ H2 in-memory mất dữ liệu khi khởi động lại; môi trường sản xuất cần MySQL.",
 "No reservation queue for currently-unavailable titles. In-app notifications are implemented, but email delivery is not; the due-soon / overdue reminder method (LoanService.sendDueReminders) exists but is not yet scheduled.":
   "Chưa có hàng đợi đặt trước cho đầu sách đang hết. Thông báo in-app đã có, nhưng gửi email thì chưa; phương thức nhắc sắp/đã quá hạn (LoanService.sendDueReminders) đã tồn tại nhưng chưa được lên lịch.",

 # =================== TABLE CELLS ===================
 # common headers
 "Layer": "Lớp", "Technology": "Công nghệ", "Version / Notes": "Phiên bản / Ghi chú",
 "Column": "Cột", "Type": "Kiểu", "Notes": "Ghi chú", "Method": "Phương thức",
 "Description": "Mô tả", "Feature": "Tính năng", "Route": "Đường dẫn", "Item": "Mục",
 "Package": "Gói", "Key Classes": "Lớp chính", "Member": "Thành viên", "Module": "Phần việc",
 "Key Files": "Tệp chính", "Tool": "Công cụ", "Version": "Phiên bản", "Purpose": "Mục đích",
 "Role": "Vai trò", "Card Status": "Trạng thái thẻ", "Scenario": "Kịch bản",
 "Expected Result": "Kết quả mong đợi", "Result": "Kết quả", "Priority": "Ưu tiên",
 "Category": "Hạng mục", "Requirement": "Yêu cầu", "Step": "Bước", "Activity": "Hoạt động",
 "Actor": "Người thực hiện", "Digital Implementation": "Hiện thực số", "Rule": "Quy tắc",
 "Stakeholder": "Bên liên quan", "Role in System": "Vai trò trong hệ thống", "Key Needs": "Nhu cầu chính",
 "Aspect": "Khía cạnh", "Login Code": "Mã đăng nhập", "ID": "ID",
 "(none)": "(không có)", "Pass": "Đạt",
 # table 1 (tech stack)
 "Backend Framework": "Nền tảng backend", "Server-side template rendering": "Kết xuất giao diện phía máy chủ",
 "Security": "Bảo mật", "Form login, strict role-based access (no role hierarchy)": "Đăng nhập bằng form, phân quyền chặt theo vai trò (không kế thừa vai trò)",
 "Scheduling": "Lập lịch", "Background job auto-cancels expired pickups": "Tác vụ nền tự huỷ đơn quá hạn lấy",
 "ORM / Persistence": "ORM / Lưu trữ", "H2 in-memory (dev), MySQL-compatible": "H2 in-memory (phát triển), tương thích MySQL",
 "Database (Dev)": "CSDL (phát triển)", "Auto-seeded via data.sql on startup": "Tự nạp dữ liệu qua data.sql khi khởi động",
 "Database (Prod)": "CSDL (sản xuất)", "Switch via application.properties": "Chuyển đổi qua application.properties",
 "UI Framework": "Khung giao diện", "Responsive layout": "Bố cục responsive",
 "Build Tool": "Công cụ build", "pom.xml dependency management": "Quản lý phụ thuộc qua pom.xml",
 "Language": "Ngôn ngữ",
 # table 2 (changes)
 "v2 (previous)": "v2 (trước)", "v3 (current)": "v3 (hiện tại)",
 "Approval": "Phê duyệt", "Librarian must approve each PENDING loan": "Thủ thư phải duyệt từng đơn PENDING",
 "No approval — granted immediately if eligible": "Không phê duyệt — cấp ngay nếu đủ điều kiện",
 "Pickup": "Lấy sách", "Implicit, no code": "Ngầm định, không có mã",
 "Pickup code + 24h window; reader self-confirms by code": "Mã lấy sách + thời hạn 24h; bạn đọc tự xác nhận bằng mã",
 "Un-collected loans": "Đơn chưa lấy", "Stay forever / manual cancel": "Tồn mãi / huỷ thủ công",
 "Auto-cancelled after 24h by a scheduled job": "Tự huỷ sau 24h bằng tác vụ định kỳ",
 "Roles": "Vai trò", "ADMIN + readers only": "Chỉ ADMIN + bạn đọc",
 "ADMIN, LIBRARIAN + readers (clearly separated)": "ADMIN, LIBRARIAN + bạn đọc (tách bạch rõ)",
 "Admin scope": "Phạm vi của admin", "Everything (loans, books, users)": "Tất cả (đơn mượn, sách, người dùng)",
 "Only Users + a functional-permission matrix (no loans/books/rules)": "Chỉ người dùng + ma trận phân quyền chức năng (không đụng đơn mượn/sách/quy tắc)",
 "Librarian scope": "Phạm vi của thủ thư", "(did not exist)": "(chưa tồn tại)",
 "Dashboard, pickup orders, returns, book/stock management": "Bảng điều khiển, đơn chờ lấy, trả sách, quản lý sách/tồn kho",
 "Doc-type visibility": "Hiển thị loại tài liệu", "Hard-coded": "Cố định trong mã",
 "Toggled per role by the librarian on the /librarian/rules page": "Thủ thư bật/tắt theo vai trò tại trang /librarian/rules",
 "Loan status": "Trạng thái đơn",
 "Notifications": "Thông báo",
 "In-app notifications for loan / pickup / return / fine / cancel events (navbar bell)": "Thông báo in-app cho các sự kiện mượn / lấy / trả / phí / huỷ (chuông trên thanh điều hướng)",
 "In-house reading": "Đọc tại chỗ", "Librarian issues read-on-site slips for restricted documents": "Thủ thư lập phiếu đọc tại chỗ cho tài liệu nội sinh",
 "Fine settlement": "Thanh toán phí",
 "Per-item fine tracked & marked paid; a loan completes only when settled": "Theo dõi phí từng cuốn & đánh dấu đã thu; đơn chỉ hoàn tất khi đã thanh toán xong",
 # table 3 (stakeholders)
 "Student": "Sinh viên", "Borrower (STUDENT)": "Bạn đọc (STUDENT)",
 "Search, request loans, self-confirm pickup, view history, renew": "Tra cứu, tạo phiếu mượn, tự xác nhận nhận sách, xem lịch sử, gia hạn",
 "Lecturer / Staff": "Giảng viên / Cán bộ", "Borrower (LECTURER)": "Bạn đọc (LECTURER)",
 "Unlimited loan duration on textbooks & specialised references": "Thời hạn mượn không giới hạn với giáo trình & tài liệu tham khảo chuyên ngành",
 "Researcher": "Nghiên cứu viên", "Borrower (RESEARCHER)": "Bạn đọc (RESEARCHER)",
 "Extended quotas on specialised references": "Hạn mức mở rộng với tài liệu tham khảo chuyên ngành",
 "Librarian": "Thủ thư", "Operations (LIBRARIAN)": "Nghiệp vụ (LIBRARIAN)",
 "Prepare books for pickup orders, process returns & fines, configure borrowing rules & document-type visibility, issue in-house reading slips, manage catalogue & stock": "Chuẩn bị sách cho đơn chờ lấy, xử lý trả sách & phí, cấu hình quy tắc mượn & hiển thị loại tài liệu, lập phiếu đọc tại chỗ, quản lý danh mục & tồn kho",
 "Administrator": "Quản trị viên", "System admin (ADMIN)": "Quản trị hệ thống (ADMIN)",
 "Manage users & roles; configure the functional-permission matrix per role": "Quản lý người dùng & vai trò; cấu hình ma trận phân quyền chức năng theo vai trò",
 # table 4 (NFR)
 "Strict RBAC. /admin/** requires ADMIN; /librarian/** requires LIBRARIAN; /client/** requires a reader role; /notifications/** requires any authenticated user. No role inheritance between ADMIN and LIBRARIAN.":
   "RBAC chặt chẽ. /admin/** yêu cầu ADMIN; /librarian/** yêu cầu LIBRARIAN; /client/** yêu cầu vai trò bạn đọc; /notifications/** yêu cầu người dùng đã đăng nhập. Không kế thừa vai trò giữa ADMIN và LIBRARIAN.",
 "Data Integrity": "Toàn vẹn dữ liệu",
 "All mutating service methods are @Transactional; copies are validated for all cart items before any decrement.":
   "Mọi phương thức service có thay đổi dữ liệu đều @Transactional; số lượng được kiểm tra cho tất cả mục trong giỏ trước khi trừ.",
 "Reliability": "Độ tin cậy",
 "A scheduled job guarantees that held copies are released if a reader never collects the books.":
   "Tác vụ định kỳ bảo đảm số lượng đang giữ được hoàn lại nếu bạn đọc không đến lấy sách.",
 "Usability": "Khả dụng",
 "Bootstrap 5 responsive UI; inline validation messages; pickup code shown prominently to the reader.":
   "Giao diện responsive Bootstrap 5; thông báo kiểm tra ngay tại chỗ; mã lấy sách hiển thị nổi bật cho bạn đọc.",
 "Portability": "Tính khả chuyển",
 "H2 in-memory for zero-config development; MySQL-compatible schema for production.":
   "H2 in-memory để phát triển không cần cấu hình; lược đồ tương thích MySQL cho sản xuất.",
 "Maintainability": "Khả bảo trì",
 "Clean MVC layering; all borrowing calculations encapsulated in RuleEngine; rule administration in RuleService.":
   "Phân lớp MVC rõ ràng; mọi tính toán mượn gói trong RuleEngine; quản trị quy tắc trong RuleService.",
 # table 5 (revised process)
 "Sign in": "Đăng nhập", "Reader": "Bạn đọc", "Login + card_status == ACTIVE check": "Đăng nhập + kiểm tra card_status == ACTIVE",
 "Search documents": "Tra cứu tài liệu", "GET /client/home — keyword + type filter, role-based visibility": "GET /client/home — lọc theo từ khoá + loại, hiển thị theo vai trò",
 "Build cart & submit": "Tạo giỏ & gửi phiếu", "POST /client/loans/submit — RuleEngine validates": "POST /client/loans/submit — RuleEngine kiểm tra",
 "Loan granted instantly": "Cấp đơn ngay", "System": "Hệ thống",
 "Create loan AWAITING_PICKUP + pickup code + 24h deadline; hold copies": "Tạo đơn AWAITING_PICKUP + mã lấy sách + hạn 24h; giữ chỗ",
 "Prepare books": "Chuẩn bị sách",
 "View pickup orders (GET /librarian/loans?status=AWAITING_PICKUP) and gather books": "Xem đơn chờ lấy (GET /librarian/loans?status=AWAITING_PICKUP) và soạn sách",
 "Collect & self-confirm": "Nhận & tự xác nhận",
 "Re-enter pickup code → POST /client/loans/{id}/pickup → BORROWED + due dates": "Nhập lại mã lấy sách → POST /client/loans/{id}/pickup → BORROWED + hạn trả",
 "Auto-cancel if not collected": "Tự huỷ nếu không lấy",
 "Scheduled job cancels expired AWAITING_PICKUP loans, releases copies": "Tác vụ định kỳ huỷ đơn AWAITING_PICKUP quá hạn, hoàn số lượng",
 "Return / renew / fine": "Trả / gia hạn / phí", "Reader + Librarian": "Bạn đọc + Thủ thư",
 "Return via /librarian/loans/details/{id}/return; renew via /client/loans/renew/{detailId}": "Trả qua /librarian/loans/details/{id}/return; gia hạn qua /client/loans/renew/{detailId}",
 # table 6 (rules)
 "Document Type": "Loại tài liệu", "Max Qty": "SL tối đa", "Loan Duration": "Thời hạn mượn",
 "Max Renewals": "Số lần gia hạn", "Days / Renewal": "Số ngày / lần",
 "Lecturer": "Giảng viên", "Textbook": "Giáo trình", "Specialised Ref.": "TLTK chuyên ngành",
 "General Ref.": "TLTK tổng quát", "Restricted": "Nội sinh",
 "150 days": "150 ngày", "90 days": "90 ngày", "15 days": "15 ngày", "30 days": "30 ngày",
 "Unlimited": "Không giới hạn", "3 days": "3 ngày", "Not allowed": "Không cho phép",
 # table 7 (validation)
 "Message (Vietnamese)": "Thông báo (tiếng Việt)",
 "Account": "Tài khoản", "Cart": "Giỏ", "Quota": "Hạn mức",
 "Card status must be ACTIVE": "Trạng thái thẻ phải ACTIVE",
 "No outstanding overdue items": "Không còn tài liệu quá hạn",
 "Cart not empty": "Giỏ không rỗng",
 "At most 5 items per loan": "Tối đa 5 cuốn mỗi lần mượn",
 "Restricted documents not borrowable": "Tài liệu nội sinh không được mượn về",
 "Type must be visible & permitted (max_quantity>0)": "Loại phải được hiển thị & cho phép (max_quantity>0)",
 "Per-type count ≤ max_quantity": "Số lượng theo loại ≤ max_quantity",
 # table 8 (architecture)
 "Presentation": "Trình bày", "Business Logic": "Logic nghiệp vụ", "Rule Engine": "Bộ quy tắc",
 "Data Access": "Truy cập dữ liệu", "Domain Model": "Mô hình miền", "DTOs": "DTO", "View": "Giao diện",
 # tables 9-13 (schema notes)
 "PK, auto-increment": "Khoá chính, tự tăng", "PK": "Khoá chính",
 "UNIQUE, NOT NULL — login id": "UNIQUE, NOT NULL — mã đăng nhập",
 "NOT NULL": "NOT NULL", "NOT NULL — BCrypt": "NOT NULL — BCrypt",
 "Account creation timestamp": "Thời điểm tạo tài khoản",
 "Subject category": "Phân loại chủ đề", "Stock counters": "Bộ đếm tồn kho",
 "Catalogue metadata": "Siêu dữ liệu biên mục",
 "When the request was submitted": "Thời điểm gửi phiếu",
 "6-char code the reader uses to collect & confirm": "Mã 6 ký tự bạn đọc dùng để lấy & xác nhận",
 "created_at + 24h; after this the loan auto-cancels": "created_at + 24h; sau mốc này đơn tự huỷ",
 "When the reader confirmed receipt": "Thời điểm bạn đọc xác nhận đã nhận",
 "TRUE for in-house (read-on-site) slips of restricted documents": "TRUE cho phiếu đọc tại chỗ của tài liệu nội sinh",
 "Reason shown to the reader when an order is cancelled": "Lý do hiển thị cho bạn đọc khi đơn bị huỷ",
 "Role that cancelled (LIBRARIAN / reader); NULL if auto-cancelled": "Vai trò đã huỷ (LIBRARIAN / bạn đọc); NULL nếu tự huỷ",
 "Set on pickup (NULL before pickup, or unlimited for lecturers)": "Đặt khi lấy sách (NULL trước khi lấy, hoặc không giới hạn với giảng viên)",
 "Actual return timestamp": "Thời điểm trả thực tế",
 "GOOD | DAMAGED (set on return)": "GOOD | DAMAGED (đặt khi trả)",
 "Overdue fine in VND": "Phí quá hạn (VND)",
 "Whether the overdue fine has been collected (auto-true when fine = 0)": "Phí quá hạn đã thu hay chưa (tự true khi phí = 0)",
 "part of UNIQUE(user_role, doc_type)": "thuộc UNIQUE(user_role, doc_type)",
 "Max copies per loan (0 = not permitted)": "Số cuốn tối đa mỗi lần mượn (0 = không cho phép)",
 "Loan duration; NULL = unlimited": "Thời hạn mượn; NULL = không giới hạn",
 "0 = no renewal": "0 = không gia hạn",
 "Days added per renewal": "Số ngày cộng mỗi lần gia hạn",
 "NEW — whether this role sees/borrows this doc type (admin-toggled)": "MỚI — vai trò này có thấy/mượn loại tài liệu này không (admin bật/tắt)",
 # table 14 (reader features)
 "Browse & Search": "Duyệt & Tìm kiếm", "Keyword/type/availability filters; only role-visible document types": "Lọc theo từ khoá/loại/còn sẵn; chỉ loại tài liệu vai trò được xem",
 "Book Detail": "Chi tiết sách", "Full metadata, availability, in-cart indicator": "Đầy đủ siêu dữ liệu, tình trạng còn sẵn, cờ đã ở trong giỏ",
 "Add to Cart": "Thêm vào giỏ", "Duplicate & 5-item limit, availability and visibility checks": "Kiểm tra trùng & giới hạn 5 cuốn, còn sẵn và quyền hiển thị",
 "View / Remove / Clear Cart": "Xem / Xoá / Làm trống giỏ", "Manage session cart": "Quản lý giỏ trong phiên",
 "Submit Loan (self-service)": "Gửi phiếu mượn (tự phục vụ)", "Validate → create AWAITING_PICKUP + pickup code; show code": "Kiểm tra → tạo AWAITING_PICKUP + mã lấy sách; hiển thị mã",
 "My Loans": "Đơn của tôi", "Awaiting-pickup orders (with code & countdown), active loans, history": "Đơn chờ lấy (kèm mã & đếm ngược), đơn đang mượn, lịch sử",
 "Self-Confirm Pickup": "Tự xác nhận nhận sách", "Re-enter code → BORROWED + due dates": "Nhập lại mã → BORROWED + hạn trả",
 "Renew": "Gia hạn", "Ownership + BORROWING + renewal-limit checks": "Kiểm tra quyền sở hữu + trạng thái BORROWING + giới hạn gia hạn",
 "Change Password (all roles)": "Đổi mật khẩu (mọi vai trò)", "Verify current password, set a new one (>= 6 chars), saved as BCrypt hash": "Xác minh mật khẩu hiện tại, đặt mật khẩu mới (>= 6 ký tự), lưu dạng băm BCrypt",
 "Cancel Pickup Order": "Huỷ đơn chờ lấy", "Reader cancels an own AWAITING_PICKUP order; held copies released": "Bạn đọc huỷ đơn AWAITING_PICKUP của mình; hoàn số lượng đang giữ",
 "Read in-app notifications; mark one or all as read": "Đọc thông báo in-app; đánh dấu đã đọc một hoặc tất cả",
 # table 15 (librarian features)
 "Operations Dashboard": "Bảng điều khiển nghiệp vụ", "Awaiting-pickup count, active borrowings, overdue list": "Số đơn chờ lấy, đơn đang mượn, danh sách quá hạn",
 "Pickup Orders": "Đơn chờ lấy", "AWAITING_PICKUP list with codes & deadlines (prepare books)": "Danh sách AWAITING_PICKUP kèm mã & hạn (soạn sách)",
 "Cancel Order": "Huỷ đơn", "Cancel with a mandatory reason; held copies restored and the reader notified": "Huỷ kèm lý do bắt buộc; hoàn số lượng đang giữ và thông báo cho bạn đọc",
 "Active Borrowings": "Đơn đang mượn", "BORROWING items with due dates / overdue markers": "Các mục BORROWING kèm hạn trả / dấu quá hạn",
 "Process Return": "Xử lý trả sách", "Record condition, compute fine, restore copy; loan completes when all items returned & fines settled": "Ghi tình trạng, tính phí, hoàn số lượng; đơn hoàn tất khi đã trả hết & thu hết phí",
 "Book / Stock Management": "Quản lý sách / tồn kho", "Full CRUD incl. copy counts": "CRUD đầy đủ gồm cả số lượng bản",
 "Collect Fine": "Thu phí", "Mark an overdue fine collected; completes the loan when fully settled": "Đánh dấu đã thu phí quá hạn; hoàn tất đơn khi đã thanh toán đủ",
 "Borrowing-Rule & Visibility Config": "Cấu hình quy tắc mượn & hiển thị", "Edit quotas/durations/renewals and toggle document-type visibility per role": "Sửa hạn mức/thời hạn/số lần gia hạn và bật/tắt hiển thị loại tài liệu theo vai trò",
 "In-House Reading Slip": "Phiếu đọc tại chỗ", "Issue a read-on-site slip for a RESTRICTED document by reader code": "Lập phiếu đọc tại chỗ cho tài liệu RESTRICTED theo mã bạn đọc",
 # table 16 (admin features)
 "User Management": "Quản lý người dùng", "List/search users; lock/unlock cards; reset password; delete": "Liệt kê/tìm người dùng; khoá/mở thẻ; đặt lại mật khẩu; xoá",
 "Create User": "Tạo người dùng", "Create accounts (incl. LIBRARIAN) and assign role": "Tạo tài khoản (gồm cả LIBRARIAN) và gán vai trò",
 "Permission Matrix": "Ma trận phân quyền", "Grant/revoke functional permissions per role (book CRUD, return, pay-fine, cancel, in-house, rule-manage, borrow/renew/self-cancel)": "Cấp/thu quyền chức năng theo vai trò (CRUD sách, trả, thu phí, huỷ, đọc tại chỗ, quản lý quy tắc, mượn/gia hạn/tự huỷ)",
 "Login & Routing": "Đăng nhập & Định tuyến", "Form login; role-based redirect": "Đăng nhập bằng form; chuyển hướng theo vai trò",
 # table 17 (RuleEngine)
 "Account/cart/type/quota checks incl. visibility; returns ValidationResult.": "Kiểm tra tài khoản/giỏ/loại/hạn mức gồm cả hiển thị; trả về ValidationResult.",
 "Adds borrow_days; returns null for unlimited.": "Cộng borrow_days; trả null nếu không giới hạn.",
 "ChronoUnit.DAYS between due date and return date × 2,000 VND.": "ChronoUnit.DAYS giữa hạn trả và ngày trả × 2.000đ.",
 # table 18 (LoanService)
 "Validate, verify stock, create AWAITING_PICKUP loan + pickup code + 24h deadline, hold copies.": "Kiểm tra, xác minh tồn kho, tạo đơn AWAITING_PICKUP + mã lấy sách + hạn 24h, giữ chỗ.",
 "Reader self-confirm: owner + status + code check → BORROWED + due dates.": "Bạn đọc tự xác nhận: kiểm tra chủ sở hữu + trạng thái + mã → BORROWED + hạn trả.",
 "Librarian cancels with a mandatory reason; restores copies, records who cancelled, notifies the reader.": "Thủ thư huỷ kèm lý do bắt buộc; hoàn số lượng, ghi nhận người huỷ, thông báo cho bạn đọc.",
 "Cancel all AWAITING_PICKUP loans past their deadline (called by scheduler).": "Huỷ mọi đơn AWAITING_PICKUP quá hạn (do scheduler gọi).",
 "Record return & condition, compute the fine, restore the copy; the loan reaches COMPLETED once all items are returned and all fines are settled.": "Ghi trả & tình trạng, tính phí, hoàn số lượng; đơn đạt COMPLETED khi mọi cuốn đã trả và mọi phí đã thu.",
 "Ownership + BORROWING + renewal-limit checks, then extend due date.": "Kiểm tra quyền sở hữu + BORROWING + giới hạn gia hạn, rồi gia hạn hạn trả.",
 "Reader cancels an own AWAITING_PICKUP order; restores the held copies.": "Bạn đọc huỷ đơn AWAITING_PICKUP của mình; hoàn số lượng đang giữ.",
 "Mark an item's overdue fine as collected; completes the loan when every item is settled.": "Đánh dấu đã thu phí quá hạn của một mục; hoàn tất đơn khi mọi mục đã thanh toán.",
 "Create an in-house (read-on-site) BORROWED loan for a RESTRICTED document, due same day.": "Tạo đơn BORROWED đọc tại chỗ cho tài liệu RESTRICTED, hạn trong ngày.",
 "Push due-soon / overdue notifications to readers (implemented; not yet scheduled).": "Gửi thông báo sắp/đã quá hạn cho bạn đọc (đã hiện thực; chưa lên lịch).",
 # table 19 (RuleService)
 "All rules ordered by role & type for the config page.": "Tất cả quy tắc sắp theo vai trò & loại cho trang cấu hình.",
 "Set of doc types where visible = true for that role.": "Tập loại tài liệu có visible = true cho vai trò đó.",
 "Persist max_quantity / borrow_days / renewals / visibility edits.": "Lưu các chỉnh sửa max_quantity / borrow_days / số lần gia hạn / hiển thị.",
 # table 20 (changes)
 "Self-service borrowing": "Mượn tự phục vụ", "Removed approval; loans are granted immediately with a pickup code and a 24h window.": "Bỏ phê duyệt; đơn được cấp ngay kèm mã lấy sách và thời hạn 24h.",
 "Reader self-confirm by code": "Bạn đọc tự xác nhận bằng mã", "Reader re-enters the pickup code to move the loan to BORROWED (no librarian accept).": "Bạn đọc nhập lại mã lấy sách để chuyển đơn sang BORROWED (không cần thủ thư chấp nhận).",
 "Auto-cancel job": "Tác vụ tự huỷ", "Scheduled task releases held copies if a reader does not collect within 24h.": "Tác vụ định kỳ hoàn số lượng đang giữ nếu bạn đọc không lấy trong 24h.",
 "Admin/Librarian separation": "Tách Admin/Thủ thư", "Disjoint roles: the admin handles user accounts + the functional-permission matrix, the librarian handles operations + borrowing-rule configuration.": "Hai vai trò tách biệt: admin lo tài khoản người dùng + ma trận phân quyền chức năng, thủ thư lo nghiệp vụ + cấu hình quy tắc mượn.",
 "On-screen configuration": "Cấu hình trên giao diện", "borrowing_rules gains a 'visible' flag; the librarian edits rules & visibility on /librarian/rules, while the admin grants functional permissions on /admin/permissions.": "borrowing_rules có thêm cờ 'visible'; thủ thư sửa quy tắc & hiển thị tại /librarian/rules, còn admin cấp quyền chức năng tại /admin/permissions.",
 "Overdue fine bug": "Lỗi phí quá hạn", "Bug fix": "Sửa lỗi", "calculateFine used Period.getDays() (day-of-month only). Now uses ChronoUnit.DAYS.between for the true day count.": "calculateFine dùng Period.getDays() (chỉ lấy ngày trong tháng). Nay dùng ChronoUnit.DAYS.between để đếm đúng số ngày.",
 "Stock-hold bug": "Lỗi giữ chỗ tồn kho", "submitLoan now verifies availability for all items before any decrement, preventing leaked held copies.": "submitLoan nay kiểm tra còn sẵn cho mọi mục trước khi trừ, tránh rò rỉ số lượng đang giữ.",
 "Carried-over v2 fixes": "Các bản sửa từ v2", "Transactional cancelLoan, renewal ownership check, and BORROWING-status guard remain in place.": "cancelLoan có giao dịch, kiểm tra quyền sở hữu khi gia hạn, và chốt trạng thái BORROWING vẫn được giữ.",
 "Default-password account seed": "Nạp tài khoản mật khẩu mặc định", "data.sql seeds 82 accounts (2 admins + 20 per role) with default password password123; no manual ids so auto-increment stays correct for admin-created users.": "data.sql nạp 82 tài khoản (2 admin + 20 mỗi vai trò) với mật khẩu mặc định password123; không gán id thủ công nên auto-increment vẫn đúng cho người dùng do admin tạo.",
 "Self-service password change": "Tự đổi mật khẩu", "Any authenticated user changes their password at /account/password (UserService.changePassword + AccountController + change-password.html).": "Mọi người dùng đã đăng nhập đổi mật khẩu tại /account/password (UserService.changePassword + AccountController + change-password.html).",
 "In-app notifications": "Thông báo in-app", "NotificationService raises in-app alerts for loan / pickup / return / fine / cancel / completion events; navbar bell + /notifications page.": "NotificationService phát thông báo in-app cho các sự kiện mượn / lấy / trả / phí / huỷ / hoàn tất; chuông trên thanh điều hướng + trang /notifications.",
 "Librarian issues read-on-site slips for RESTRICTED (internal) documents via /librarian/inhouse.": "Thủ thư lập phiếu đọc tại chỗ cho tài liệu RESTRICTED (nội sinh) qua /librarian/inhouse.",
 "Fine settlement & COMPLETED": "Thanh toán phí & COMPLETED", "Per-item fine_paid flag; a loan reaches COMPLETED only after all items are returned and all fines settled.": "Cờ fine_paid theo từng mục; đơn đạt COMPLETED chỉ khi mọi cuốn đã trả và mọi phí đã thu.",
 "Functional-permission matrix": "Ma trận phân quyền chức năng", "Permission enum + role_permissions table + PermissionService gate every librarian action and toggle UI buttons.": "Enum Permission + bảng role_permissions + PermissionService chốt mọi thao tác của thủ thư và bật/tắt nút trên giao diện.",
 "Reader self-cancel": "Bạn đọc tự huỷ", "A reader can cancel an own AWAITING_PICKUP order, releasing the held copies.": "Bạn đọc có thể huỷ đơn AWAITING_PICKUP của mình, hoàn số lượng đang giữ.",
 # table 21 (task assignment modules)
 "Auth, Users & Permissions (+ account seeding & password change)": "Xác thực, Người dùng & Phân quyền (+ nạp tài khoản & đổi mật khẩu)",
 "Catalogue & Book Search": "Danh mục & Tìm kiếm sách",
 "Borrowing Rules, Visibility & Cart": "Quy tắc mượn, Hiển thị & Giỏ sách",
 "Loan Lifecycle, Pickup & Scheduling": "Vòng đời đơn mượn, Lấy sách & Lập lịch",
 # table 22 (prereq)
 "Compile & run": "Biên dịch & chạy", "Build & dependencies": "Build & phụ thuộc",
 "Web browser": "Trình duyệt web", "Any modern": "Bất kỳ bản hiện đại", "Access the app": "Truy cập ứng dụng",
 "8.0+ (optional)": "8.0+ (tuỳ chọn)", "Production only": "Chỉ cho sản xuất",
 # table 23 (sample accounts)
 "Users + functional-permission matrix only": "Chỉ người dùng + ma trận phân quyền chức năng",
 "Operations: pickup orders, returns & fines, book/stock, borrowing rules, in-house slips": "Nghiệp vụ: đơn chờ lấy, trả & phí, sách/tồn kho, quy tắc mượn, phiếu đọc tại chỗ",
 "Normal student": "Sinh viên thường",
 "Lock it from the admin page to test locked-account validation": "Khoá từ trang admin để kiểm thử xác thực tài khoản bị khoá",
 "Unlimited duration on textbooks": "Thời hạn không giới hạn với giáo trình",
 "Extended specialised-reference quotas": "Hạn mức mở rộng cho TLTK chuyên ngành",
 # table 24 (test scenarios)
 "Self-service borrow": "Mượn tự phục vụ", "Loan created AWAITING_PICKUP with a pickup code; copy held; no approval": "Tạo đơn AWAITING_PICKUP kèm mã lấy sách; giữ chỗ; không phê duyệt",
 "Self-confirm with WRONG code": "Tự xác nhận với mã SAI", "Rejected; loan stays AWAITING_PICKUP": "Bị từ chối; đơn vẫn AWAITING_PICKUP",
 "Self-confirm with CORRECT code": "Tự xác nhận với mã ĐÚNG", "Loan → BORROWED; due dates set; leaves awaiting list": "Đơn → BORROWED; đặt hạn trả; rời danh sách chờ",
 "No staff pickup endpoint": "Không có endpoint lấy sách cho nhân viên", "POST /librarian/loans/{id}/pickup → 404 (pickup is reader self-service)": "POST /librarian/loans/{id}/pickup → 404 (lấy sách là tự phục vụ của bạn đọc)",
 "Role separation — Admin": "Tách vai trò — Admin",
 "Role separation — Librarian": "Tách vai trò — Thủ thư",
 "Login routing": "Định tuyến đăng nhập",
 "Hide doc type for STUDENT": "Ẩn loại tài liệu với STUDENT", "Type disappears from catalogue & add-to-cart blocked": "Loại biến mất khỏi danh mục & chặn thêm vào giỏ",
 "Re-show doc type": "Hiện lại loại tài liệu", "Type reappears for the role": "Loại xuất hiện lại với vai trò",
 "Edit quota on UI": "Sửa hạn mức trên giao diện", "New max quantity enforced at submit": "Hạn mức mới được áp khi gửi phiếu",
 "Return & overdue fine": "Trả & phí quá hạn", "Fine = overdue days × 2,000 VND (full day count)": "Phí = số ngày quá hạn × 2.000đ (đếm trọn ngày)",
 "Cancel restores stock": "Huỷ hoàn lại tồn kho", "Copies returned to catalogue": "Số lượng được hoàn về danh mục",
 "In-house reading slip": "Phiếu đọc tại chỗ", "Librarian issues an in-house slip for a RESTRICTED document; copy held, due same day": "Thủ thư lập phiếu đọc tại chỗ cho tài liệu RESTRICTED; giữ chỗ, hạn trong ngày",
 "Fine collection & completion": "Thu phí & hoàn tất", "After a return with an overdue fine the loan stays BORROWED until pay-fine, then becomes COMPLETED": "Sau khi trả có phí quá hạn, đơn vẫn BORROWED cho đến khi thu phí, rồi thành COMPLETED",
 "Reader cancels an own AWAITING_PICKUP order; held copies restored": "Bạn đọc huỷ đơn AWAITING_PICKUP của mình; hoàn số lượng đang giữ",
 "In-app notification": "Thông báo in-app", "Creating / confirming / cancelling a loan produces the matching navbar notification": "Tạo / xác nhận / huỷ đơn sinh ra thông báo tương ứng trên thanh điều hướng",
 "Permission gate": "Chốt phân quyền", "Revoking BOOK_DELETE for LIBRARIAN blocks the delete action (flash error)": "Thu quyền BOOK_DELETE của LIBRARIAN sẽ chặn thao tác xoá (báo lỗi flash)",
 # table 28 (roadmap)
 "High": "Cao", "Medium": "Trung bình", "Low": "Thấp",
 "MySQL / production deployment": "Triển khai MySQL / sản xuất", "Persistent storage with Flyway migrations.": "Lưu trữ bền vững với migration Flyway.",
 "Schedule & email the overdue reminders": "Lên lịch & gửi email nhắc quá hạn", "Wire LoanService.sendDueReminders() to a scheduler and add email delivery (BM.04/BM.05 of QT.03.TV.DV).": "Gắn LoanService.sendDueReminders() vào scheduler và thêm gửi email (BM.04/BM.05 của QT.03.TV.DV).",
 "Reservation queue": "Hàng đợi đặt trước", "Reserve unavailable titles; block renewal when reserved.": "Đặt trước đầu sách đang hết; chặn gia hạn khi đã có đặt trước.",
 "QR pickup code": "Mã lấy sách QR", "Render the pickup code as a QR for faster counter handover.": "Hiển thị mã lấy sách dạng QR để giao nhận tại quầy nhanh hơn.",
 "Excel / PDF reports": "Báo cáo Excel / PDF", "Export borrowing statistics and overdue lists.": "Xuất thống kê mượn và danh sách quá hạn.",
}

def apply_runs(p):
    full = p.text
    key = full.strip()
    if key in T and p.runs:
        lead = full[:len(full)-len(full.lstrip())]
        trail = full[len(full.rstrip()):]
        p.runs[0].text = lead + T[key] + trail
        for r in p.runs[1:]:
            r.text = ""

doc = Document(SRC)
for p in doc.paragraphs:
    apply_runs(p)
for t in doc.tables:
    for row in t.rows:
        for c in row.cells:
            for p in c.paragraphs:
                apply_runs(p)
doc.save(DST)
print("SAVED", DST)
