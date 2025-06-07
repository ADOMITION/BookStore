package com.books.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("书籍详情返回数据")
public class BookDetailVO {
    @ApiModelProperty("书籍信息")
    private Book product;
    
    @ApiModelProperty("评论列表")
    private List<Review> reviews;

    public BookDetailVO setReviews(List<Review> reviews) {
        this.reviews = reviews;
        return this; // 返回 this
    }

    public BookDetailVO setProduct(Book product) {
        this.product = product;
        return this; // 返回 this
    }
}