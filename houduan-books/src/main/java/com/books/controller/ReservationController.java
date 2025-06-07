package com.books.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.Reservation;
import com.books.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @PostMapping("/listByUser")
    public Result listByUser(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        Integer userId = (Integer)param.get("userId");
        Integer status = (Integer)param.get("status");

        Page<Reservation> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<Reservation> result = reservationService.pageByUser(page, userId, status);

        return Result.suc(result.getRecords(), result.getTotal());
    }

    @PostMapping("/create")
    public Result create(@RequestBody Reservation reservation) {
        boolean success = reservationService.createReservation(
                reservation.getUserId(),
                reservation.getBookId(),
                reservation.getQuantity()
        );
        return success ? Result.suc() : Result.fail("预定失败");
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestBody Reservation reservation) {
        boolean success = reservationService.cancelReservation(reservation.getId());
        return success ? Result.suc() : Result.fail("取消失败");
    }

    @PostMapping("/updateQuantity")
    public Result updateQuantity(@RequestBody Reservation reservation) {
        boolean success = reservationService.updateQuantity(reservation.getId(), reservation.getQuantity());
        return success ? Result.suc() : Result.fail("更新失败");
    }
}
