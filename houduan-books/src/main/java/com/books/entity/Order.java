package com.books.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Order对象", description="")
@TableName("`order`")
public class Order implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "'待付款','已付款','已发货'，'已完成','已取消'")
    private String status;

    private LocalDateTime createtime;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "收件人姓名")
    private String recipient;

    @ApiModelProperty(value = "联系方式")
    private String tel;

    @ApiModelProperty("支付时间")
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

}
