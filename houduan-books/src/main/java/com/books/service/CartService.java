package com.books.service;


import com.books.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import com.books.common.Result;
import com.books.entity.CartItem;

import javax.servlet.http.HttpSession;
import java.util.List;


@Service
public interface CartService extends IService<Cart> {

    Result addItem(Integer userId, Integer bookId, Integer quantity, HttpSession session);

    Result updateQuantity(Integer userId, Integer bookId, Integer quantity, HttpSession session);

    Result removeItem(Integer userId, Integer bookId, HttpSession session);

    Result clearCart(Integer userId, HttpSession session);

    List<CartItem> getCartItems(Integer userId, HttpSession session);

    Result mergeTempCart(Integer userId, HttpSession session);

}
