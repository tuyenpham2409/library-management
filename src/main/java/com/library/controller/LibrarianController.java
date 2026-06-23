package com.library.controller;

import com.library.dto.BookForm;
import com.library.dto.ReturnBatchItem;
import com.library.dto.RuleConfigForm;
import com.library.dto.ValidationResult;
import com.library.entity.*;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.PermissionService;
import com.library.service.RuleService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Khu vận hành của THỦ THƯ: đơn mượn, trả sách, thu phí, quản lý sách, quy tắc mượn.
 * Tách hẳn khỏi khu Admin (tài khoản + phân quyền).
 */
@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private final BookService bookService;
    private final LoanService loanService;
    private final RuleService ruleService;
    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public LibrarianController(BookService bookService, LoanService loanService,
                               RuleService ruleService, PermissionService permissionService,
                               UserRepository userRepository) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.ruleService = ruleService;
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByStudentCode(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /** Trả về true (và set flash error) nếu thủ thư KHÔNG có quyền yêu cầu. */
    private boolean lacks(Authentication auth, Permission p, RedirectAttributes ra) {
        if (!permissionService.has(getCurrentUser(auth).getRole(), p)) {
            ra.addFlashAttribute("error", "Bạn không được cấp quyền thực hiện thao tác này.");
            return true;
        }
        return false;
    }

    // =========== DASHBOARD ===========

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("awaitingCount", loanService.countAwaitingPickup());
        model.addAttribute("activeCount", loanService.countActiveLoans());
        model.addAttribute("overdueList", loanService.findAllOverdue());
        model.addAttribute("borrowingCount", loanService.countBorrowingDetails());
        return "librarian/dashboard";
    }

    // =========== QUẢN LÝ ĐƠN MƯỢN (có filter trạng thái) ===========

    @GetMapping("/loans")
    public String loans(@RequestParam(required = false) LoanStatus status, Model model) {
        List<Loan> loans = (status == null)
                ? loanService.findAll()
                : loanService.findByStatus(status);
        model.addAttribute("loans", loans);
        model.addAttribute("statuses", LoanStatus.values());
        model.addAttribute("selectedStatus", status);
        return "librarian/loans";
    }

    @PostMapping("/loans/confirm-pickup")
    public String confirmPickup(@RequestParam String code,
                                Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_CANCEL, ra)) return "redirect:/librarian/loans";
        if (code == null || code.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập mã mượn.");
            return "redirect:/librarian/loans";
        }
        boolean ok = loanService.confirmPickupByLibrarian(code.trim());
        if (ok) {
            ra.addFlashAttribute("success",
                    "Xác nhận bàn giao sách thành công cho đơn mã " + code.trim().toUpperCase()
                            + ". Đơn đã chuyển sang Đang mượn.");
        } else {
            ra.addFlashAttribute("error",
                    "Không tìm thấy đơn mượn đang chờ lấy với mã: " + code.trim().toUpperCase());
        }
        return "redirect:/librarian/loans?status=BORROWED";
    }

    @PostMapping("/loans/{id}/cancel")
    public String cancelLoan(@PathVariable Long id, @RequestParam String reason,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_CANCEL, ra)) return "redirect:/librarian/loans";
        if (reason == null || reason.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập lý do huỷ đơn.");
            return "redirect:/librarian/loans";
        }
        try {
            loanService.cancelLoan(id, reason.trim(), getCurrentUser(auth).getRole());
            ra.addFlashAttribute("success", "Đã huỷ đơn mượn #" + id + " và gửi lý do cho bạn đọc.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/librarian/loans#loan" + id;
    }

    @PostMapping("/loans/details/{id}/return")
    public String returnBook(@PathVariable Long id,
                             @RequestParam(defaultValue = "GOOD") BookCondition condition,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_RETURN, ra)) return "redirect:/librarian/loans";
        try {
            java.math.BigDecimal fine = loanService.returnBook(id, condition);
            if (fine.compareTo(java.math.BigDecimal.ZERO) > 0) {
                ra.addFlashAttribute("warn", "Đã trả sách. Còn phí phạt " +
                        String.format("%,.0f", fine) + " VND — bấm 'Đã thu phí' khi bạn đọc thanh toán.");
            } else {
                ra.addFlashAttribute("success", "Trả sách thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/librarian/loans";
    }

    @PostMapping("/loans/details/{id}/pay-fine")
    @ResponseBody
    public Map<String, Object> payFine(@PathVariable Long id, Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.LOAN_PAY_FINE)) {
            resp.put("ok", false);
            resp.put("message", "Bạn không được cấp quyền thực hiện thao tác này.");
            return resp;
        }
        try {
            boolean ok = loanService.payFine(id);
            resp.put("ok", ok);
            resp.put("message", ok ? "Đã ghi nhận thu phí." : "Không có phí cần thu hoặc đã thu trước đó.");
            if (ok) {
                loanService.findDetailById(id).ifPresent(detail -> {
                    Loan loan = detail.getLoan();
                    resp.put("loanId", loan.getId());
                    boolean loanCompleted = loan.getStatus() == LoanStatus.COMPLETED;
                    resp.put("loanCompleted", loanCompleted);
                    resp.put("bookTitle", detail.getBook().getTitle());
                    resp.put("fineFormatted", String.format("%,.0f đ", detail.getFineAmount()));
                    if (detail.getFinePaidAt() != null) {
                        resp.put("finePaidAtFormatted", detail.getFinePaidAt()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    }
                    if (loanCompleted && loan.getCompletedAt() != null) {
                        resp.put("completedAtFormatted", loan.getCompletedAt()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    }
                });
            }
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Lỗi: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/loans/{loanId}/return-batch")
    @ResponseBody
    public Map<String, Object> returnBatch(@PathVariable Long loanId,
                                           @RequestBody List<ReturnBatchItem> items,
                                           Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.LOAN_RETURN)) {
            resp.put("ok", false);
            resp.put("message", "Bạn không được cấp quyền thực hiện thao tác này.");
            resp.put("results", List.of());
            resp.put("loanCompleted", false);
            return resp;
        }
        try {
            List<Map<String, Object>> results = loanService.returnBatch(loanId, items);

            long totalFine = results.stream()
                    .mapToLong(r -> ((Number) r.get("fine")).longValue()).sum();

            Loan updatedLoan = loanService.findById(loanId).orElse(null);
            boolean completed = updatedLoan != null && updatedLoan.getStatus() == LoanStatus.COMPLETED;
            if (completed && updatedLoan.getCompletedAt() != null) {
                resp.put("completedAtFormatted", updatedLoan.getCompletedAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }

            String message;
            if (results.isEmpty()) {
                message = "Không có quyển nào ở trạng thái Đang mượn để xử lý.";
            } else if (totalFine > 0) {
                message = "Đã trả " + results.size() + " quyển. Tổng phí phạt: "
                        + String.format("%,d", totalFine) + " VND.";
            } else {
                message = "Đã trả " + results.size() + " quyển thành công.";
            }

            resp.put("ok", true);
            resp.put("results", results);
            resp.put("loanCompleted", completed);
            resp.put("message", message);
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Lỗi: " + e.getMessage());
            resp.put("results", List.of());
            resp.put("loanCompleted", false);
        }
        return resp;
    }

    // =========== QUẢN LÝ SÁCH ===========

    @GetMapping("/books")
    public String listBooks(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) DocType docType,
                            @RequestParam(defaultValue = "false") boolean availableOnly,
                            Model model) {
        List<Book> books = (keyword == null && docType == null && !availableOnly)
                ? bookService.findAll()
                : bookService.search(keyword, docType, availableOnly);
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDocType", docType);
        model.addAttribute("availableOnly", availableOnly);
        model.addAttribute("docTypes", DocType.values());
        return "librarian/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", true);
        return "librarian/book-form";
    }

    @GetMapping("/books/new-batch")
    public String newBatchForm(Model model) {
        model.addAttribute("docTypes", DocType.values());
        return "librarian/book-batch";
    }

    @PostMapping("/books/batch")
    @ResponseBody
    public Map<String, Object> createBatch(@RequestBody List<BookForm> forms,
                                           Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.BOOK_CREATE)) {
            resp.put("ok", false);
            resp.put("message", "Bạn không được cấp quyền thực hiện thao tác này.");
            return resp;
        }
        try {
            List<Book> saved = bookService.saveAll(forms);
            resp.put("ok", true);
            resp.put("count", saved.size());
            resp.put("message", "Đã thêm " + saved.size() + " sách thành công!");
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Lỗi: " + e.getMessage());
        }
        return resp;
    }

    /** Tải file Excel mẫu để nhập danh sách sách. */
    @GetMapping("/books/import-template")
    public void downloadBookTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=mau_sach.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sách sách");

            CellStyle headerStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle noteStyle = wb.createCellStyle();
            Font italicFont = wb.createFont();
            italicFont.setItalic(true);
            italicFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            noteStyle.setFont(italicFont);

            String[] headers = {"Tên sách *", "Tác giả *", "ISBN", "Loại TL *", "Mã phân loại", "Danh mục", "Nhà xuất bản", "Năm", "Tổng số", "Còn sẵn"};
            int[]    widths  = {8000, 6000, 4500, 5000, 4000, 5000, 5000, 2500, 2500, 2500};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, widths[i]);
            }

            // Dòng mẫu 1
            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue("Giải Tích 1");
            r1.createCell(1).setCellValue("Nguyễn Đình Trí");
            r1.createCell(2).setCellValue("978-604-0-17415-1");
            r1.createCell(3).setCellValue("TEXTBOOK");
            r1.createCell(4).setCellValue("TOAN.001");
            r1.createCell(5).setCellValue("Toán học");
            r1.createCell(6).setCellValue("NXB Giáo Dục");
            r1.createCell(7).setCellValue(2022);
            r1.createCell(8).setCellValue(3);
            r1.createCell(9).setCellValue(3);

            // Dòng mẫu 2
            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue("Design Patterns");
            r2.createCell(1).setCellValue("Gang of Four");
            r2.createCell(2).setCellValue("978-0-201-63361-0");
            r2.createCell(3).setCellValue("SPECIALIZED_REF");
            r2.createCell(4).setCellValue("CNTT.001");
            r2.createCell(5).setCellValue("Công nghệ thông tin");
            r2.createCell(6).setCellValue("Addison-Wesley");
            r2.createCell(7).setCellValue(1994);
            r2.createCell(8).setCellValue(2);
            r2.createCell(9).setCellValue(2);

            // Ghi chú
            Row noteRow = sheet.createRow(4);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue(
                "Loại TL hợp lệ: TEXTBOOK (Giáo trình) | SPECIALIZED_REF (TK Chuyên ngành) " +
                "| GENERAL_REF (TK Tra cứu) | RESTRICTED (Nội sinh). " +
                "Tổng số / Còn sẵn để trống → mặc định 1.");
            noteCell.setCellStyle(noteStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(4, 4, 0, 9));

            wb.write(response.getOutputStream());
        }
    }

    /** Parse file Excel sách → JSON preview (có validate). */
    @PostMapping("/books/import-preview")
    @ResponseBody
    public Map<String, Object> importBookPreview(@RequestParam("file") MultipartFile file,
                                                 Authentication auth) {
        Map<String, Object> resp = new HashMap<>();
        if (!permissionService.has(getCurrentUser(auth).getRole(), Permission.BOOK_CREATE)) {
            resp.put("ok", false); resp.put("message", "Không có quyền."); return resp;
        }
        if (file.isEmpty()) {
            resp.put("ok", false); resp.put("message", "Chưa chọn file."); return resp;
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            Set<String> validDocTypes = Set.of("TEXTBOOK", "SPECIALIZED_REF", "GENERAL_REF", "RESTRICTED");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String title    = cellStr(row, 0);
                String author   = cellStr(row, 1);
                String isbn     = cellStr(row, 2);
                String docType  = cellStr(row, 3).toUpperCase();
                String classCode = cellStr(row, 4);
                String category  = cellStr(row, 5);
                String publisher = cellStr(row, 6);
                String yearStr   = cellStr(row, 7);
                String totalStr  = cellStr(row, 8);
                String availStr  = cellStr(row, 9);
                if (title.isBlank() && author.isBlank() && docType.isBlank()) continue;

                List<String> errors = new ArrayList<>();
                if (title.isBlank())   errors.add("Thiếu tên sách");
                if (author.isBlank())  errors.add("Thiếu tác giả");
                if (docType.isBlank()) {
                    errors.add("Thiếu loại TL");
                } else if (!validDocTypes.contains(docType)) {
                    errors.add("Loại TL không hợp lệ: " + docType);
                }
                int total = totalStr.isBlank() ? 1 : parseIntSafe(totalStr, -1);
                int avail = availStr.isBlank() ? total : parseIntSafe(availStr, -1);
                if (total < 0) errors.add("Tổng số phải là số nguyên dương");
                if (avail < 0) errors.add("Còn sẵn phải là số nguyên dương");
                if (!errors.isEmpty() || (total >= 0 && avail >= 0 && avail > total))
                    if (avail > total && total >= 0) errors.add("Còn sẵn không được vượt quá tổng số");

                Map<String, Object> r = new LinkedHashMap<>();
                r.put("title",              title);
                r.put("author",             author);
                r.put("isbn",               isbn);
                r.put("docType",            docType);
                r.put("classificationCode", classCode);
                r.put("category",           category);
                r.put("publisher",          publisher);
                r.put("publishYear",        yearStr.isBlank() ? null : parseIntSafe(yearStr, 0));
                r.put("totalCopies",        total < 0 ? 1 : total);
                r.put("availableCopies",    avail < 0 ? 1 : avail);
                r.put("errors",             errors);
                r.put("valid",              errors.isEmpty());
                rows.add(r);
            }
            resp.put("ok", true);
            resp.put("rows", rows);
            resp.put("validCount",   rows.stream().filter(r -> Boolean.TRUE.equals(r.get("valid"))).count());
            resp.put("invalidCount", rows.stream().filter(r -> !Boolean.TRUE.equals(r.get("valid"))).count());
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("message", "Không đọc được file: " + e.getMessage());
        }
        return resp;
    }

    private static String cellStr(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                yield (v == Math.floor(v)) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }

    private static int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute BookForm form, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_CREATE, ra)) return "redirect:/librarian/books";
        bookService.save(form);
        ra.addFlashAttribute("success", "Đã thêm sách '" + form.getTitle() + "'!");
        return "redirect:/librarian/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        model.addAttribute("bookForm", bookService.toForm(book));
        model.addAttribute("docTypes", DocType.values());
        model.addAttribute("isNew", false);
        return "librarian/book-form";
    }

    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id, @ModelAttribute BookForm form,
                             Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_UPDATE, ra)) return "redirect:/librarian/books";
        bookService.update(id, form);
        ra.addFlashAttribute("success", "Đã cập nhật sách '" + form.getTitle() + "'!");
        return "redirect:/librarian/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.BOOK_DELETE, ra)) return "redirect:/librarian/books";
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookService.delete(id);
        ra.addFlashAttribute("success", "Đã xóa sách '" + book.getTitle() + "'.");
        return "redirect:/librarian/books";
    }

    // =========== QUY TẮC MƯỢN (rule mượn sách) ===========

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        RuleConfigForm form = new RuleConfigForm();
        form.setRules(ruleService.findAllOrdered());
        model.addAttribute("form", form);
        return "librarian/rules";
    }

    @PostMapping("/rules")
    public String saveRules(@ModelAttribute RuleConfigForm form, Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.RULE_MANAGE, ra)) return "redirect:/librarian/rules";
        ruleService.updateRules(form.getRules());
        ra.addFlashAttribute("success", "Đã lưu quy tắc mượn.");
        return "redirect:/librarian/rules";
    }

    // =========== CHO MƯỢN ĐỌC TẠI CHỖ (chỉ tài liệu nội sinh) ===========

    @GetMapping("/inhouse")
    public String inhousePage(Model model) {
        // Chỉ tài liệu nội sinh (RESTRICTED) mới đọc tại chỗ
        model.addAttribute("restrictedBooks", bookService.search(null, DocType.RESTRICTED, false));
        model.addAttribute("activeInhouse", loanService.findInHouseActive());
        return "librarian/inhouse";
    }

    @PostMapping("/inhouse")
    public String lendInHouse(@RequestParam String studentCode, @RequestParam Long bookId,
                              Authentication auth, RedirectAttributes ra) {
        if (lacks(auth, Permission.LOAN_LEND_INHOUSE, ra)) return "redirect:/librarian/inhouse";
        User reader = userRepository.findByStudentCode(studentCode.trim()).orElse(null);
        if (reader == null) {
            ra.addFlashAttribute("error", "Không tìm thấy bạn đọc có mã '" + studentCode + "'.");
            return "redirect:/librarian/inhouse";
        }
        ValidationResult r = loanService.lendInHouse(reader, bookId);
        if (r.isValid()) {
            ra.addFlashAttribute("success", "Đã lập phiếu đọc tại chỗ cho " + reader.getFullName() + ".");
        } else {
            ra.addFlashAttribute("errors", r.getErrors());
        }
        return "redirect:/librarian/inhouse";
    }
}
