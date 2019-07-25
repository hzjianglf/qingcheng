package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/2 23:27
 * @Description:
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {


    @Select("SELECT " +
            "category_id1 categoryId1,category_id2 categoryId2,category_id3 categoryId3, " +
            "DATE_FORMAT(o.pay_time,'%Y-%m-%d' )  countDate,SUM(oi.num) num, " +
            "SUM(oi.pay_money) money " +
            "FROM  " +
            "tb_order o, tb_order_item oi " +
            "WHERE o.pay_status='1' AND o.id = oi.order_id AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')=#{date} " +
            "GROUP BY " +
            "category_id1, category_id2 ,category_id3 , DATE_FORMAT(o.pay_time,'%Y-%m-%d') ")
    List<CategoryReport> findYesterday(@Param("date") LocalDate date);


    @Select("SELECT " +
            "category_id1 categoryId1,category_id2 categoryId2,category_id3 categoryId3" +
            ",DATE_FORMAT(o.pay_time,'%Y-%m-%d' )  countDate,SUM(oi.num) num ," +
            "SUM(oi.pay_money) money " +
            "FROM  " +
            "tb_order o, tb_order_item oi " +
            "WHERE o.pay_status='1' AND o.id = oi.order_id AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')> #{start} AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')<#{end} " +
            "GROUP BY " +
            "category_id1, category_id2 ,category_id3 , DATE_FORMAT(o.pay_time,'%Y-%m-%d')=#{date}")
    List<CategoryReport> findListByDate(@Param("satrt")String start,@Param("end")String end);


    @Select("SELECT name name,sum(num)count,sum(money) money  " +
            "FROM v_category ,tb_category_report  " +
            "WHERE id = category_id1 AND DATE_FORMAT(count_date,'%Y-%m-%d')>#{start} " +
            "AND  DATE_FORMAT(count_date,'%Y-%m-%d')<#{end}" +
            "GROUP BY category_id1 ")
    List<Map> category1Count(@Param("start")String start , @Param("end") String end);
}
