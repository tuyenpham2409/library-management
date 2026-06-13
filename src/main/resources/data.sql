-- ==========================================
-- DATA SEED — Library Management System
-- Phenikaa University (H2 Compatible)
-- BCrypt hash of "password123"
-- ==========================================

-- USERS (mat khau mac dinh: password123 — nguoi dung dang nhap roi tu doi tai /account/password)
-- 2 admin, 20 sinh vien, 20 giang vien, 20 nghien cuu sinh, 20 thu thu
-- Khong gan id thu cong -> de auto_increment giu dung chuoi (tao user moi tren UI khong bi trung)
INSERT INTO users (student_code, full_name, password_hash, role, card_status, created_at) VALUES
('admin', 'Quan Tri Vien 01', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'ADMIN', 'ACTIVE', '2026-03-01 08:00:00'),
('admin02', 'Quan Tri Vien 02', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'ADMIN', 'ACTIVE', '2026-03-01 08:00:00'),
('student01', 'Sinh Vien 01', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer01', 'Giang Vien 01', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher01', 'Nghien Cuu Sinh 01', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian01', 'Thu Thu 01', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student02', 'Sinh Vien 02', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer02', 'Giang Vien 02', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher02', 'Nghien Cuu Sinh 02', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian02', 'Thu Thu 02', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student03', 'Sinh Vien 03', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer03', 'Giang Vien 03', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher03', 'Nghien Cuu Sinh 03', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian03', 'Thu Thu 03', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student04', 'Sinh Vien 04', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer04', 'Giang Vien 04', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher04', 'Nghien Cuu Sinh 04', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian04', 'Thu Thu 04', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student05', 'Sinh Vien 05', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer05', 'Giang Vien 05', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher05', 'Nghien Cuu Sinh 05', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian05', 'Thu Thu 05', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student06', 'Sinh Vien 06', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer06', 'Giang Vien 06', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher06', 'Nghien Cuu Sinh 06', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian06', 'Thu Thu 06', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student07', 'Sinh Vien 07', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer07', 'Giang Vien 07', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher07', 'Nghien Cuu Sinh 07', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian07', 'Thu Thu 07', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student08', 'Sinh Vien 08', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer08', 'Giang Vien 08', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher08', 'Nghien Cuu Sinh 08', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian08', 'Thu Thu 08', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student09', 'Sinh Vien 09', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer09', 'Giang Vien 09', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher09', 'Nghien Cuu Sinh 09', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian09', 'Thu Thu 09', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student10', 'Sinh Vien 10', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer10', 'Giang Vien 10', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher10', 'Nghien Cuu Sinh 10', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian10', 'Thu Thu 10', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student11', 'Sinh Vien 11', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer11', 'Giang Vien 11', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher11', 'Nghien Cuu Sinh 11', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian11', 'Thu Thu 11', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student12', 'Sinh Vien 12', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer12', 'Giang Vien 12', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher12', 'Nghien Cuu Sinh 12', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian12', 'Thu Thu 12', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student13', 'Sinh Vien 13', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer13', 'Giang Vien 13', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher13', 'Nghien Cuu Sinh 13', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian13', 'Thu Thu 13', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student14', 'Sinh Vien 14', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer14', 'Giang Vien 14', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher14', 'Nghien Cuu Sinh 14', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian14', 'Thu Thu 14', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student15', 'Sinh Vien 15', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer15', 'Giang Vien 15', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher15', 'Nghien Cuu Sinh 15', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian15', 'Thu Thu 15', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student16', 'Sinh Vien 16', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer16', 'Giang Vien 16', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher16', 'Nghien Cuu Sinh 16', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian16', 'Thu Thu 16', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student17', 'Sinh Vien 17', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer17', 'Giang Vien 17', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher17', 'Nghien Cuu Sinh 17', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian17', 'Thu Thu 17', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student18', 'Sinh Vien 18', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer18', 'Giang Vien 18', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher18', 'Nghien Cuu Sinh 18', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian18', 'Thu Thu 18', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student19', 'Sinh Vien 19', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer19', 'Giang Vien 19', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher19', 'Nghien Cuu Sinh 19', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian19', 'Thu Thu 19', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00'),
('student20', 'Sinh Vien 20', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT', 'ACTIVE', '2026-03-01 08:00:00'),
('lecturer20', 'Giang Vien 20', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER', 'ACTIVE', '2026-03-01 08:00:00'),
('researcher20', 'Nghien Cuu Sinh 20', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
('librarian20', 'Thu Thu 20', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN', 'ACTIVE', '2026-03-01 08:00:00');

-- BOOKS (10 cuon du 4 loai)
INSERT INTO books (id, title, author, isbn, category, doc_type, total_copies, available_copies, classification_code, description, publisher, publish_year) VALUES
(1,  'Giai Tich 1',                 'Nguyen Dinh Tri',     '978-604-0-17415-1', 'Toan hoc',            'TEXTBOOK',        3, 3, 'TOAN.001', 'Giao trinh giai tich danh cho sinh vien nam nhat.', 'NXB Giao Duc',    2022),
(2,  'Dai So Tuyen Tinh',           'Tran Vui',            '978-604-0-17416-8', 'Toan hoc',            'TEXTBOOK',        3, 3, 'TOAN.002', 'Giao trinh dai so tuyen tinh co ban.', 'NXB Dai hoc QG',  2021),
(3,  'Vat Ly Dai Cuong',            'Luong Duyen Binh',    '978-604-0-17417-5', 'Vat ly',              'TEXTBOOK',        3, 3, 'LY.001',   'Giao trinh vat ly dai cuong tap 1.', 'NXB Giao Duc',    2020),
(4,  'Design Patterns',             'Gang of Four',        '978-0-201-63361-0', 'Cong nghe thong tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.001', 'Cuon sach kinh dien ve 23 mau thiet ke phan mem.', 'Addison-Wesley',  1994),
(5,  'Clean Code',                  'Robert C. Martin',    '978-0-13-235088-4', 'Cong nghe thong tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.002', 'Ky thuat viet code sach, de doc, de bao tri.', 'Prentice Hall',   2008),
(6,  'Lap Trinh Java Nang Cao',     'Nguyen Tien Si',      '978-604-0-18000-8', 'Cong nghe thong tin', 'SPECIALIZED_REF', 3, 3, 'CNTT.003', 'Lap trinh Java tu co ban den nang cao voi Spring Framework.', 'NXB KHKT',        2023),
(7,  'Tu Dien Anh-Viet Oxford',     'Ban bien tap Oxford', '978-0-19-479728-3', 'Ngon ngu',            'GENERAL_REF',     3, 3, 'TDT.001',  'Tu dien Anh-Viet Oxford ban day du voi hon 200,000 tu.', 'Oxford UP', 2019),
(8,  'Bach Khoa Toan Thu Viet Nam', 'Hoi dong quoc gia',   '978-604-0-19000-7', 'Bach khoa',           'GENERAL_REF',     3, 3, 'BKT.001',  'Bach khoa toan thu Viet Nam tong hop tri thuc tren moi linh vuc.', 'NXB Bach Khoa',   2018),
(9,  'Lich Su Viet Nam',            'Nguyen Phan Quang',   '978-604-0-15000-1', 'Lich su',             'GENERAL_REF',     3, 3, 'LS.001',   'Lich su Viet Nam tu thoi dung nuoc den nay.', 'NXB Su Hoc',      2020),
(10, 'Luan An Tien Si Mau 2024',    'Nhieu tac gia',       'PHEN-LUANAN-2024',  'Luan an noi sinh',    'RESTRICTED',      2, 2, 'NS.001',   'Tuyen tap luan an tien si duoc luu tru tai thu vien Phenikaa. Chi doc tai cho.', 'Thu vien Phenikaa', 2024);

-- BORROWING RULES (12 dong theo ma tran chinh xac tu PDF)
-- Cot visible: TRUE = vai tro duoc nhin thay/muon loai TL nay (admin bat/tat tren UI)
-- STUDENT
INSERT INTO borrowing_rules (id, user_role, doc_type, max_quantity, borrow_days, max_renewals, renewal_days, visible) VALUES
(1,  'STUDENT',    'TEXTBOOK',        10, 150, 0, NULL, TRUE),
(2,  'STUDENT',    'SPECIALIZED_REF',  5,  90, 2,    3, TRUE),
(3,  'STUDENT',    'GENERAL_REF',      3,  15, 0, NULL, TRUE),
(4,  'STUDENT',    'RESTRICTED',       0,   0, 0, NULL, TRUE);

-- LECTURER
INSERT INTO borrowing_rules (id, user_role, doc_type, max_quantity, borrow_days, max_renewals, renewal_days, visible) VALUES
(5,  'LECTURER',   'TEXTBOOK',        10, NULL, 0, NULL, TRUE),
(6,  'LECTURER',   'SPECIALIZED_REF',  5, NULL, 0, NULL, TRUE),
(7,  'LECTURER',   'GENERAL_REF',      5,   15, 2,    3, TRUE),
(8,  'LECTURER',   'RESTRICTED',       0,    0, 0, NULL, TRUE);

-- RESEARCHER
INSERT INTO borrowing_rules (id, user_role, doc_type, max_quantity, borrow_days, max_renewals, renewal_days, visible) VALUES
(9,  'RESEARCHER', 'TEXTBOOK',         5, 150, 0, NULL, TRUE),
(10, 'RESEARCHER', 'SPECIALIZED_REF', 10,  30, 2,    3, TRUE),
(11, 'RESEARCHER', 'GENERAL_REF',     10,  15, 0, NULL, TRUE),
(12, 'RESEARCHER', 'RESTRICTED',       0,   0, 0, NULL, TRUE);
