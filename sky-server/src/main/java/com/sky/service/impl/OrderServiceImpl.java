package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.controller.admin.WebSocketServer;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    @Resource
    private WebSocketServer webSocketServer;

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

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();

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

        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号"+outTradeNo);


        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }

    /**
     * 历史订单查询
     * @return
     */
    @Override
    public PageResult historyOrders(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);

        OrdersPageQueryDTO ordersDTO = new OrdersPageQueryDTO();
        ordersDTO.setUserId(BaseContext.getCurrentId());
        ordersDTO.setStatus(status);

        Page<Orders> orders = (Page<Orders>) orderMapper.list(ordersDTO);
        List<Object> lists = new ArrayList<>();
        if(orders!=null&&!orders.isEmpty()){
            for (Orders pages : orders) {
                List<OrderDetail> orderDetails =  orderDetailMapper.pageQuery( pages.getId());

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(pages,orderVO);
                orderVO.setOrderDetailList(orderDetails);

                lists.add(orderVO);
            }
        }
        long total = orders != null ? orders.getTotal() : 0;
        return new PageResult(total,lists);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO detail(Long id) {
        Orders order = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);

        orderVO.setOrderDetailList(orderDetailMapper.pageQuery(order.getId()));
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     * 业务规则：
     * - 待支付和待接单状态下，用户可直接取消订单
     * - 商家已接单状态下，用户取消订单需电话沟通商家
     * - 派送中状态下，用户取消订单需电话沟通商家
     * - 如果在待接单状态下取消订单，需要给用户退款
     * - 取消订单后需要将订单状态修改为“已取消”
     */
    @Override
    public   void cancle(Long id) throws Exception {
//       1.  待支付和待接单状态下，用户可直接取消订单
        Orders order = orderMapper.getByNumber(String.valueOf(id));
        if(Objects.equals(order.getStatus(), Orders.PENDING_PAYMENT)
                || Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)){
            order.setStatus(Orders.CANCELLED);
            orderMapper.update(order);
        }
//       2. 商家已接单状态下，用户取消订单需电话沟通商家
        if(Objects.equals(order.getStatus(), Orders.CONFIRMED)){
            throw new OrderBusinessException("商家已接单，取消订单需电话沟通商家");
        }
//       3. 派送中状态下，用户取消订单需电话沟通商家
        if(Objects.equals(order.getStatus(),Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException("派送中，取消订单需电话沟通商家");
        }
//       4. 如果在待接单状态下取消订单，需要给用户退款
        if(Objects.equals(order.getStatus(),Orders.TO_BE_CONFIRMED)){
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    order.getNumber(), //商户订单号
                    order.getNumber(), //商户退款单号
                    new BigDecimal("0.01"),//退款金额，单位 元
                    new BigDecimal("0.01"));//原订单金额

            //支付状态修改为 退款
            order.setPayStatus(Orders.REFUND);
        }
//       5. 取消订单后需要将订单状态修改为“已取消”
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {

        List<OrderDetail> orderDetails =
                orderDetailMapper.pageQuery(id);
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarts.add(shoppingCart);
        }
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /**
     * 订单列表
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> list = (Page<Orders>) orderMapper.list(ordersPageQueryDTO);

        List<Object> lists = new ArrayList<>();
        if(list!=null&&!list.isEmpty()){
            for (Orders orders : list) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);

                orderVO.setOrderDetailList(orderDetailMapper.pageQuery(orders.getId()));
                lists.add(orderVO);
            }
        }

        return new PageResult(list.getTotal(),lists);
    }

    /**
     * 接单
     * @param ordersDTO
     */
    @Override
    public void confirm(OrdersDTO ordersDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO,orders);
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }
    /**
     * 拒单
     * @param orders
     */
    @Override
    public void rejection(Orders orders) {
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }
    /**
     * 取消订单
     * @param orders
     */
    @Override
    public void cancelByOrder(Orders orders) {
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param orders
     */
    @Override
    public void delivery(Orders orders) {
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param orders
     */
    @Override
    public void complete(Orders orders) {
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    /**
     * 催单提醒
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);

        if(orders==null){
            throw  new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap();
        map.put("type","2");
        map.put("orderId",id);
        map.put("content","订单号:"+orders.getNumber());

        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }


}
