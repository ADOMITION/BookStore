package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.Result;
import com.books.entity.User;
import com.books.mapper.OrderMapper;
import com.books.mapper.UserMapper;
import com.books.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;


    @Override
    public IPage pageCC(Page<User> page, Wrapper wrapper) {
        return userMapper.pageCC(page, wrapper);
    }

    @Override
    public Result getUserProfile(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        //获取阅读积分
        List<BigDecimal> amounts = orderMapper.selectCompletedAmounts(userId);
        BigDecimal total = amounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算积分（取总金额整数部分）
        user.setScore(total.intValue());

        user.setPassword(null); // 清除密码
        return Result.suc(user);
    }

    @Override
    public Result updateUser(User user) {
        // 参数验证
        if (user.getId() == null) {
            return Result.fail("用户ID不能为空");
        }
        if (StringUtils.hasText(user.getNo())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getNo, user.getNo())
                    .ne(User::getId, user.getId());
            int count = userMapper.selectCount(wrapper);
            if (count > 0) {
                return Result.fail("用户名已存在");
            }
        }
        // 更新密码
        int result = userMapper.updateById(user);
        return result > 0 ? Result.suc() : Result.fail("更新失败");
    }

    @Override
    public void updateAvatar(Integer userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        baseMapper.updateById(user);
    }

}
