package com.books.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


@Service
public interface AnnouncementService extends IService<Announcement> {

    IPage pageCC(Page<Announcement> page, Wrapper wrapper);
}
