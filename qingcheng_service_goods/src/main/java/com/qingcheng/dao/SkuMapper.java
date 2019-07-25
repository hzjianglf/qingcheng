package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {


    /**
     * 商品库存删减
     * @param id
     * @param num
     */
    @Update("update tb_sku set num=num-#{num} where id=#{id}")
    void deductionStock(@Param("id") String id, @Param("num") Integer num);


    /**
     * 增加商品的销量
     * @param id
     * @param num
     */
    @Update("update tb_sku set saleNum=saleNum+#{num} where id=#{id}")
    void addSaleNum(@Param("id") String id, @Param("num") Integer num);
}
