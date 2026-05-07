package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @return
     */
    PageResult historyOrders(Integer page, Integer pageSize, Integer status);

    /**
     * 订单详情
     * @param id
     * @return
     */
    OrderVO detail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancle(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单列表
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 确认订单
     * @param ordersDTO
     */
    void confirm(OrdersDTO ordersDTO);

    /**
     * 拒单
     * @param orders
     */
    void rejection(Orders orders);

    /**
     * 取消订单
     * @param orders
     */
    void cancelByOrder(Orders orders);

    /**
     * 派送订单
     * @param orders
     */
    void delivery(Orders orders);

    /**
     * 完成订单
     * @param orders
     */
    void complete(Orders orders);

    /**
     * 催单提醒
     * @param id
     */
    void reminder(Long id);
}
