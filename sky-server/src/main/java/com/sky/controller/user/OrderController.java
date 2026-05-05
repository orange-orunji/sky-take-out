package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端订单接口")
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersDTO){
        log.info("用户下单：{}", ordersDTO);
        return Result.success(orderService.submit(ordersDTO));
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        log.info("订单支付：{}", ordersPaymentDTO);
//        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
//        log.info("生成预支付交易单：{}", orderPaymentVO);
//        return Result.success(orderPaymentVO);
        log.info("订单支付：{}", ordersPaymentDTO);

        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
        orderPaymentVO.setPackageStr("prepay_id=test_prepay_id");
        orderPaymentVO.setNonceStr("test_nonce_str");
        orderPaymentVO.setPaySign("test_pay_sign");
        orderPaymentVO.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));

        log.info("模拟支付成功，生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);

    }

    /**
     * 历史订单查询
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(Integer page,Integer pageSize,Integer status){
        log.info("历史订单查询");
        if(status ==  null) status = 1;
        PageResult result = orderService.historyOrders(page, pageSize, status);
        log.info("历史订单查询结果：{}", result);
        return Result.success(result);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("订单详情:{}",id);
        return Result.success(orderService.getOrderDetail(id));
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancle/{id}")
    @ApiOperation("取消订单")
    public Result cancle(@PathVariable Long id) throws Exception {
        log.info("取消订单：{}",id);
        orderService.cancle(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}",id);
        orderService.repetition(id);
        return Result.success();
    }
}
