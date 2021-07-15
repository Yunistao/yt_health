package com.yt.health.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.yt.health.constant.MessageConstant;
import com.yt.health.constant.RedisMessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.CheckItem;
import com.yt.health.pojo.Order;
import com.yt.health.pojo.Setmeal;
import com.yt.health.service.OrderService;
import com.yt.health.service.SetmealService;
import com.yt.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-07-04  19:11
 */
@RequestMapping("order")
@RestController
public class OrderMobileController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetmealService setmealService;

    @Reference
    private OrderService orderService;

    @GetMapping("/exportSetmealInfo")
    public Result exportSetmealInfo(Integer id, HttpServletResponse res){
        //获得订单信息
        Map<String,Object> orderInfo = orderService.findOrderDetailById(id);
        //获得套餐详情
        Setmeal setmeal = setmealService.findDetailById2((int) orderInfo.get("setmeal_id"));
        //创建文件对象
        Document document = new Document();

        try {
            //设置响应头信息
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition","attachement;filename=setmealInfo.pdf");
            PdfWriter.getInstance(document, res.getOutputStream());
            //打开文档
            document.open();
            //设置字体
            BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",false);
            Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);
            //填写内容

            document.add(new Paragraph("体检人："+(String) orderInfo.get("member"),font));
            document.add(new Paragraph("体检套餐："+(String) orderInfo.get("setmeal"),font));
            Date orderDate = (Date) orderInfo.get("orderDate");
            document.add(new Paragraph("体检日期："+ DateUtils.parseDate2String(orderDate,"yyyy-MM-dd"),font));
            document.add(new Paragraph("预约类型："+(String) orderInfo.get("orderType"),font));

            //填写表格
            Table table = new Table(3);//3列

            //===================表格样式========================
            table.setWidth(80);
            table.setBorder(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_BASELINE);//水平对齐方式
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);//垂直对齐方式
            /*设置表格属性*/
            table.setBorderColor(new Color(0, 0, 255)); //将边框的颜色设置为蓝色
            table.setPadding(5);//设置表格与字体间的间距
            //table.setSpacing(5);//设置表格上下的间距
            table.setAlignment(Element.ALIGN_CENTER);//设置字体显示居中样式

            table.addCell(buildCell("项目名称",font));
            table.addCell(buildCell("项目内容",font));
            table.addCell(buildCell("项目解读",font));

            // 检查组
            List<CheckGroup> checkGroups = setmeal.getCheckGroups();
            if(null != checkGroups){
                for (CheckGroup checkGroup : checkGroups) {
                    // 项目名称列
                    table.addCell(buildCell(checkGroup.getName(),font));
                    // 项目内容, 把所有的检查项拼接
                    List<CheckItem> checkItems = checkGroup.getCheckItems();
                    String checkItemStr = "";
                    if(null != checkItems){
                        StringBuilder sb = new StringBuilder();
                        for (CheckItem checkItem : checkItems) {
                            sb.append(checkItem.getName()).append(" ");
                        }
                        sb.setLength(sb.length()-1); // 去最一个空格
                        // 检查项的拼接完成
                        checkItemStr = sb.toString();
                    }
                    table.addCell(buildCell(checkItemStr,font));
                    // 项目解读
                    table.addCell(buildCell(checkGroup.getRemark(),font));
                }
            }
            //在文档中添加表格
            document.add(table);
            //关闭文档
            document.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"导出失败！");
    }
    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font)
            throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }


    /**
     * 成功信息的展示
     */
    @PostMapping("/findById")
    public Result findById(int id){
        Map<String,Object> orderDetail = orderService.findOrderDetailById(id);
        return new Result(true, MessageConstant.ORDER_SUCCESS,orderDetail);
    }

    @GetMapping("/submit")
    public Result submit(@RequestBody Map<String,String>  paramMap){
        //1.获得redis连接
        Jedis jedis = jedisPool.getResource();
        //2.获得前端输入的验证码
        String validateCode = paramMap.get("validateCode");
        //3.获得redis中的验证码
        String telephone = paramMap.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_ORDER + ":" + telephone;
        String codeInRedis = jedis.get(key);
        //4.判断redis中的验证码是不否存在，不存在，return false
        if (StringUtils.isEmpty(codeInRedis)) {
            return new Result(false, "请重新获取验证码！");
        }
        //5.判断两个验证码是否相等，不相等，return false
        if (!codeInRedis.equals(validateCode)) {
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
        //6.调用orderservice服务端的submit方法
        paramMap.put("orderType",Order.ORDERTYPE_WEIXIN);
        Order order = orderService.submit(paramMap);
        return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,order);
    }
}
