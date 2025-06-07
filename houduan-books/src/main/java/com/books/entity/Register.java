package com.books.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Register extends User {
    @ApiModelProperty("确认密码")
    @TableField(exist = false)
    private String checkPass;
}