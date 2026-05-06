package com.library.dto;

import com.library.entity.BookCondition;
import lombok.Data;

@Data
public class ReturnBookForm {
    private Long loanDetailId;
    private BookCondition condition = BookCondition.GOOD;
}
