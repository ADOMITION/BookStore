package com.books.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Integer quantity;
    private BigDecimal price;
    private Book book;
}