package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetails
     */
    void insert(List<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单明细
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> pageQuery(Long id);

    /**
     * 根据订单id删除订单明细数据
     * @param id
     */
    @Delete("delete from order_detail where order_id = #{id}")
    void delete(Long id);
}
