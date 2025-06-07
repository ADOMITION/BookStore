package com.books.controller;

import com.books.common.Result;
import com.books.entity.Register;
import com.books.entity.User;
import com.books.service.CartService;
import com.books.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Resource
    UserService userService;
    @Resource
    CartService cartService;

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User userParam,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        //查询用户
        List<User> list = userService.lambdaQuery()
                .eq(User::getNo, userParam.getNo())
                .eq(User::getPassword, userParam.getPassword())
                .list();

        if (list.isEmpty()) {
            return Result.fail("账号或密码错误");
        }

        //获取用户对象
        User realUser = list.get(0);
        Integer userId = realUser.getId();

        // 创建新会话
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate(); // 使旧会话失效
        }
        HttpSession newSession = request.getSession(true); // 创建新会话

        //设置会话属性（存整个对象）
        newSession.setAttribute("user", realUser);
        newSession.setAttribute("userId", userId);
        newSession.setAttribute("lastAccessTime", System.currentTimeMillis());

        //显式设置Cookie
        Cookie sessionCookie = new Cookie("JSESSIONID", newSession.getId());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(3600);
        sessionCookie.setSecure(request.isSecure()); // 根据环境自动设置Secure
        response.addCookie(sessionCookie);

        // 合并购物车
        cartService.mergeTempCart(userId, newSession);

        //返回脱敏数据
        User userVO = new User();
        BeanUtils.copyProperties(realUser, userVO);
        userVO.setPassword(null);

        return Result.suc(userVO);
    }

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody Register register){
        if(!register.getPassword().equals(register.getCheckPass())){
            return Result.fail("两次密码不一致");
        }

        int count1 = userService.lambdaQuery()
                .eq(User::getNo, register.getNo())
                .count();
        if (count1 > 0) {
            return Result.fail("账号已存在");
        }
        //3.校验邮箱唯一性
        int count2 = userService.lambdaQuery()
                .eq(User::getEmail, register.getEmail())
                .count();
        if (count2 > 0) {
            return Result.fail("邮箱已存在");
        }

        //实体转换
        User user = new User();
        BeanUtils.copyProperties(register, user);
        user.setRoleId(1); // 设置默认角色为用户
        return userService.save(user) ? Result.suc(user) : Result.fail("注册失败");
    }

}
