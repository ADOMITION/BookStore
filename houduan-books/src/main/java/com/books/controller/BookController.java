package com.books.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.*;
import com.books.service.BookService;
import com.books.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    @Resource
    private  BookService bookService;

    @Resource
    private final CategoryService categoryService;

    //精确展示
    @GetMapping("/{id}")
    public Result getBookDetail( @PathVariable Integer id) {
        BookDetailVO detail = bookService.getBookDetail(id);
        return Result.suc(detail);
    }

    //预览
    @GetMapping("/preview")
    @SuppressWarnings("unchecked") // 这个注解忽略警告
    public Result preview() {
        List<CategoryWithBook> data = categoryService.getCategoryPreview();
        return Result.suc(data);
    }

    //图书搜索
    @PostMapping("/search")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public Result searchBook(@RequestBody QueryPageParam query) {
        // 提取正确的参数
        Map<String, Object> paramMap = query.getParam();

        // 创建搜索参数对象
        SearchParam searchParam = new SearchParam();
        searchParam.setKeyword((String) paramMap.get("keyword"));
        // 分页参数
        Page<BookVO> page = new Page<>(query.getPageNum(), query.getPageSize());

        IPage<BookVO> result = bookService.searchBook(page, searchParam);
        return Result.suc(result.getRecords(), result.getTotal());
    }

    // 获取热门推荐书籍
    @GetMapping("/hot")
    public Result getHotBooks() {
        List<Book> books = bookService.findHotBooks();
        return Result.suc(books);
    }


    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return bookService.removeById(id)?Result.suc():Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Book book){
        return bookService.save(book)?Result.suc():Result.fail();
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Book book){
        return bookService.updateById(book)?Result.suc():Result.fail();
    }

    //查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {

        HashMap param = query.getParam();
        String name = (String) param.get("name");
        String author = (String) param.get("author");
        String press = (String) param.get("press");

        Page<Book> page = new Page<>(query.getPageNum(),query.getPageSize());//当前页，每页多少条数据

        LambdaQueryWrapper<Book> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(Book::getName,name);
        }
        if(StringUtils.isNotBlank(author) && !"null".equals(author)){
            lambdaQueryWrapper.like(Book::getAuthor,author);
        }
        if(StringUtils.isNotBlank(press) && !"null".equals(press)){
            lambdaQueryWrapper.like(Book::getPress,press);
        }



        IPage result = bookService.pageCC(page,lambdaQueryWrapper);
        System.out.println("total=="+result.getTotal());
        return  Result.suc(result.getRecords(),result.getTotal());
    }
}
