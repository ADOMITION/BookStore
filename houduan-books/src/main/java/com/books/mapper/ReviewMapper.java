package com.books.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    List<Review> selectReviewsWithUser(@Param("bookId") Integer bookId);

    List<Review> selectHotReviews();

    IPage<Review> findReviewPage(Page<Review> page, String bookName, String userName);

    List<Review> findUserReviews(@Param("userId") Integer userId, @Param("status") Integer status);


}
