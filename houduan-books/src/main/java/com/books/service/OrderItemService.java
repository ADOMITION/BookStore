package com.books.service;

import com.books.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.books.entity.OrderItemVO;

import java.util.List;


public interface OrderItemService extends IService<OrderItem> {

    List<OrderItemVO> getByOrderId(Integer id);

    void deleteByOrderId(Integer oid);
}
