package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where id = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询历史订单数据
     * @return
     */
    List<Orders> list(OrdersPageQueryDTO ordersDTO);

    /**
     * 根据id删除订单数据
     * @param id
     */
    @Delete("delete from orders where id = #{id}")
    void delete(Long id);

    /**
     * 根据状态和下单时间批量查询的订单数据
     * @param pendingPayment
     * @param localDateTime
     * @return
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time < #{localDateTime}")
    List<Orders> find(Integer pendingPayment, LocalDateTime localDateTime);

    /**
     * 根据id查询订单数据
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 获取日期对应的营收额
     *
     * @param map
     * @return
     */
    double turnoverStatistics(Map map);
}
