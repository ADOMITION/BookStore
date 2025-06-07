package com.books.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Category;
import com.books.service.BookService;
import com.books.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private BookService bookService;

    @Resource
    private CategoryService categoryService;

    @GetMapping("/{id}")
    public Result getCategoryInfo(@PathVariable Integer id) {
        Category category = categoryService.getById(id);
        if(category == null){
            return Result.fail("分类不存在");
        }
        return Result.suc(category);
    }

    //分类管理
    @PostMapping("/{id}/books")
    public Result getBooksByCategory(@PathVariable Integer id, @RequestBody QueryPageParam query) {
        // 将分类ID加入查询参数
        query.getParam().put("cid", id);
        return bookService.queryBooksByCondition(query);
    }

    //list
    @GetMapping("/list")
    public Result getAllCategories() {
        List<Category> list = categoryService.list();
        return Result.suc(list);
    }

    //admin
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return categoryService.removeById(id)?Result.suc():Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Category category){
        return categoryService.save(category)?Result.suc():Result.fail();
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Category category){
        return categoryService.updateById(category)?Result.suc():Result.fail();
    }

    //查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {

        HashMap param = query.getParam();
        String name = (String) param.get("name");

        Page<Category> page = new Page<>(query.getPageNum(),query.getPageSize());//当前页，每页多少条数据

        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(Category::getName,name);
        }

        IPage result = categoryService.pageCC(page,lambdaQueryWrapper);
        return  Result.suc(result.getRecords(),result.getTotal());
    }

}
