package com.books.entity;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    private List<Integer> oiids;
    private String address;
    private String recipient;
    private String tel;
}