package com.books.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewReply {
    private Integer id;
    private Integer reviewId;
    private Integer userId;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String avatar;
}