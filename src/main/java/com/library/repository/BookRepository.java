package com.library.repository;

import com.library.entity.Book;
import com.library.entity.DocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(b.classificationCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:docType IS NULL OR b.docType = :docType) AND " +
           "(:availableOnly = false OR b.availableCopies > 0)")
    List<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("docType") DocType docType,
                           @Param("availableOnly") boolean availableOnly);

    List<Book> findByDocType(DocType docType);
}
