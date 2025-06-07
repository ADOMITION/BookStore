package com.books.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.books.entity.BookVO;
import com.books.entity.SearchParam;
import com.books.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface BookMapper extends BaseMapper<Book> {

    //查找预览书籍
    List<BookVO> findBook(Integer cid);

    //搜索书籍
    IPage<BookVO> searchBook(Page<BookVO> page, @Param("param") SearchParam param);

    //查找书评数量
    Integer selectReviewCount(@Param("bookId") Integer bookId);

    //分类管理，需要获取页码
    IPage<BookVO> queryBooksByCondition(
            @Param("page") Page<BookVO> page,
            @Param("param") Map<String, Object> param
    );

    //查找热门书籍
    List<Book> selectHotBooks();

    //扣减库存
    int deductStock(@Param("bookId") Integer bookId, @Param("quantity") Integer quantity);

    IPage pageCC(IPage<Book> page, @Param(Constants.WRAPPER) Wrapper wrapper);

}
