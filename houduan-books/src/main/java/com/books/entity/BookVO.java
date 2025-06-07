package com.books.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("书籍预览信息")
public class BookVO {
    @ApiModelProperty("书籍ID")
    private Integer id;
    
    @ApiModelProperty("书籍名称")
    private String name;
    
    @ApiModelProperty("作者")
    private String author;
    
    @ApiModelProperty("价格")
    private BigDecimal price;
    
    @ApiModelProperty("封面地址")
    private String image;
    
    @ApiModelProperty("评分")
    private Float mark;

    @ApiModelProperty("出版社")
    private String press;
}