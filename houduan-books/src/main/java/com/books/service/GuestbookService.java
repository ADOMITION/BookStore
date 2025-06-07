package com.books.service;

import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Guestbook;
import com.baomidou.mybatisplus.extension.service.IService;


public interface GuestbookService extends IService<Guestbook> {
    Result listUserMessages(QueryPageParam query);
    Result listAllMessages(QueryPageParam query);
}
