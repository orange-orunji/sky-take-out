package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ResoprtService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResoprtServiceImpl implements ResoprtService {

    @Resource
    private OrderMapper  orderMapper;
    @Resource
    private UserMapper userMapper;

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
