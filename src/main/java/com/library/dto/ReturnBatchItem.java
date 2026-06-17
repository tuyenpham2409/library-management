package com.library.dto;

import com.library.entity.BookCondition;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBatchItem {
    private Long detailId;
    private BookCondition condition;
}
