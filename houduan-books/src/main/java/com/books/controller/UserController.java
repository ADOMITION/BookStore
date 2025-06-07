package com.books.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Order;
import com.books.entity.OrderItemVO;
import com.books.entity.OrderVO;
import com.books.entity.User;
import com.books.service.OrderItemService;
import com.books.service.OrderService;
import com.books.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderItemService orderItemService;

    //前台个人中心
    @GetMapping("/profile")
    public Result getProfile(@RequestParam Integer id) {
        return userService.getUserProfile(id);
    }
    //前台保存个人信息
    @PostMapping("/uupdate")
    public Result updateProfile(@RequestBody User user) {
        return userService.updateUser(user);
    }

    //检查用户名是否已经存在
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no){
        List<User> list = userService.lambdaQuery().eq(User::getNo,no).list();
        return !list.isEmpty() ?Result.suc(list):Result.fail();
    }

    //最近订单
    @GetMapping("/recent")
    public Result getRecentOrders(@RequestParam Integer userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .ne("status", "已取消")
                .orderByDesc("createtime")
                .last("LIMIT 2");

        List<Order> orders = orderService.list(wrapper);

        List<OrderVO> orderVOS = orders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);

            // 获取订单商品信息
            List<OrderItemVO> items = orderItemService.getByOrderId(order.getId());
            vo.setItems(items);

            return vo;
        }).collect(Collectors.toList());

        return Result.suc(orderVOS);
    }



    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return userService.removeById(id)?Result.suc():Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return userService.save(user)?Result.suc():Result.fail();
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        return userService.updateById(user)?Result.suc():Result.fail();
    }

    //查询
    @PostMapping("/listPageC1")
    public Result listPageC1 (@RequestBody QueryPageParam query){

        HashMap param = query.getParam();
        String name = (String) param.get("name");
        String roleId = (String) param.get("roleId");
        String no = (String) param.get("no");
        String phone = (String) param.get("phone");

        Page<User> page = new Page<User>(query.getPageNum(),query.getPageSize());//当前页，每页多少条数据

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }
        if(StringUtils.isNotBlank(no) && !"null".equals(no)){
            lambdaQueryWrapper.like(User::getNo,no);
        }
        if(StringUtils.isNotBlank(phone) && !"null".equals(phone)){
            lambdaQueryWrapper.like(User::getPhone,phone);
        }
        if(StringUtils.isNotBlank(roleId) && !"null".equals(roleId)){
            lambdaQueryWrapper.eq(User::getRoleId,roleId);
        }


        IPage result = userService.pageCC(page,lambdaQueryWrapper);
        System.out.println("total=="+result.getTotal());
        return  Result.suc(result.getRecords(),result.getTotal());
    }

}
