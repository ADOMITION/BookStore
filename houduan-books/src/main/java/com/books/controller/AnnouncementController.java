package com.books.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Announcement;
import com.books.entity.User;
import com.books.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;


@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String title = (String) param.get("title");
        Page<Announcement> page = new Page<Announcement>(query.getPageNum(),query.getPageSize());
        LambdaQueryWrapper<Announcement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(title) && !"null".equals(title)){
            lambdaQueryWrapper.like(Announcement::getTitle,title);
        }
        IPage result = announcementService.pageCC(page,lambdaQueryWrapper);
        return  Result.suc(result.getRecords(),result.getTotal());
    }

    @PostMapping("/add")
    public Result add(@RequestBody Announcement announcement) {
        return announcementService.save(announcement) ? Result.suc() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Announcement announcement) {
        return announcementService.updateById(announcement) ? Result.suc() : Result.fail();
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        return announcementService.removeById(id) ? Result.suc() : Result.fail();
    }
}
