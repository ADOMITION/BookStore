package com.books.mapper;

import com.books.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;


@Mapper
public interface OrderMapper extends BaseMapper<Order> {


    List<BigDecimal> selectCompletedAmounts(Integer userId);
}
