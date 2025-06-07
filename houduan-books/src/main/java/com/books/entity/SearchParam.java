package com.books.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("搜索参数")
public class SearchParam {
    @ApiModelProperty("关键字")
    private String keyword;

}