package com.books.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.books.entity.CategoryWithBook;

import java.util.List;


public interface CategoryService extends IService<Category> {

    List<CategoryWithBook> getCategoryPreview();

    IPage pageCC(Page<Category> page, Wrapper wrapper);

}
