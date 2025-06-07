package com.books.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface BookService extends IService<Book> {

    IPage<BookVO> searchBook(Page<BookVO> page, SearchParam param);

    BookDetailVO getBookDetail(Integer bookId);

    Result queryBooksByCondition(QueryPageParam queryPageParam);

    List<Book> findHotBooks();

    IPage pageCC(Page<Book> page, Wrapper wrapper);

    //上传书籍
    void updateCover(Integer bookId, String coverUrl);

    //出库入库

}
