package com.books.service.Impl;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.books.common.Result;
import com.books.entity.*;
import com.books.mapper.CartItemMapper;
import com.books.mapper.CartMapper;
import com.books.service.BookService;
import com.books.service.CartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Resource
    private CartMapper cartMapper;
    @Resource private CartItemMapper cartItemMapper;
    @Resource private BookService bookService;

    // 临时购物车存储（未登录用户）
    private static final String TEMP_CART_KEY = "temp_cart";

    @Override
    @Transactional
    public Result addItem(Integer userId, Integer bookId, Integer quantity, HttpSession session) {
        // 校验书籍状态和库存
        Book book = bookService.getById(bookId);
        if (book == null || book.getStatus() == 0) {
            return Result.fail("商品已下架");
        }
        if (book.getStock() < quantity) {
            return Result.fail("库存不足，当前库存：" + book.getStock());
        }
        return userId != null ?
                addToDB(userId, bookId, quantity) :
                addToSession(bookId, quantity, session);
    }


    @Override
    @Transactional
    public Result updateQuantity(Integer userId, Integer bookId, Integer quantity, HttpSession session) {
        // 校验最小数量
        if (quantity < 0) {
            return Result.fail("数量不能小于0");
        }
        return userId != null ?
                updateInDB(userId, bookId, quantity) :
                updateInSession(bookId, quantity, session);
    }


    @Override
    @Transactional
    public Result removeItem(Integer userId, Integer bookId, HttpSession session) {
        if (userId != null) {
            return removeFromDB(userId, bookId);
        } else {
            return removeFromSession(bookId, session);
        }
    }

    @Override
    @Transactional
    public Result clearCart(Integer userId, HttpSession session) {
        if (userId != null) {
            return clearDBCart(userId);
        } else {
            return clearSessionCart(session);
        }
    }


    //=== 数据库操作 ===//
    private Result addToDB(Integer userId, Integer bookId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemMapper.selectOne(new QueryWrapper<CartItem>()
                .eq("cart_id", cart.getId())
                .eq("book_id", bookId));

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            cartItemMapper.updateById(item);
        } else {
            item = new CartItem();
            item.setCartId(cart.getId());
            item.setBookId(bookId);
            item.setQuantity(quantity);
            cartItemMapper.insert(item);
        }
        return Result.suc();
    }

    private Result updateInDB(Integer userId, Integer bookId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemMapper.selectOne(new QueryWrapper<CartItem>()
                .eq("cart_id", cart.getId())
                .eq("book_id", bookId));

        if (item == null) {
            return Result.fail("商品不存在");
        }
        // 校验库存
        Book book = bookService.getById(bookId);
        if (book.getStock() < quantity) {
            return Result.fail("库存不足，最大可购买数量：" + book.getStock());
        }
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
        return Result.suc();
    }

    private Result removeFromDB(Integer userId, Integer bookId) {
        Cart cart = getOrCreateCart(userId);
        int deleted = cartItemMapper.delete(new QueryWrapper<CartItem>()
                .eq("cart_id", cart.getId())
                .eq("book_id", bookId));
        return deleted > 0 ? Result.suc() : Result.fail("删除失败");
    }

    private Result clearDBCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemMapper.delete(new QueryWrapper<CartItem>().eq("cart_id", cart.getId()));
        return Result.suc();
    }


    //=== Session操作 ===//
    private Result addToSession(Integer bookId, Integer quantity, HttpSession session) {
        Map<Integer, Integer> tempCart = getTempCart(session);
        tempCart.put(bookId, tempCart.getOrDefault(bookId, 0) + quantity);
        session.setAttribute(TEMP_CART_KEY, tempCart);
        return Result.suc();
    }

    private Result updateInSession(Integer bookId, Integer quantity, HttpSession session) {
        Map<Integer, Integer> tempCart = getTempCart(session);
        if (!tempCart.containsKey(bookId)) {
            return Result.fail("商品不存在");
        }
        tempCart.put(bookId, quantity);
        return Result.suc();
    }

    private Result removeFromSession(Integer bookId, HttpSession session) {
        Map<Integer, Integer> tempCart = getTempCart(session);
        tempCart.remove(bookId);
        return Result.suc();
    }

    private Result clearSessionCart(HttpSession session) {
        session.removeAttribute(TEMP_CART_KEY);
        return Result.suc();
    }


    //=== 查询 ===//
    @Override
    public List<CartItem> getCartItems(Integer userId, HttpSession session) {
        if (userId != null) {
            return getFromDB(userId);
        } else {
            return getFromSession(session);
        }
    }
    private List<CartItem> getFromDB(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemMapper.selectList(
                new QueryWrapper<CartItem>().eq("cart_id", cart.getId()));

        // 填充书籍信息
        return items.stream().map(item -> {
            item.setBook(bookService.getById(item.getBookId()));
            return item;
        }).collect(Collectors.toList());
    }
    private List<CartItem> getFromSession(HttpSession session) {
        Map<Integer, Integer> tempCart = getTempCart(session);
        return convertTempCartItems(tempCart);
    }


    private Cart getOrCreateCart(Integer userId) {
        Cart cart = cartMapper.selectOne(new QueryWrapper<Cart>().eq("user_id", userId));
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setUpdatetime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
        return cart;
    }


    //处理未登录
    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getTempCart(HttpSession session) {
        Map<Integer, Integer> tempCart = (Map<Integer, Integer>) session.getAttribute(TEMP_CART_KEY);
        if (tempCart == null) {
            tempCart = new ConcurrentHashMap<>();
            session.setAttribute(TEMP_CART_KEY, tempCart);
        }
        return tempCart;
    }
    private List<CartItem> convertTempCartItems(Map<Integer, Integer> tempCart) {
        return tempCart.entrySet().stream().map(entry -> {
            CartItem item = new CartItem();
            item.setBookId(entry.getKey());
            item.setQuantity(entry.getValue());
            item.setBook(bookService.getById(entry.getKey()));
            return item;
        }).collect(Collectors.toList());
    }

    //=== 合并购物车 ===//
    @Override
    @Transactional
    public Result mergeTempCart(Integer userId, HttpSession session) {
        Map<Integer, Integer> tempCart = getTempCart(session);
        if (tempCart.isEmpty()) return Result.suc();

        Cart cart = getOrCreateCart(userId);
        tempCart.forEach((bookId, quantity) -> {
            CartItem existingItem = cartItemMapper.selectOne(new QueryWrapper<CartItem>()
                    .eq("cart_id", cart.getId())
                    .eq("book_id", bookId));

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemMapper.updateById(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCartId(cart.getId());
                newItem.setBookId(bookId);
                newItem.setQuantity(quantity);
                cartItemMapper.insert(newItem);
            }
        });
        session.removeAttribute(TEMP_CART_KEY);
        return Result.suc();
    }
}
