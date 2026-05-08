package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ResoprtService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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
    @Resource
    private WorkspaceService workspaceService;

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

    @Override
    public void excel(HttpServletResponse response) throws Exception {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        //获取表头数据
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO busDay = workspaceService.getBusinessData(beginTime, endTime);

        //获取输入对象
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //获取AOP对象
        XSSFWorkbook book = null;
        if (in != null) {
            book = new XSSFWorkbook(in);
        }

        //获取sheet页对象
        XSSFSheet sheet = null;
        if (book != null) {
            sheet = book.getSheetAt(0);
        }
        //获取行对象
        XSSFRow row = sheet.createRow(2);
        row.createCell(2).setCellValue(begin+"至"+end+"的营业额统计");
        //对表头数据进行赋值
        row= sheet.getRow(3);
        row.createCell(2).setCellValue(busDay.getTurnover());
        row.createCell(4).setCellValue(busDay.getOrderCompletionRate());
        row.createCell(6).setCellValue(busDay.getNewUsers());
        row = sheet.getRow(4);
        row.createCell(2).setCellValue(busDay.getValidOrderCount());
        row.createCell(4).setCellValue(busDay.getUnitPrice());

        //获取三十天内的营业数据并赋值到表体当中
        for (int i = 0; i < 30; i++) {
            BusinessDataVO bd = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(begin,LocalTime.MAX));

            row = sheet.createRow(i+7);
            row.createCell(1).setCellValue(begin.toString());
            row.createCell(2).setCellValue(bd.getTurnover());
            row.createCell(3).setCellValue(bd.getValidOrderCount());
            row.createCell(4).setCellValue(bd.getOrderCompletionRate());
            row.createCell(5).setCellValue(bd.getUnitPrice());
            row.createCell(6).setCellValue(bd.getNewUsers());

            begin = begin.plusDays(1);
        }

        //通过servlet向用户端存入数据
        ServletOutputStream sm = response.getOutputStream();
        book.write(sm);

        book.close();
        sm.close();
        if (in != null) {
            in.close();
        }
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
