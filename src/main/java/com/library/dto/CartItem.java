package com.library.dto;

import com.library.entity.DocType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long bookId;
    private String title;
    private String author;
    private DocType docType;
    private String classificationCode;
}
