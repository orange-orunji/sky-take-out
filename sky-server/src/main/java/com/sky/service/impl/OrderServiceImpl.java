package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private AddressBookMapper addressBookMapper;


    /**
     * 用户下单
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersDTO) {
        //判断是否存在异常(购物车为空/地址为空)
        //处理购物车为空
        ShoppingCart cart = new ShoppingCart();
        Long currentId = BaseContext.getCurrentId();
        cart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if(list.isEmpty()){
           throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL) ;
        }

        //处理地址为空
        AddressBook ab = addressBookMapper.getById(ordersDTO.getAddressBookId());
        if(ab==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //插入单条订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO,orders);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setOrderTime(LocalDateTime.now());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setAddress(ab.getDetail());
        orders.setPhone(ab.getPhone());
        orders.setConsignee(ab.getConsignee());
        orders.setUserId(currentId);

        orderMapper.insert(orders);

        //插入n条订单详情数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart sc : list) {
            OrderDetail od = new OrderDetail();
            BeanUtils.copyProperties(sc,od);
            od.setOrderId(orders.getId());
            orderDetails.add(od);
        }

        orderDetailMapper.insert(orderDetails);

        //清空对应的购物车数据
        shoppingCartMapper.delete(currentId);

        //封装返回对象
        OrderSubmitVO result = new OrderSubmitVO();
        result.setId(orders.getId());
        result.setOrderNumber(orders.getNumber());
        result.setOrderAmount(orders.getAmount());
        result.setOrderTime(orders.getOrderTime());
        return result;
    }
}
