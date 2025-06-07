package com.books.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
@ApiModel(value="OrderItem对象", description="")
public class OrderItem implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private Integer orderId;

    private Integer bookId;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "单个价格")
    private BigDecimal price;


}
