package com.books.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Integer id;
    private Integer userId;
    private String userName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createtime;
    private List<OrderItemVO> items;
    private String statusDesc;
    private String address;
    private String recipient;
    private String tel;
    private LocalDateTime payTime;

}