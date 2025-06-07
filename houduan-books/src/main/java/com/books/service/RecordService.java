package com.books.service;


import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Record;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;


@Service
public interface RecordService extends IService<Record> {
    Result saveRecord(Record record, HttpSession session);
    Result listPage(QueryPageParam query);
}
