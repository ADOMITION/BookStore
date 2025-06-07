package com.books.service.Impl;

import com.books.entity.CartItem;
import com.books.mapper.CartItemMapper;
import com.books.service.CartItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

}
