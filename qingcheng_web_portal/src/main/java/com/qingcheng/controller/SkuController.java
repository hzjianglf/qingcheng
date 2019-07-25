package com.qingcheng.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/11 16:35
 * @Description:
 */

@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    @RequestMapping("/price")
    public Integer price(String id) {
        return skuService.findPrice(id);
    }
}
