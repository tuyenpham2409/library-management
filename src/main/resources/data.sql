-- ==========================================
-- DATA SEED — Library Management System
-- Phenikaa University (H2 Compatible)
-- BCrypt hash of "password123"
-- ==========================================

-- USERS
INSERT INTO users (id, student_code, full_name, password_hash, role, card_status, created_at) VALUES
(1, 'admin',        'Nguyen Van Admin',           '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'ADMIN',      'ACTIVE', '2026-03-01 08:00:00'),
(2, 'student01',    'Tran Thi Sinh Vien',          '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
(3, 'student02',    'Le Van Bi Khoa',              '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'STUDENT',    'ACTIVE', '2026-03-01 08:00:00'),
(4, 'lecturer01',   'PGS.TS. Pham Van Giang Vien', '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LECTURER',   'ACTIVE', '2026-03-01 08:00:00'),
(5, 'researcher01', 'TS. Hoang Thi Nghien Cuu',    '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'RESEARCHER', 'ACTIVE', '2026-03-01 08:00:00'),
(6, 'student03',    'Pham Minh Tuyen',             '$2a$12$mXAkEAWvOTDeXveyavkso.vPtJWvLN7hEsEO9dMYG2zg7XUSVDd2O', 'STUDENT',    'ACTIVE', '2026-05-06 23:27:15'),
(7, 'librarian01',  'Do Thi Thu Thu',              '$2b$10$XQoWyXc0qhA0zv9TEn6WK.48bSdFr/l428Bo.POvlBHRZZEIphkGS', 'LIBRARIAN',  'ACTIVE', '2026-03-01 08:00:00');

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
