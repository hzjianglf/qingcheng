package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/9 08:42
 * @Description:
 */

@Controller
public class IndexController {

    @Reference
    private AdService adService;

    @Reference
    private CategoryService categoryService;

    /**
     * 轮播图广告
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String index(Model model){
        List<Ad> index_lb = adService.findPageByPosition("index_lb");
        model.addAttribute("lbt",index_lb);
        System.out.println(index_lb);
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryList", categoryTree);
        return "index";
    }
}
