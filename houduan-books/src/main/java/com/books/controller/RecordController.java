package com.books.controller;


import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Record;
import com.books.service.RecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/record")
public class RecordController {

    @Resource
    private RecordService recordService;

    @PostMapping("/save")
    public Result save(@RequestBody Record record, HttpSession session) {
        return recordService.saveRecord(record,session);
    }

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        return recordService.listPage(query);
    }

}
