package com.books.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordVO extends Record {
    private String bookName;
    private String userName;
}