package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Resource
    private UserMapper userMapper;
    @Resource
    private WeChatPayUtil weChatPayUtil;

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


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

}
