package com.books.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Reservation对象", description="")
public class Reservation implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "书籍ID")
    private Integer bookId;

    @ApiModelProperty(value = "预定数量")
    private Integer quantity;

    @ApiModelProperty(value = "状态 0-预定中 1-已到货")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String bookCover;

    @TableField(exist = false)
    private Integer bookStock;

}
