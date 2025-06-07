package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Guestbook;
import com.books.mapper.GuestbookMapper;
import com.books.service.GuestbookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Service
public class GuestbookServiceImpl extends ServiceImpl<GuestbookMapper, Guestbook> implements GuestbookService {
    @Resource
    private GuestbookMapper guestbookMapper;

    @Resource
    private HttpSession session;

    @Override
    public Result listUserMessages(QueryPageParam query) {
        Integer userId = (Integer) session.getAttribute("userId");
        Page<Guestbook> page = new Page<>(query.getPageNum(), query.getPageSize());
        QueryWrapper<Guestbook> wrapper = new QueryWrapper<Guestbook>()
                .eq("user_id", userId)
                .orderByDesc("create_time");

        IPage<Guestbook> result = guestbookMapper.selectPage(page, wrapper);
        return Result.suc(result.getRecords(), result.getTotal());
    }

    @Override
    public Result listAllMessages(QueryPageParam query) {
        Page<Guestbook> page = new Page<>(query.getPageNum(), query.getPageSize());
        QueryWrapper<Guestbook> wrapper = new QueryWrapper<Guestbook>()
                .orderByDesc("create_time");

        IPage<Guestbook> result = guestbookMapper.selectPage(page, wrapper);
        return Result.suc(result.getRecords(), result.getTotal());
    }
}
