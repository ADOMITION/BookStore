package com.books.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Review对象", description="")
public class Review implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("book_id")
    private Integer bookId;

    private String content;

    private LocalDateTime createtime;

    @ApiModelProperty("用户头像")
    @TableField(exist = false)
    private String avatar;

    @TableField(exist = false)
    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("回复数")
    private Integer replyCount;

    @ApiModelProperty("状态（0：未通过，1：已通过，2：未通过）")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty("书籍名称")
    private String bookName;

    @ApiModelProperty("我的评分")
    private Float mark;

    @TableField(exist = false)
    private Boolean hasLiked; // 当前用户是否点赞

}
