package com.books.mapper;

import com.books.entity.Reservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    List<Reservation> findUserReservations(@Param("userId") Integer userId, @Param("status") Integer status);
}
