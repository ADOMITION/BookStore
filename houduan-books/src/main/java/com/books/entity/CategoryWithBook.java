package com.books.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分类及其书籍列表")
public class CategoryWithBook {
    @ApiModelProperty("分类ID")
    private Integer id;
    
    @ApiModelProperty("分类名称")
    private String name;
    
    @ApiModelProperty("书籍列表")
    private List<BookVO> books;
}