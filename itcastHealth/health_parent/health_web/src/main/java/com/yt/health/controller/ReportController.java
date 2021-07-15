package com.yt.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.pojo.Member;
import com.yt.health.service.MemberService;
import com.yt.health.service.ReportService;
import com.yt.health.service.SetmealService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiContext;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-07-11  13:51
 */

@RequestMapping("/report")
@RestController
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        //1.获得当前日历
        Calendar calendar = Calendar.getInstance();

        //2.设置List<String> 月份集合
        calendar.add(Calendar.MONTH,-11);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        List<String> listMonth = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String time = sdf.format(calendar.getTime());
            listMonth.add(time);
            calendar.add(Calendar.MONTH,1);
        }

        Integer[] arrayMemberCount = new Integer[12];
        //3.根据月份查询会员注册人数
        for(int i=0;i<12;i++){
            String month = listMonth.get(i);
            Integer MonthMemberCount= memberService.getMemberReport(month);
            arrayMemberCount[i]=MonthMemberCount;
        }
        //4.封装成Map数据并返回数据
        Map map = new HashMap<String,Object>();
        map.put("months",listMonth);
        map.put("memberCount",arrayMemberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        //调用service 查询套餐服务数量
        List<Map<String,Object>> mapList = setmealService.findSetmealCount();

        //创建一个套餐名字集合
        List<String> nameList = new ArrayList<>();

        //获得套餐名称集合
        if (null != mapList) {
            for (Map<String, Object> map : mapList) {
                String name = (String) map.get("name");
                nameList.add(name);
            }
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("setmealCount",mapList);
        resultMap.put("setmealNames",nameList);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,resultMap);
    }


    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        Map<String,Object> resultMap = reportService.getBusinessReport();
        return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,resultMap);

    }

    @GetMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest req, HttpServletResponse res){
        //1.获得模板路径
        String template = req.getSession().getServletContext().getRealPath("/template/report_template.xlsx");

        try {
            ServletOutputStream os = res.getOutputStream();
            //2.创建工作簿(模板路径)
            XSSFWorkbook workbook = new XSSFWorkbook(template);
            //3.获得工作表
            XSSFSheet sht = workbook.getSheetAt(0);
            //4.获得运营数据
            Map<String,Object> reportData = reportService.getBusinessReport();
            //5.会员数据添加
            // 日期 坐标 2,5
            sht.getRow(2).getCell(5).setCellValue(reportData.get("reportDate").toString());
            //新增会员
            // 新增会员数 4,5
            sht.getRow(4).getCell(5).setCellValue((Integer)reportData.get("todayNewMember"));
            // 总会员数 4,7
            sht.getRow(4).getCell(7).setCellValue((Integer)reportData.get("totalMember"));
            // 本周新增会员数5,5
            sht.getRow(5).getCell(5).setCellValue((Integer)reportData.get("thisWeekNewMember"));
            // 本月新增会员数 5,7
            sht.getRow(5).getCell(7).setCellValue((Integer)reportData.get("thisMonthNewMember"));

            //6.预约到诊数据添加
            //=================== 预约 ============================
            sht.getRow(7).getCell(5).setCellValue((Integer)reportData.get("todayOrderNumber"));
            sht.getRow(7).getCell(7).setCellValue((Integer)reportData.get("todayVisitsNumber"));
            sht.getRow(8).getCell(5).setCellValue((Integer)reportData.get("thisWeekOrderNumber"));
            sht.getRow(8).getCell(7).setCellValue((Integer)reportData.get("thisWeekVisitsNumber"));
            sht.getRow(9).getCell(5).setCellValue((Integer)reportData.get("thisMonthOrderNumber"));
            sht.getRow(9).getCell(7).setCellValue((Integer)reportData.get("thisMonthVisitsNumber"));

            //7.热门套餐添加
            int row = 12;
            List<Map<String,Object>> hotSetmeal = (List<Map<String,Object>> )reportData.get("hotSetmeal");
            for (Map<String, Object> setmealMap : hotSetmeal) {
                sht.getRow(row).getCell(4).setCellValue((String)setmealMap.get("name"));
                sht.getRow(row).getCell(5).setCellValue((Long)setmealMap.get("setmeal_count"));
                BigDecimal proportion = (BigDecimal) setmealMap.get("proportion");
                sht.getRow(row).getCell(6).setCellValue(proportion.doubleValue());
                sht.getRow(row).getCell(7).setCellValue((String)setmealMap.get("remark"));
                row++;
            }
            //8.工作簿写给response 输出流
            //setContentType:区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
            res.setContentType("application/vnd.ms-excel");
            String filename = "运营统计数据报表.xlsx";
            //9.解决下载的文件名 中文乱码
            filename = new String(filename.getBytes(), "ISO-8859-1");
            //10. 设置头信息，告诉浏览器，是带附件的，文件下载
            res.setHeader("Content-Disposition","attachement;filename=" + filename);
            //工作簿输出给客户端
            workbook.write(os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 条件：数据相对固定且有规律
     * @param req
     * @param res
     */
    @GetMapping("/exportBusinessReport2")
    public void exportBusinessReport2(HttpServletRequest req, HttpServletResponse res){
        String template = req.getSession().getServletContext().getRealPath("template") + File.separator + "report_template2.xlsx";
        // 数据模型 map
        Context context = new PoiContext();
        context.putVar("obj", reportService.getBusinessReport());
        try {
            res.setContentType("application/vnd.ms-excel");
            res.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            // 把数据模型中的数据填充到文件中
            JxlsHelper.getInstance().processTemplate(new FileInputStream(template),res.getOutputStream(),context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/exportBusinessReportPDF")
    public Result exportBusinessReportPDF(HttpServletRequest req, HttpServletResponse res){
        //通过req获得模板路径
        String realPath = req.getSession().getServletContext().getRealPath("/template");
        // jrxml路径
        String jrxml = realPath + File.separator + "health_business3.jrxml";
        // jasper路径
        String jasper = realPath + File.separator + "health_business.jasper";

        try {
            JasperCompileManager.compileReportToFile(jrxml,jasper);

            Map<String, Object> businessReport = reportService.getBusinessReport();
            // 热门套餐(list -> Detail1)
            List<Map<String,Object>> hotSetmeals = (List<Map<String,Object>>)businessReport.get("hotSetmeal");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, businessReport,
                    new JRBeanCollectionDataSource(hotSetmeals));

            //保存
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition","attachement;filename=businessReport.pdf");

            JasperExportManager.exportReportToPdfStream(jasperPrint,res.getOutputStream());
            return null;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new Result(false,"导出运营数据统计pdf失败");
    }
}
