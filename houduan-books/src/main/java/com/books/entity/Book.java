package com.books.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Book对象", description="书籍表")
public class Book implements Serializable {


    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "书名")
    private String name;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "出版社")
    private String press;

    @ApiModelProperty(value = "出版日期")
    private String date;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "书籍描述")
    private String info;

    @ApiModelProperty(value = "封面链接")
    private String url;

    @ApiModelProperty(value = "评分")
    private Float mark;

    @ApiModelProperty(value = "分类ID")
    private Integer cid;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;

    @ApiModelProperty(value = "状态（0：下架，1：上架）")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty("评论数量")
    private Integer reviewCount;

}
