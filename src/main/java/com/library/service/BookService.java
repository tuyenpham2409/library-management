package com.library.service;

import com.library.dto.BookForm;
import com.library.entity.Book;
import com.library.entity.DocType;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> search(String keyword, DocType docType, boolean availableOnly) {
        return bookRepository.searchBooks(keyword, docType, availableOnly);
    }

    /**
     * Tìm sách nhưng chỉ trả về các loại tài liệu mà vai trò được phép xem.
     * Dùng cho phía client để tôn trọng cấu hình ẩn/hiện của admin.
     */
    public List<Book> search(String keyword, DocType docType, boolean availableOnly, Set<DocType> allowed) {
        return bookRepository.searchBooks(keyword, docType, availableOnly).stream()
                .filter(b -> allowed.contains(b.getDocType()))
                .toList();
    }

    public Book save(BookForm form) {
        Book book = new Book();
        mapFormToBook(form, book);
        return bookRepository.save(book);
    }

    public Book update(Long id, BookForm form) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        mapFormToBook(form, book);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public void decreaseAvailable(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));
        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }
    }

    @Transactional
    public void increaseAvailable(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }
    }

    private void mapFormToBook(BookForm form, Book book) {
        book.setTitle(form.getTitle());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        book.setCategory(form.getCategory());
        book.setDocType(form.getDocType());
        book.setTotalCopies(form.getTotalCopies() != null ? form.getTotalCopies() : 1);
        book.setAvailableCopies(form.getAvailableCopies() != null ? form.getAvailableCopies() : 1);
        book.setImageUrl(form.getImageUrl());
        book.setClassificationCode(form.getClassificationCode());
        book.setDescription(form.getDescription());
        book.setPublisher(form.getPublisher());
        book.setPublishYear(form.getPublishYear());
    }

    public BookForm toForm(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());
        form.setCategory(book.getCategory());
        form.setDocType(book.getDocType());
        form.setTotalCopies(book.getTotalCopies());
        form.setAvailableCopies(book.getAvailableCopies());
        form.setImageUrl(book.getImageUrl());
        form.setClassificationCode(book.getClassificationCode());
        form.setDescription(book.getDescription());
        form.setPublisher(book.getPublisher());
        form.setPublishYear(book.getPublishYear());
        return form;
    }
}
