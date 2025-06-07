package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Category;
import com.books.entity.CategoryWithBook;
import com.books.mapper.BookMapper;
import com.books.mapper.CategoryMapper;
import com.books.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private  final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    //预览书籍
    @Override
    public List<CategoryWithBook> getCategoryPreview() {
        return categoryMapper.selectList(null).stream()
                .map(category -> {
                    CategoryWithBook dto = new CategoryWithBook();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setBooks(bookMapper.findBook(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public IPage pageCC(Page<Category> page, Wrapper wrapper) {
        return categoryMapper.pageCC(page, wrapper);
    }
}
