package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ResoprtService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
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
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd") LocalDate end
            ){
        log.info("营业额统计:开始时间{},结束时间{}",begin,end);
        return Result.success(resoprtService.turnoverStatistics(begin,end));
    }

}
