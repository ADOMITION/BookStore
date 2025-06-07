package com.books.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Review;
import com.books.entity.User;
import com.books.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    // 获取热门书评
    @GetMapping("/hot")
    public Result getHotReviews() {
        List<Review> reviews = reviewService.findHotReviews();
        return Result.suc(reviews);
    }

    // 提交书评
    @PostMapping("/submit")
    public Result submitReview(@RequestBody Review review, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Result.fail("请先登录");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("会话已过期，请重新登录");
        }

        Review newReview = new Review();
        newReview.setBookId(review.getBookId());
        newReview.setContent(review.getContent());
        newReview.setUserId(user.getId());
        newReview.setCreatetime(LocalDateTime.now());
        newReview.setMark(review.getMark());
        return reviewService.save(newReview) ? Result.suc() : Result.fail();
    }

    //点赞和回复



    //我的评论
    @PostMapping("/listByUser")
    public Result listByUser(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        Integer userId = (Integer)param.get("userId");
        Integer status = (Integer)param.get("status");
        Page<Review> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<Review> result = reviewService.pageUserReviews(page, userId, status);
        return Result.suc(result.getRecords(), result.getTotal());
    }

    //admin
    @PostMapping("/list")
    public Result listReviews(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String bookName = (String) param.get("bookName");
        String userName = (String) param.get("userName");

        Page<Review> page = new Page<>(
                query.getPageNum(),
                query.getPageSize()
        );

        IPage<Review> result = reviewService.findReviewPage(page, bookName, userName);
        return Result.suc(result.getRecords(), result.getTotal());
    }

    // 更新审核状态
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Review review) {
        return reviewService.updateStatus(review.getId(), review.getStatus())
                ? Result.suc()
                : Result.fail();
    }
    // 删除评论
    @PostMapping("/delete")
    public Result delete(@RequestBody Review review) {
        return reviewService.removeById(review.getId())
                ? Result.suc()
                : Result.fail();
    }

}
