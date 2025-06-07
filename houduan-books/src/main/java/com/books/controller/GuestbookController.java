package com.books.controller;


import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Guestbook;
import com.books.service.GuestbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;



@RestController
@RequestMapping("/guestbook")
public class GuestbookController {

    @Resource
    private GuestbookService guestbookService;

    @Resource
    private HttpSession session;

    // 用户提交留言
    @PostMapping("/submit")
    public Result submit(@RequestBody Guestbook guestbook) {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) return Result.fail("未登录");
        guestbook.setUserId(userId);
        guestbook.setCreateTime(LocalDateTime.now());
        return guestbookService.save(guestbook) ? Result.suc() : Result.fail();
    }

    // 用户查看自己的留言
    @PostMapping("/user/list")
    public Result userList(@RequestBody QueryPageParam query) {
        return guestbookService.listUserMessages(query);
    }

    //用户删除自己的留言
    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        return guestbookService.removeById(id) ? Result.suc() : Result.fail();
    }

    // 管理员回复
    @PostMapping("/admin/reply")
    public Result reply(@RequestBody Guestbook guestbook) {
        Integer adminId = (Integer) session.getAttribute("userId");
        if(adminId == null) return Result.fail("未登录");

        Guestbook update = new Guestbook();
        update.setId(guestbook.getId());
        update.setAdminId(adminId);
        update.setAdminReply(guestbook.getAdminReply());
        update.setUpdateTime(LocalDateTime.now());

        return guestbookService.updateById(update) ? Result.suc() : Result.fail();
    }

    // 管理员查看所有留言
    @PostMapping("/admin/list")
    public Result adminList(@RequestBody QueryPageParam query) {
        return guestbookService.listAllMessages(query);
    }

}
