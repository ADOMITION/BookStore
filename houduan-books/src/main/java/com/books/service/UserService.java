package com.books.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.Result;
import com.books.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


public interface UserService extends IService<User> {
    IPage pageCC(Page<User> page, Wrapper wrapper);

    Result getUserProfile(Integer id);

    Result updateUser(User user);

    void updateAvatar(Integer userId, String avatarUrl);

}
