package com.books.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface ReviewService extends IService<Review> {

    List<Review> findHotReviews();


    boolean updateStatus(Integer id, Integer status);

    IPage<Review> findReviewPage(Page<Review> page, String bookName, String userName);

    //我的评论
    IPage<Review> pageUserReviews(Page<Review> page, Integer userId, Integer status);



}
