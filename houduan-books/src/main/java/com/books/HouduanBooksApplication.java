package com.books;

import com.books.service.ReservationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@SpringBootApplication
@EnableScheduling
public class HouduanBooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouduanBooksApplication.class, args);
    }

    //定时任务配置
    @Resource
    private ReservationService reservationService;

    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟执行一次
    public void checkReservationStatus() {
        reservationService.checkStockAndUpdateStatus();
    }
}
