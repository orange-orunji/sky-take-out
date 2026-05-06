package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Resource
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void inTimeCancel(){
        log.info("开始进行订单状态转换为取消...");
        List<Orders> list =  orderMapper.find(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));

        if(!list.isEmpty()){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时,自动取消");
                orderMapper.update(orders);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void finishOrder(){
        log.info("开始进行订单状态转换为完成...");
        List<Orders> list =  orderMapper.find(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(60));

        if (!list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
