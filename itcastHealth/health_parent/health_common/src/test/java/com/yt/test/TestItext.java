package com.yt.test;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 包名：com.yt.test
 *
 * @author Yangtao
 * 日期：2021-07-13  21:17
 */
public class TestItext {

    @Test
    public void test01(){
        //创建文件对象
        Document document = new Document();
        //设置文件存储
        try {
            PdfWriter.getInstance(document,new FileOutputStream(new File("d:\\iText.pdf")));
            //打开文档
            document.open();

            // 添加段落
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            document.add(new Paragraph("你好，传智播客", new Font(bfChinese)));
            //doc.add(new Paragraph("Hello 传智播客"));
           // document.add(new Paragraph("hello world!"));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
