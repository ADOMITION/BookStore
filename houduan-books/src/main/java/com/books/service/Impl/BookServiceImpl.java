package com.books.service.Impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.*;
import com.books.entity.Record;
import com.books.mapper.BookMapper;
import com.books.mapper.ReviewMapper;
import com.books.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;



@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Resource
    private final BookMapper bookMapper;

    @Resource
    private final ReviewMapper reviewMapper;

    @Override
    public IPage<BookVO> searchBook(Page<BookVO> page, SearchParam param) {
        return bookMapper.searchBook(page, param);
    }

    //书籍详情页
    @Override
    public BookDetailVO getBookDetail(Integer bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }
        // 设置评论数量
        book.setReviewCount(bookMapper.selectReviewCount(bookId));

        List<Review> reviews = reviewMapper.selectReviewsWithUser(bookId);

        return new BookDetailVO().setReviews(reviews).setProduct(book);
    }

    //分类管理
    @Override
    public Result queryBooksByCondition(QueryPageParam queryPageParam) {
        // 使用MyBatis Plus分页构造器
        Page<BookVO> page = new Page<>(
                queryPageParam.getPageNum(),
                queryPageParam.getPageSize()
        );
        // 执行自定义分页查询
        IPage<BookVO> result = bookMapper.queryBooksByCondition(page, queryPageParam.getParam());

        return Result.suc(result.getRecords(), result.getTotal());
    }

    //查找热门书籍
    @Override
    public List<Book> findHotBooks() {
        return bookMapper.selectHotBooks();
    }


    @Override
    public IPage pageCC(Page<Book> page, Wrapper wrapper) {
        return bookMapper.pageCC(page, wrapper);
    }

    @Override
    public void updateCover(Integer bookId, String coverUrl) {
        Book book = new Book();
        book.setId(bookId);
        book.setUrl(coverUrl);
        baseMapper.updateById(book);
    }



}
