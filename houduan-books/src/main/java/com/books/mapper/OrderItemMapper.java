package com.books.mapper;

import com.books.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.books.entity.OrderItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    //更具orderid查询
    List<OrderItem> selectByOrderId(Integer orderId);

}
