package com.books.mapper;

import com.books.entity.CartItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

}
