package com.library.dto;

import com.library.entity.DocType;
import lombok.Data;

@Data
public class BookForm {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private DocType docType;
    private Integer totalCopies;
    private Integer availableCopies;
    private String imageUrl;
    private String classificationCode;
    private String description;
    private String publisher;
    private Integer publishYear;
}
