package com.yt.test;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.test
 *
 * @author Yangtao
 * 日期：2021-07-14  21:12
 */
public class JasperTest {
    @Test
    public void test01(){
        // 定义模板jrxml文件路径
        String jrxml = "D:\\IdeaProjects\\itcastHealth\\health_parent\\jasperdemo\\src\\main\\resources\\demo.jrxml";
        // 设置编译后的路径
        String jasper = "D:\\IdeaProjects\\itcastHealth\\health_parent\\jasperdemo\\src\\main\\resources\\demo.jasper";
        //编译模板文件
        try {
            JasperCompileManager.compileReportToFile(jrxml,jasper);

            //构造数据
            Map paramters = new HashMap();
            paramters.put("reportDate","2019-10-10");
            paramters.put("company","itcast");

            List<Map> list = new ArrayList();
            Map map1 = new HashMap();
            map1.put("name","xiaoming");
            map1.put("address","beijing");
            map1.put("email","xiaoming@itcast.cn");
            Map map2 = new HashMap();
            map2.put("name","xiaoli");
            map2.put("address","nanjing");
            map2.put("email","xiaoli@itcast.cn");
            list.add(map1);
            list.add(map2);

            //填充数据
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper,paramters, new JRBeanCollectionDataSource(list));
            //设置保存地址
            String pdfPath = "D:\\test.pdf";
            //导出
            JasperExportManager.exportReportToPdfFile(jasperPrint,pdfPath);
        } catch (JRException e) {
            e.printStackTrace();
        }

    }
}
