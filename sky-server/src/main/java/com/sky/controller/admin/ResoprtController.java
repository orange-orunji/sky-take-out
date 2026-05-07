package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ResoprtService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/report")
@Api(tags = "统计数据相关接口")
public class ResoprtController {

    @Resource
    private ResoprtService resoprtService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate end
            ){
        log.info("营业额统计:开始时间{},结束时间{}",begin,end);
        return Result.success(resoprtService.turnoverStatistics(begin,end));
    }

    /**
     *  用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userReportVOResult(
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate end
    ){
        log.info("用户统计:开始时间{},结束时间{}",begin,end);
        return Result.success(resoprtService.userStatistics(begin,end));
    }
    /**
     *  订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderReportVOResult(
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate end
    ){
        log.info("订单统计:开始时间{},结束时间{}",begin,end);
        return Result.success(resoprtService.orderStatistics(begin,end));
    }

/**
     *  top10统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("top10统计")
    public Result<SalesTop10ReportVO> top10ReportVOResult(
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate end
    ){
        log.info("top10统计:开始时间{},结束时间{}",begin,end);
        return Result.success(resoprtService.top10Statistics(begin,end));
    }


}
