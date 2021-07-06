package com.yt.health.job;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 包名：com.yt.health.job
 *
 * @author Yangtao
 * 日期：2021-06-27  22:10
 */
public class JobApplication {
    public static void main(String[] args) throws IOException {
        new ClassPathXmlApplicationContext("classpath:application-job.xml");
        System.in.read();
    }
}
