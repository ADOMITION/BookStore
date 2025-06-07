package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.books.entity.Book;
import com.books.entity.OrderItem;
import com.books.entity.OrderItemVO;
import com.books.mapper.OrderItemMapper;
import com.books.service.BookService;
import com.books.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private BookService bookService;


    @Override
    public List<OrderItemVO> getByOrderId(Integer id) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", id)
                .select("book_id", "quantity", "price");

        return orderItemMapper.selectList(wrapper).stream().map(item -> {
            OrderItemVO vo = new OrderItemVO();
            BeanUtils.copyProperties(item, vo);

            Book book = bookService.getById(item.getBookId());
            vo.setBook(book);

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteByOrderId(Integer oid) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", oid);
        this.remove(wrapper);
    }
}
