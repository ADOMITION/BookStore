package com.books.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Review;
import com.books.mapper.ReviewMapper;
import com.books.service.ReviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public List<Review> findHotReviews() {
        // 获取最近一周的热门评论（按点赞数+回复数排序）
        return reviewMapper.selectHotReviews();
    }



    //admin
    @Override
    public IPage<Review> findReviewPage(Page<Review> page, String bookName, String userName) {
        return baseMapper.findReviewPage(page, bookName, userName);
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        Review review = new Review();
        review.setId(id);
        review.setStatus(status);
        return updateById(review);
    }

    @Override
    public IPage<Review> pageUserReviews(Page<Review> page, Integer userId, Integer status) {
        return page.setRecords(this.baseMapper.findUserReviews(userId, status));
    }


}
