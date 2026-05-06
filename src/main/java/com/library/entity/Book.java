package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Column(name = "category", length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 20)
    private DocType docType;

    @Column(name = "total_copies")
    private Integer totalCopies = 1;

    @Column(name = "available_copies")
    private Integer availableCopies = 1;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "classification_code", length = 50)
    private String classificationCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "publisher", length = 150)
    private String publisher;

    @Column(name = "publish_year")
    private Integer publishYear;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<LoanDetail> loanDetails;

    // Helper method
    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }

    public String getDocTypeDisplay() {
        if (docType == null) return "";
        return switch (docType) {
            case TEXTBOOK -> "Giáo trình";
            case SPECIALIZED_REF -> "Tham khảo chuyên ngành";
            case GENERAL_REF -> "Tham khảo tra cứu";
            case RESTRICTED -> "Tài liệu nội sinh";
        };
    }
}
