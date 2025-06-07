package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.*;
import com.books.mapper.BookMapper;
import com.books.mapper.OrderItemMapper;
import com.books.mapper.OrderMapper;
import com.books.mapper.UserMapper;
import com.books.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<OrderVO> getOrderList(Integer userId) {
        List<Order> orders = baseMapper.selectList(
                new QueryWrapper<Order>()
                        .eq("user_id", userId)
                        .orderByDesc("createtime")
        );
        return orders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setItems(orderItemMapper.selectByOrderId(order.getId()).stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                BeanUtils.copyProperties(item, itemVO);
                itemVO.setBook(bookMapper.selectById(item.getBookId()));
                return itemVO;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public OrderVO getOrderDetail(Integer oid) {
        Order order = baseMapper.selectById(oid);
        if(order == null) return null;

        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setItems(orderItemMapper.selectByOrderId(oid).stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            itemVO.setBook(bookMapper.selectById(item.getBookId()));
            return itemVO;
        }).collect(Collectors.toList()));
        return vo;
    }

    //增加回库存
    @Override
    public void restoreStock(Integer orderId) {
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Book book = bookMapper.selectById(item.getBookId());
            if (book != null) {
                book.setStock(book.getStock() + item.getQuantity());
                bookMapper.updateById(book);
            }
        }
    }

    @Override
    public Result payOrder(Integer orderId) {
        // 1. 更新订单状态为已支付
        Order order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if ("已付款".equals(order.getStatus())) {
            return Result.fail("订单已支付");
        }
        order.setStatus("已付款");
        order.setPayTime(LocalDateTime.now());
        baseMapper.updateById(order);

        // 2. 扣减库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            int result = bookMapper.deductStock(item.getBookId(), item.getQuantity());
            if (result == 0) {
                // 库存不足，抛出异常回滚事务
                throw new RuntimeException("商品ID：" + item.getBookId() + " 库存不足");
            }
        }
        // 返回支付时间
        Map<String, Object> result = new HashMap<>();
        result.put("payTime", order.getPayTime());
        return Result.suc("支付成功");
    }

    //admin
    @Override
    public Result pageQuery(QueryPageParam queryPageParam) {
        // 获取分页参数
        int pageNum = queryPageParam.getPageNum();
        int pageSize = queryPageParam.getPageSize();
        Map<String, Object> param = queryPageParam.getParam();
        // 构建分页对象
        Page<Order> page = new Page<>(pageNum, pageSize);
        // 构建查询条件
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (param != null) {
            if (param.containsKey("id")) {
                wrapper.eq("id", param.get("id"));
            }
            if (param.containsKey("userId")) {
                wrapper.eq("user_id", param.get("userId"));
            }
            if (param.containsKey("status")) {
                wrapper.eq("status", param.get("status"));
            }
        }
        wrapper.orderByDesc("createtime");
        // 执行分页查询
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        // 转换为VO列表
        List<OrderVO> orderVOS = orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setUserName(userMapper.getUserName(order.getUserId()));
            return vo;
        }).collect(Collectors.toList());

        return Result.suc(orderVOS, orderPage.getTotal());
    }


    @Transactional
    @Override
    public Result cancelOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) return Result.fail("订单不存在");
        if (!"待付款".equals(order.getStatus())) {
            return Result.fail("当前状态不可取消");
        }
        order.setStatus("已取消");
        orderMapper.updateById(order);
        return Result.suc();
    }

    @Override
    public boolean updateStatus(Integer id, String status) {
       Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        return updateById(order);
    }

}
