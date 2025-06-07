package com.books.mapper;

import com.books.entity.Guestbook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuestbookMapper extends BaseMapper<Guestbook> {
    Long countByUserId(Integer userId);

}
