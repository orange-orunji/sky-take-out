package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ResoprtService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResoprtServiceImpl implements ResoprtService {

    @Resource
    private OrderMapper  orderMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDateList(begin, end);

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("begin",LocalDateTime.of(date, LocalTime.MIN));
            map.put("end",LocalDateTime.of(date, LocalTime.MAX));
            map.put("status",5);

            turnoverList.add(orderMapper.turnoverStatistics(map));
        }

        return TurnoverReportVO.builder().
                dateList(StringUtil.join(",", dateList))
                .turnoverList(StringUtil.join(",",turnoverList)).build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDateList(begin, end);
        List<Integer> totalUserCount = new ArrayList<>();
        List<Integer> newUserCount = new ArrayList<>();

        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("end",LocalDateTime.of(date,LocalTime.MAX));
            totalUserCount.add(userMapper.getUserCount(map));
            map.put("begin",LocalDateTime.of(date,LocalTime.MIN));
            newUserCount.add(userMapper.getUserCount(map));
        }


        return UserReportVO.builder().
                dateList(StringUtil.join(",",dateList))
                .totalUserList(StringUtil.join(",",totalUserCount))
                .newUserList(StringUtil.join(",",newUserCount))
                .build();
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDateList(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            orderCountList.add(getOrderCount(beginTime,endTime,null));

            validOrderCountList.add(getOrderCount(beginTime,endTime, Orders.COMPLETED));
        }

        Integer orderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);

        double orderCompletionRate = 0.0;
        if(orderCount!=0){
            orderCompletionRate = (double) validOrderCount /orderCount;
        }

        return OrderReportVO
                .builder()
                .dateList(StringUtil.join(",",dateList))
                .orderCountList(StringUtil.join(",",orderCountList))
                .validOrderCountList(StringUtil.join(",",validOrderCountList))
                .totalOrderCount(orderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * top10菜品统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10Statistics(LocalDate begin, LocalDate end) {
        Map map = new HashMap();
        map.put("begin",LocalDateTime.of(begin,LocalTime.MIN));
        map.put("end",LocalDateTime.of(end,LocalTime.MAX));
        map.put("status",Orders.COMPLETED);
        List<GoodsSalesDTO> list = orderMapper.top10Statistics(map);
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),","))
                .numberList(StringUtils.join(list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),","))
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        return orderMapper.getByDate(begin,end,status);
    }

    /**
     * 获取指定时间区间内的日期列表
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getLocalDateList(LocalDate begin,LocalDate end){
        List<LocalDate> dateList = new ArrayList<>();

        while(!begin.isAfter(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        return dateList;
    }
}
