package com.books.service;

import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.books.entity.OrderVO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface OrderService extends IService<Order> {

    //支付
    Result payOrder(Integer orderId);

    List<OrderVO> getOrderList(Integer userId);

    OrderVO getOrderDetail(Integer oid);

    //恢复库存
    void restoreStock(Integer orderId);

    Result pageQuery(QueryPageParam queryPageParam);
    Result cancelOrder(Integer orderId);
    boolean updateStatus(Integer id, String status);
}
