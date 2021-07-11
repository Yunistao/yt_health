package com.yt.health.service.imlp;

import com.alibaba.dubbo.config.annotation.Service;
import com.yt.health.dao.MemberDao;
import com.yt.health.dao.OrderDao;
import com.yt.health.service.ReportService;
import com.yt.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-07-11  21:04
 */

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Map<String, Object> getBusinessReport() {
        /**
         * 获得运营统计数据
         * Map数据格式：
         *      reportDate（当前时间）--String
         *      todayNewMember（今日新增会员数） -> number
         *      totalMember（总会员数） -> number
         *      thisWeekNewMember（本周新增会员数） -> number
         *      thisMonthNewMember（本月新增会员数） -> number
         *      todayOrderNumber（今日预约数） -> number
         *      todayVisitsNumber（今日到诊数） -> number
         *      thisWeekOrderNumber（本周预约数） -> number
         *      thisWeekVisitsNumber（本周到诊数） -> number
         *      thisMonthOrderNumber（本月预约数） -> number
         *      thisMonthVisitsNumber（本月到诊数） -> number
         *      hotSetmeal（热门套餐（取前4）） -> List<Map<String,Object>>
         */
        Map<String,Object> reportData = new HashMap<>();

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 星期一
        String monday = sdf.format(DateUtils.getFirstDayOfWeek(today));
        // 星期天
        String sunday = sdf.format(DateUtils.getLastDayOfWeek(today));
        // 1号
        String firstDayOfThisMonth = sdf.format(DateUtils.getFirstDayOfThisMonth());
        // 本月最后一天
        String lastDayOfThisMonth = sdf.format(DateUtils.getLastDayOfThisMonth());


        String reportDate = sdf.format(today);
        reportData.put("reportDate",reportDate);


        //=======================会员数量===========================
        //todayNewMember 今日新增会员
        int todayNewMember = memberDao.findMemberCountByDate(reportDate);
        //totalMember
        int totalMember = memberDao.findMemberTotalCount();
        //thisWeekNewMember 本周新增会员数
        int thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);
        //thisMonthNewMember 本月新增会员数
        int thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDayOfThisMonth);
        //==================================================

        //========================订单统计============================
        //todayOrderNumber 今日预约数
        int todayOrderNumber = orderDao.findOrderCountByDate(reportDate);
        //todayVisitsNumber 今日到诊数
        int todayVisitsNumber = orderDao.findVisitsCountByDate(reportDate);
        //thisWeekOrderNumber 本周预约数
        int thisWeekOrderNumber = orderDao.findOrderCountBetweenDate(monday, sunday);
        //thisWeekVisitsNumber 本周到诊数
        int thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(monday);
        //thisMonthOrderNumber 本月预约数
        int thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(firstDayOfThisMonth, lastDayOfThisMonth);
        //thisMonthVisitsNumber 本月到诊数
        int thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDayOfThisMonth);

        //========================== 热门套餐========================
        //hotSetmeal
        List<Map<String,Object>> hotSetmeal = orderDao.findHotSetmeal();

        reportData.put("reportDate",reportDate);
        reportData.put("todayNewMember",todayNewMember);
        reportData.put("totalMember",totalMember);
        reportData.put("thisWeekNewMember",thisWeekNewMember);
        reportData.put("thisMonthNewMember",thisMonthNewMember);
        reportData.put("todayOrderNumber",todayOrderNumber);
        reportData.put("todayVisitsNumber",todayVisitsNumber);
        reportData.put("thisWeekOrderNumber",thisWeekOrderNumber);
        reportData.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        reportData.put("thisMonthOrderNumber",thisMonthOrderNumber);
        reportData.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        reportData.put("hotSetmeal",hotSetmeal);

        return reportData;

    }
}
