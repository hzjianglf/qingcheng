package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/3 00:00
 * @Description:
 */

@Service(interfaceClass = CategoryReportService.class)
public class CategoryReportServiceImpl implements CategoryReportService {

    @Autowired
    private CategoryReportMapper categoryReportMapper;

    /**
     * 查询昨天的销量
     * @param date
     * @return
     */
    @Override
    public List<CategoryReport> findYesterday(LocalDate date) {
        System.out.println(date+"----------------------------------");
        return categoryReportMapper.findYesterday(date);
    }

    @Override
    public List<CategoryReport> findList(String start, String end, Map<String, Object> searchMap) {
        return categoryReportMapper.findListByDate(start,end);
    }


    @Override
    @Transactional
    public void saveCategoryReport(List<CategoryReport> items) {
        System.out.println(items);
        if (items!=null&&items.size()>0){
            for (CategoryReport item : items) {
                categoryReportMapper.insert(item);
            }
        }
    }

    @Override
    public List<Map> category1Count(String start, String end) {
        return categoryReportMapper.category1Count(start, end);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example = new Example(CategoryReport.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){

            if(searchMap.get("categoryId1")!=null ){
                criteria.andEqualTo("categoryId1",searchMap.get("categoryId1"));
            }
            if(searchMap.get("categoryId2")!=null ){
                criteria.andEqualTo("categoryId2",searchMap.get("categoryId2"));
            }
            if(searchMap.get("categoryId3")!=null ){
                criteria.andEqualTo("categoryId3",searchMap.get("categoryId3"));
            }
            if(searchMap.get("countDate")!=null ){
                criteria.andEqualTo("countDate",searchMap.get("countDate"));
            }
        }
        return example;
    }
}
