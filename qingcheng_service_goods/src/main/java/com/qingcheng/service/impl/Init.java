package com.qingcheng.service.impl;

import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/11 14:55
 * @Description:
 */

@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuService skuService;
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("-----缓存预热-----");
        categoryService.saveCategoryTreeToRedis();
        skuService.saveAllPriceToRedis();
    }
}
