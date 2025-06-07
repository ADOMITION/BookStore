package com.books.entity;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmVO {
    private Book book;
    private OrderItem orderItem;
    private BigDecimal originalPrice; // 书籍原价
    private BigDecimal discountPrice; // 折扣价
}