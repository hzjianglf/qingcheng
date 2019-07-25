package com.qingcheng.controller.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/2 23:46
 * @Description:
 */
@Component
public class OrderTask {

    @Reference
    CategoryReportService categoryReportService;

    /**
     * 保存昨天的销量信息
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void saveYesterday() {
        System.out.println(LocalDate.now().minusDays(1));
        System.out.println(categoryReportService.findYesterday(LocalDate.now().minusDays(1)));
        categoryReportService.saveCategoryReport(categoryReportService.findYesterday(LocalDate.now().minusDays(1)));
    }
}
