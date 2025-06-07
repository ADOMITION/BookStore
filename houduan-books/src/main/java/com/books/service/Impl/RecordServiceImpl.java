package com.books.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Book;
import com.books.entity.Record;
import com.books.entity.RecordVO;
import com.books.entity.User;
import com.books.mapper.BookMapper;
import com.books.mapper.RecordMapper;
import com.books.service.RecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Resource
    private RecordMapper recordMapper;

    @Resource
    private BookMapper bookMapper;


    @Override
    @Transactional
    public Result saveRecord(Record record,HttpSession session) {
        // 获取当前用户ID
        User user = (User) session.getAttribute("user");
        Integer currentUserId = user.getId();
        record.setUserId(currentUserId);
        record.setCreateTime(LocalDateTime.now());
        // 更新库存
        Book book = bookMapper.selectById(record.getBookId());
        if(book == null) return Result.fail("书籍不存在");
        if(record.getType()) { // 入库
            book.setStock(book.getStock() + record.getQuantity());
        } else { // 出库
            if(book.getStock() < record.getQuantity()) {
                return Result.fail("库存不足");
            }
            book.setStock(book.getStock() - record.getQuantity());
        }
        bookMapper.updateById(book);
        recordMapper.insert(record);
        return Result.suc();
    }

    @Override
    public Result listPage(QueryPageParam query) {
        Page<RecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());

        QueryWrapper<Record> wrapper = new QueryWrapper<>();

        if(query.getParam() != null) {
            String bookName = (String) query.getParam().get("bookName");
            Integer type = (Integer) query.getParam().get("type");
            String startDate = (String) query.getParam().get("startDate");
            String endDate = (String) query.getParam().get("endDate");
            if(StringUtils.hasText(bookName)) {
                wrapper.like("b.name", bookName);
            }
            if(type != null) {
                wrapper.eq("r.type", type);
            }
            if(StringUtils.hasText(startDate)) {
                wrapper.ge("r.create_time", startDate + " 00:00:00");
            }
            if(StringUtils.hasText(endDate)) {
                wrapper.le("r.create_time", endDate + " 23:59:59");
            }
        }
        // 添加关联查询
        wrapper
                .apply("r.book_id = b.id")
                .apply("r.user_id = u.id");

        IPage<RecordVO> result = recordMapper.selectRecordPage(page, wrapper);
        return Result.suc(result.getRecords(), result.getTotal());
    }
}
