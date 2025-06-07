package com.books.controller;


import com.books.common.Result;

import com.books.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    // 添加商品到购物车
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Integer bookId = params.get("bookId");
        Integer quantity = params.get("quantity");
        return cartService.addItem(getUserId(request), bookId, quantity, request.getSession());
    }

    // 获取购物车列表
    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        return Result.suc(cartService.getCartItems(getUserId(request), request.getSession()));
    }

    // 修改商品数量
    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Integer bookId = params.get("bookId");
        Integer quantity = params.get("quantity");
        return cartService.updateQuantity(getUserId(request), bookId, quantity, request.getSession());
    }

    // 删除商品
    @PostMapping("/delete")
    public Result delete(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Integer bookId = params.get("bookId");
        return cartService.removeItem(getUserId(request), bookId, request.getSession());
    }

    // 清空购物车
    @PostMapping("/clear")
    public Result clear(HttpServletRequest request) {
        return cartService.clearCart(getUserId(request), request.getSession());
    }

    // 合并临时购物车（登录时调用）
    @PostMapping("/merge")
    public Result mergeCart(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return cartService.mergeTempCart(userId, session);
    }
    private Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (Integer) session.getAttribute("userId") : null;
    }

}
