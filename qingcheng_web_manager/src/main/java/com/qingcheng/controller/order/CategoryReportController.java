package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/4 18:06
 * @Description:
 */

@RestController
@RequestMapping("/cateReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService;

    @GetMapping("/cate1")
    public List<Map> category1Count(String start, String end){
        System.out.println("--------------start"+start+"========="+end );
        return categoryReportService.category1Count(start,end);
    }

}
