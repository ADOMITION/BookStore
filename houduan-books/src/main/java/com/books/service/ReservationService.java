package com.books.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Reservation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


@Service
public interface ReservationService extends IService<Reservation> {

    boolean createReservation(Integer userId, Integer bookId, Integer quantity);

    boolean cancelReservation(Integer reservationId);

    void checkStockAndUpdateStatus();

    IPage<Reservation> pageByUser(Page<Reservation> page, Integer userId, Integer status);

    boolean updateQuantity(Integer id, Integer quantity);
}
