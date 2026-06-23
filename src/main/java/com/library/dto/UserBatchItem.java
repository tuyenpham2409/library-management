package com.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBatchItem {
    private String studentCode;
    private String fullName;
    private String role;
    private String password;
}
