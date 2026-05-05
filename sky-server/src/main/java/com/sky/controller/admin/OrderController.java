package com.sky.controller.admin;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端订单管理")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 订单搜索
     * @param beginTime
     * @param endTime
     * @param number
     * @param page
     * @param pageSize
     * @param phone
     * @param status
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> page(@ParametersAreNullableByDefault LocalDateTime beginTime,
                                   @ParametersAreNullableByDefault LocalDateTime endTime,
                                   String number,
                                   int page,
                                   int pageSize,
                                   String phone,
                                   Integer status){
        log.info("订单搜索:开始时间{},结束时间{},用户手机号{},订单状态{}",beginTime,endTime,phone,status);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setBeginTime(beginTime);
        ordersPageQueryDTO.setEndTime(endTime);
        ordersPageQueryDTO.setNumber(number);
        ordersPageQueryDTO.setPhone(phone);
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);

        return Result.success(orderService.page(ordersPageQueryDTO));
    }

    /**
     * 订单查询
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("订单详情查询")
    public Result<OrdersDTO> page(@PathVariable Long id){
        log.info("查询id为:{}订单详情",id);
        return Result.success(orderService.getOrderDetail(id));
    }

    /**
     * 接单
     * @param ordersDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接收订单")
    public Result confirm(@RequestBody OrdersDTO ordersDTO){
        log.info("接收订单:{}",ordersDTO);
        orderService.confirm(ordersDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param orders
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝订单")
    public Result rejection(@RequestBody Orders orders){
        log.info("拒绝订单:{}",orders);
        orderService.rejection(orders);
        return Result.success();
    }

    /**
     * 取消订单
     * @param orders
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody Orders orders){
        log.info("取消订单:{}",orders);
        orderService.cancelByOrder(orders);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单");
        Orders orders = new Orders();
        orders.setId(id);
        orderService.delivery(orders);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@RequestBody Orders orders){
        log.info("完成订单:{}",orders);
        orderService.complete(orders);
        return Result.success();
    }
}
