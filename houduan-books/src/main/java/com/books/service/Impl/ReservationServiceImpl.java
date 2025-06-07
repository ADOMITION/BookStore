package com.books.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Book;
import com.books.entity.Reservation;
import com.books.mapper.ReservationMapper;
import com.books.service.BookService;
import com.books.service.ReservationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Resource
    private BookService bookService;
    @Resource
    private ReservationMapper reservationMapper;

    @Override
    @Transactional
    public boolean createReservation(Integer userId, Integer bookId, Integer quantity) {
        Book book = bookService.getById(bookId);
        if (book == null || book.getStock() > 0) {
            return false;
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setQuantity(quantity);
        reservation.setStatus(0);
        reservation.setCreateTime(LocalDateTime.now());
        return this.save(reservation);
    }

    @Override
    @Transactional
    public boolean cancelReservation(Integer reservationId) {
        return this.removeById(reservationId);
    }

    @Override
    @Transactional
    public void checkStockAndUpdateStatus() {
        // 定时任务调用，检查库存更新状态
        List<Reservation> reservations = this.list();
        reservations.forEach(res -> {
            Book book = bookService.getById(res.getBookId());
            if (book != null && book.getStock() > 2) {
                res.setStatus(1);
                res.setUpdateTime(LocalDateTime.now());
                this.updateById(res);
            }
        });
    }

    @Override
    public IPage<Reservation> pageByUser(Page<Reservation> page, Integer userId, Integer status) {
        return page.setRecords(this.baseMapper.findUserReservations(userId, status));
    }

    @Override
    public boolean updateQuantity(Integer id, Integer quantity) {
        if (quantity < 1) return false;
        return this.update(new UpdateWrapper<Reservation>()
                .eq("id", id)
                .set("quantity", quantity));
    }
}
