package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/2 23:56
 * @Description:
 */

public interface CategoryReportService {

    /**
     * 每天生产销量记录，通过SpringTask再服务器空闲时间查询
     * @param date
     * @return
     */
    List<CategoryReport> findYesterday(LocalDate date);

    /**
     * 获取销量数据
     */
    List<CategoryReport> findList(String start , String end ,Map<String,Object> searchMap);

    void saveCategoryReport(List<CategoryReport> items);

    /**
     * 获取一级分类的销量数据
     * @param start
     * @param end
     * @return
     */
    List<Map> category1Count(String start , String end);
}
