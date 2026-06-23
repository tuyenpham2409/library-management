-- ==========================================
-- DATA SEED — Hệ thống Quản lý Thư viện
-- Đại học Phenikaa (H2, UTF-8)
-- Mật khẩu mặc định: password123 (BCrypt)
-- ==========================================

-- USERS: 2 admin, 20 sinh viên, 20 giảng viên, 20 nghiên cứu sinh, 20 thủ thư
-- Không gán id thủ công để auto_increment giữ đúng chuỗi.
INSERT INTO users (student_code, full_name, password_hash, role, card_status, created_at) VALUES
('admin',         'Quản Trị Viên 01',    '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'ADMIN',      'ACTIVE', '2026-03-01 08:00:00'),
('admin02',       'Quản Trị Viên 02',    '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'ADMIN',      'ACTIVE', '2026-03-01 08:00:00'),
('student01',     'Sinh Viên 01',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer01',    'Giảng Viên 01',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher01',  'Nghiên Cứu Sinh 01',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian01',   'Thủ Thư 01',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student02',     'Sinh Viên 02',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer02',    'Giảng Viên 02',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher02',  'Nghiên Cứu Sinh 02',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian02',   'Thủ Thư 02',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student03',     'Sinh Viên 03',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer03',    'Giảng Viên 03',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher03',  'Nghiên Cứu Sinh 03',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian03',   'Thủ Thư 03',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student04',     'Sinh Viên 04',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer04',    'Giảng Viên 04',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher04',  'Nghiên Cứu Sinh 04',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian04',   'Thủ Thư 04',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student05',     'Sinh Viên 05',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer05',    'Giảng Viên 05',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher05',  'Nghiên Cứu Sinh 05',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian05',   'Thủ Thư 05',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student06',     'Sinh Viên 06',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer06',    'Giảng Viên 06',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher06',  'Nghiên Cứu Sinh 06',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian06',   'Thủ Thư 06',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student07',     'Sinh Viên 07',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer07',    'Giảng Viên 07',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher07',  'Nghiên Cứu Sinh 07',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian07',   'Thủ Thư 07',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student08',     'Sinh Viên 08',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer08',    'Giảng Viên 08',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher08',  'Nghiên Cứu Sinh 08',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian08',   'Thủ Thư 08',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student09',     'Sinh Viên 09',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer09',    'Giảng Viên 09',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher09',  'Nghiên Cứu Sinh 09',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian09',   'Thủ Thư 09',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student10',     'Sinh Viên 10',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer10',    'Giảng Viên 10',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher10',  'Nghiên Cứu Sinh 10',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian10',   'Thủ Thư 10',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student11',     'Sinh Viên 11',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer11',    'Giảng Viên 11',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher11',  'Nghiên Cứu Sinh 11',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian11',   'Thủ Thư 11',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student12',     'Sinh Viên 12',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer12',    'Giảng Viên 12',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher12',  'Nghiên Cứu Sinh 12',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian12',   'Thủ Thư 12',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student13',     'Sinh Viên 13',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer13',    'Giảng Viên 13',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher13',  'Nghiên Cứu Sinh 13',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian13',   'Thủ Thư 13',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student14',     'Sinh Viên 14',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer14',    'Giảng Viên 14',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher14',  'Nghiên Cứu Sinh 14',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian14',   'Thủ Thư 14',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student15',     'Sinh Viên 15',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer15',    'Giảng Viên 15',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher15',  'Nghiên Cứu Sinh 15',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian15',   'Thủ Thư 15',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student16',     'Sinh Viên 16',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer16',    'Giảng Viên 16',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher16',  'Nghiên Cứu Sinh 16',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian16',   'Thủ Thư 16',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student17',     'Sinh Viên 17',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer17',    'Giảng Viên 17',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher17',  'Nghiên Cứu Sinh 17',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian17',   'Thủ Thư 17',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student18',     'Sinh Viên 18',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer18',    'Giảng Viên 18',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher18',  'Nghiên Cứu Sinh 18',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian18',   'Thủ Thư 18',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student19',     'Sinh Viên 19',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer19',    'Giảng Viên 19',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher19',  'Nghiên Cứu Sinh 19',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian19',   'Thủ Thư 19',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00'),
('student20',     'Sinh Viên 20',        '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
('lecturer20',    'Giảng Viên 20',       '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
('researcher20',  'Nghiên Cứu Sinh 20',  '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian20',   'Thủ Thư 20',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00');

-- ==========================================
-- SÁCH (24 đầu tài liệu, đủ 4 loại, số lượng đa dạng)
-- ==========================================
INSERT INTO books (id, title, author, isbn, category, doc_type, total_copies, available_copies, classification_code, description, publisher, publish_year) VALUES
(1,  'Giải Tích 1',                       'Nguyễn Đình Trí',     '978-604-0-17415-1', 'Toán học',            'TEXTBOOK',        3, 3, 'TOAN.001', 'Giáo trình giải tích dành cho sinh viên năm nhất.', 'NXB Giáo Dục',     2022),
(2,  'Đại Số Tuyến Tính',                 'Trần Vui',            '978-604-0-17416-8', 'Toán học',            'TEXTBOOK',        3, 3, 'TOAN.002', 'Giáo trình đại số tuyến tính cơ bản.', 'NXB Đại học QG',   2021),
(3,  'Vật Lý Đại Cương',                  'Lương Duyên Bình',    '978-604-0-17417-5', 'Vật lý',              'TEXTBOOK',        3, 3, 'LY.001',   'Giáo trình vật lý đại cương tập 1.', 'NXB Giáo Dục',     2020),
(4,  'Design Patterns',                   'Gang of Four',        '978-0-201-63361-0', 'Công nghệ thông tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.001', 'Cuốn sách kinh điển về 23 mẫu thiết kế phần mềm.', 'Addison-Wesley',  1994),
(5,  'Clean Code',                        'Robert C. Martin',    '978-0-13-235088-4', 'Công nghệ thông tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.002', 'Kỹ thuật viết code sạch, dễ đọc, dễ bảo trì.', 'Prentice Hall',   2008),
(6,  'Lập Trình Java Nâng Cao',           'Nguyễn Tiến Sĩ',      '978-604-0-18000-8', 'Công nghệ thông tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.003', 'Lập trình Java từ cơ bản đến nâng cao với Spring Framework.', 'NXB KHKT', 2023),
(7,  'Từ Điển Anh-Việt Oxford',           'Ban biên tập Oxford', '978-0-19-479728-3', 'Ngôn ngữ',            'GENERAL_REF',     3, 3, 'TDT.001',  'Từ điển Anh-Việt Oxford bản đầy đủ với hơn 200.000 từ.', 'Oxford UP',  2019),
(8,  'Bách Khoa Toàn Thư Việt Nam',       'Hội đồng quốc gia',   '978-604-0-19000-7', 'Bách khoa',           'GENERAL_REF',     3, 3, 'BKT.001',  'Bách khoa toàn thư Việt Nam tổng hợp tri thức trên mọi lĩnh vực.', 'NXB Bách Khoa', 2018),
(9,  'Lịch Sử Việt Nam',                  'Nguyễn Phan Quang',   '978-604-0-15000-1', 'Lịch sử',             'GENERAL_REF',     3, 3, 'LS.001',   'Lịch sử Việt Nam từ thời dựng nước đến nay.', 'NXB Sử Học',      2020),
(10, 'Luận Án Tiến Sĩ Mẫu 2024',          'Nhiều tác giả',       'PHEN-LUANAN-2024',  'Luận án nội sinh',    'RESTRICTED',      2, 2, 'NS.001',   'Tuyển tập luận án tiến sĩ lưu trữ tại thư viện Phenikaa. Chỉ đọc tại chỗ.', 'Thư viện Phenikaa', 2024),
(11, 'Nhập Môn Xác Suất Thống Kê',        'Đặng Hùng Thắng',     '978-604-0-20001-0', 'Toán học',            'TEXTBOOK',        5, 5, 'TOAN.003', 'Giáo trình xác suất thống kê căn bản.', 'NXB Giáo Dục',     2021),
(12, 'Giải Tích 2',                       'Nguyễn Đình Trí',     '978-604-0-20002-7', 'Toán học',            'TEXTBOOK',        4, 4, 'TOAN.004', 'Giáo trình giải tích phần 2.', 'NXB Giáo Dục',     2022),
(13, 'Vật Lý Đại Cương 2',                'Lương Duyên Bình',    '978-604-0-20003-4', 'Vật lý',              'TEXTBOOK',        2, 2, 'LY.002',   'Điện từ học và quang học.', 'NXB Giáo Dục',     2020),
(14, 'Hóa Học Đại Cương',                 'Nguyễn Đức Chung',    '978-604-0-20004-1', 'Hóa học',             'TEXTBOOK',        3, 3, 'HOA.001',  'Hóa học đại cương cho sinh viên kỹ thuật.', 'NXB KHKT', 2019),
(15, 'Cấu Trúc Dữ Liệu và Giải Thuật',    'Lê Minh Hoàng',       '978-604-0-20005-8', 'Công nghệ thông tin', 'SPECIALIZED_REF', 4, 4, 'CNTT.004', 'Các cấu trúc dữ liệu và thuật toán kinh điển.', 'NXB ĐHQG', 2021),
(16, 'Hệ Điều Hành',                      'Andrew Tanenbaum',    '978-604-0-20006-5', 'Công nghệ thông tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.005', 'Nguyên lý hệ điều hành hiện đại.', 'Pearson', 2018),
(17, 'Trí Tuệ Nhân Tạo',                  'Stuart Russell',      '978-604-0-20007-2', 'Công nghệ thông tin', 'SPECIALIZED_REF', 2, 2, 'CNTT.006', 'Trí tuệ nhân tạo - một cách tiếp cận hiện đại.', 'Pearson', 2020),
(18, 'Kinh Tế Vi Mô',                     'Gregory Mankiw',      '978-604-0-20008-9', 'Kinh tế',             'SPECIALIZED_REF', 3, 3, 'KT.001',   'Nguyên lý kinh tế vi mô.', 'Cengage', 2019),
(19, 'Từ Điển Pháp-Việt',                 'Lê Khả Kế',           '978-604-0-20009-6', 'Ngôn ngữ',            'GENERAL_REF',     2, 2, 'TDT.002',  'Từ điển Pháp-Việt thông dụng.', 'NXB Giáo Dục', 2017),
(20, 'Atlas Địa Lý Thế Giới',             'Nhiều tác giả',       '978-604-0-20010-2', 'Địa lý',              'GENERAL_REF',     3, 3, 'DL.001',   'Atlas địa lý thế giới đầy đủ.', 'NXB Bản Đồ', 2020),
(21, 'Sổ Tay Tra Cứu Hóa Học',            'Nguyễn Văn An',       '978-604-0-20011-9', 'Hóa học',             'GENERAL_REF',     2, 2, 'HOA.TC.001','Sổ tay công thức và hằng số hóa học.', 'NXB KHKT', 2018),
(22, 'Luận Văn Thạc Sĩ CNTT 2023',        'Trần Văn Bình',       'PHEN-LVThS-2023',   'Luận văn nội sinh',   'RESTRICTED',      1, 1, 'NS.002',   'Luận văn thạc sĩ lưu trữ tại thư viện, chỉ đọc tại chỗ.', 'Thư viện Phenikaa', 2023),
(23, 'Báo Cáo Nghiên Cứu Khoa Học 2024',  'Hội đồng khoa học',   'PHEN-BCKH-2024',    'Báo cáo nội sinh',    'RESTRICTED',      2, 2, 'NS.003',   'Tuyển tập báo cáo nghiên cứu khoa học cấp trường.', 'Thư viện Phenikaa', 2024),
(24, 'Lập Trình Web với Spring Boot',     'Nguyễn Tiến Sĩ',      '978-604-0-20012-6', 'Công nghệ thông tin', 'SPECIALIZED_REF', 5, 5, 'CNTT.007', 'Xây dựng ứng dụng web với Spring Boot 3.', 'NXB KHKT', 2024);

ALTER TABLE books ALTER COLUMN id RESTART WITH 100;

-- ==========================================
-- QUY TẮC MƯỢN (ma trận vai trò × loại tài liệu)
-- ==========================================
INSERT INTO borrowing_rules (id, user_role, doc_type, max_quantity, borrow_days, max_renewals, renewal_days, visible) VALUES
(1,  'STUDENT',    'TEXTBOOK',        10, 150, 0, NULL, TRUE),
(2,  'STUDENT',    'SPECIALIZED_REF',  5,  90, 2,    3, TRUE),
(3,  'STUDENT',    'GENERAL_REF',      3,  15, 0, NULL, TRUE),
(4,  'STUDENT',    'RESTRICTED',       0,   0, 0, NULL, TRUE),
(5,  'LECTURER',   'TEXTBOOK',        10, NULL, 0, NULL, TRUE),
(6,  'LECTURER',   'SPECIALIZED_REF',  5, NULL, 0, NULL, TRUE),
(7,  'LECTURER',   'GENERAL_REF',      5,   15, 2,    3, TRUE),
(8,  'LECTURER',   'RESTRICTED',       0,    0, 0, NULL, TRUE),
(9,  'RESEARCHER', 'TEXTBOOK',         5, 150, 0, NULL, TRUE),
(10, 'RESEARCHER', 'SPECIALIZED_REF', 10,  30, 2,    3, TRUE),
(11, 'RESEARCHER', 'GENERAL_REF',     10,  15, 0, NULL, TRUE),
(12, 'RESEARCHER', 'RESTRICTED',       0,   0, 0, NULL, TRUE);

-- ==========================================
-- PHÂN QUYỀN MẶC ĐỊNH (vai trò × quyền)
-- ==========================================
INSERT INTO role_permissions (user_role, permission, granted) VALUES
('STUDENT',    'BORROW',            TRUE),
('STUDENT',    'RENEW',             TRUE),
('STUDENT',    'CANCEL_OWN',        TRUE),
('LECTURER',   'BORROW',            TRUE),
('LECTURER',   'RENEW',             TRUE),
('LECTURER',   'CANCEL_OWN',        TRUE),
('RESEARCHER', 'BORROW',            TRUE),
('RESEARCHER', 'RENEW',             TRUE),
('RESEARCHER', 'CANCEL_OWN',        TRUE),
('LIBRARIAN',  'BOOK_CREATE',       TRUE),
('LIBRARIAN',  'BOOK_UPDATE',       TRUE),
('LIBRARIAN',  'BOOK_DELETE',       TRUE),
('LIBRARIAN',  'LOAN_CONFIRM_PICKUP', TRUE),
('LIBRARIAN',  'LOAN_RETURN',       TRUE),
('LIBRARIAN',  'LOAN_PAY_FINE',     TRUE),
('LIBRARIAN',  'LOAN_CANCEL',       TRUE),
('LIBRARIAN',  'LOAN_LEND_INHOUSE', TRUE),
('LIBRARIAN',  'RULE_MANAGE',       TRUE);

-- ==========================================
-- ĐƠN MƯỢN MẪU (đa trạng thái)
-- user_id: student01=3, lecturer01=4, researcher01=5, student02=7, lecturer02=8,
--          student03=11, student04=15, student05=19
-- ==========================================
INSERT INTO loans (id, user_id, created_at, pickup_code, pickup_deadline, picked_up_at, status, cancel_reason, cancelled_by_role, in_house) VALUES
(1, 3,  '2026-06-14 08:00:00', 'ABC123', '2026-06-15 08:00:00', NULL,                  'AWAITING_PICKUP', NULL, NULL, FALSE),
(2, 7,  '2026-06-01 10:00:00', 'BRW222', '2026-06-02 10:00:00', '2026-06-01 11:00:00', 'BORROWED',        NULL, NULL, FALSE),
(3, 4,  '2026-05-20 09:00:00', 'LCT333', '2026-05-21 09:00:00', '2026-05-20 10:00:00', 'BORROWED',        NULL, NULL, FALSE),
(4, 5,  '2026-05-01 09:00:00', 'RSC444', '2026-05-02 09:00:00', '2026-05-01 10:00:00', 'BORROWED',        NULL, NULL, FALSE),
(5, 11, '2026-03-10 09:00:00', 'CMP555', '2026-03-11 09:00:00', '2026-03-10 10:00:00', 'COMPLETED',       NULL, NULL, FALSE),
(6, 15, '2026-04-01 09:00:00', 'FIN666', '2026-04-02 09:00:00', '2026-04-01 10:00:00', 'BORROWED',        NULL, NULL, FALSE),
(7, 19, '2026-06-10 09:00:00', 'CNL777', '2026-06-11 09:00:00', NULL,                  'CANCELLED', 'Sách yêu cầu đang được kiểm kê, vui lòng mượn lại sau.', 'LIBRARIAN', FALSE),
(8, 8,  '2026-06-09 09:00:00', 'CNL888', '2026-06-10 09:00:00', NULL,                  'CANCELLED', 'Bạn đọc tự huỷ',                                         'LECTURER',  FALSE);

ALTER TABLE loans ALTER COLUMN id RESTART WITH 100;

-- Chi tiết đơn: (loan_id, book_id, due_date, return_date, renewal_count, condition_status, fine_amount, fine_paid, status)
INSERT INTO loan_details (loan_id, book_id, due_date, return_date, renewal_count, condition_status, fine_amount, fine_paid, status) VALUES
(1, 1,  NULL,         NULL,                  0, 'GOOD', 0,    FALSE, 'RESERVED'),
(1, 4,  NULL,         NULL,                  0, 'GOOD', 0,    FALSE, 'RESERVED'),
(2, 2,  '2026-10-29', NULL,                  0, 'GOOD', 0,    FALSE, 'BORROWING'),
(2, 5,  '2026-06-16', NULL,                  0, 'GOOD', 0,    FALSE, 'BORROWING'),
(3, 3,  NULL,         NULL,                  0, 'GOOD', 0,    FALSE, 'BORROWING'),
(4, 6,  '2026-06-04', NULL,                  0, 'GOOD', 0,    FALSE, 'BORROWING'),
(5, 7,  '2026-08-07', '2026-03-20 09:00:00', 0, 'GOOD', 0,    TRUE,  'RETURNED'),
(6, 8,  '2026-04-16', '2026-04-20 14:00:00', 0, 'GOOD', 8000, FALSE, 'RETURNED'),
(7, 9,  NULL,         NULL,                  0, 'GOOD', 0,    FALSE, 'RESERVED'),
(8, 24, NULL,         NULL,                  0, 'GOOD', 0,    FALSE, 'RESERVED');
-- Trừ tồn kho cho sách đang được giữ chỗ / đang mượn / đang đọc tại chỗ
UPDATE books SET available_copies = 2 WHERE id IN (1, 2, 3, 4, 5, 6);
UPDATE books SET available_copies = 0 WHERE id = 22;

-- ==========================================
-- THÔNG BÁO MẪU
-- ==========================================
INSERT INTO notifications (user_id, type, message, link, is_read, created_at) VALUES
(3,  'SUCCESS', 'Tạo đơn mượn #1 thành công. Mã mượn: ABC123. Vui lòng đến thư viện lấy sách trong vòng 24 giờ.', '/client/my-loans', FALSE, '2026-06-14 08:00:00'),
(6,  'INFO',    'Đơn mượn mới #1 từ Sinh Viên 01 (student01) — 2 cuốn.', '/librarian/loans?status=AWAITING_PICKUP', FALSE, '2026-06-14 08:00:00'),
(15, 'WARNING', 'Đã trả "Bách Khoa Toàn Thư Việt Nam" (đơn #6). Phí phạt 8.000 VND — vui lòng thanh toán tại quầy.', '/client/my-loans', FALSE, '2026-04-20 14:00:00'),
(11, 'INFO',    'Bạn đang đọc tại chỗ "Luận Văn Thạc Sĩ CNTT 2023" (ghi nhận ngày 14/06/2026).', '/client/my-loans', FALSE, '2026-06-14 09:30:00');
