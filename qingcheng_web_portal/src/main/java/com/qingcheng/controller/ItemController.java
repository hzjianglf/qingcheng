package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/9 19:50
 * @Description:
 */

@RestController
public class ItemController {

    @Reference
    private SpuService spuService;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pagePath}")
    private String pagePath;


    /**
     * 生成商品页面
     * @param spuId
     */
    @GetMapping("/creatPage")
    public void creatPages(String spuId){
        Goods goods = spuService.findGoodsById(spuId);
        Spu spu = goods.getSpu();
        Map<String,List> specMap = (Map<String, List>)JSON.parse(spu.getSpecItems());
        List<String> categoryList = new ArrayList<>();
        categoryList.add(categoryService.findById(spu.getCategory1Id()).getName());
        categoryList.add(categoryService.findById(spu.getCategory2Id()).getName());
        categoryList.add(categoryService.findById(spu.getCategory3Id()).getName());
        List<Sku> skuList = goods.getSkuList();
        Map<String, Object> urlMap = new HashMap<>();
        for (Sku sku : skuList) {
            if ("1".equals(sku.getStatus())) {
                String specJson = JSON.toJSONString(JSON.parse(sku.getSpec()), SerializerFeature.MapSortField);
                urlMap.put(specJson, sku.getId() + ".html");
            }
            Context context = new Context();
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("spu", spu);
            dataModel.put("sku", sku);
            dataModel.put("categoryList", categoryList);
            for (String s : categoryList) {
                System.out.println("-------------------------------"+s);
            }
            dataModel.put("spuImages", spu.getImages().split(","));
            dataModel.put("skuImages", sku.getImages().split(","));
            Map paramItems = JSON.parseObject(spu.getParaItems());
            Map specItems = JSON.parseObject(sku.getSpec());
            dataModel.put("paramItems", paramItems);
            dataModel.put("specItems", specItems);
            Map<String , List> specListMap =  new HashMap<>();
            for (String s : specMap.keySet()) {  //{"颜色":["金色","黑色","蓝色"],"版本":["6GB+64GB"]}
                List<String> list = specMap.get(s);
                System.out.println("----------------"+list.toString());
                List<Map> listMap = new ArrayList<>();
                for (String item : list) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("option", item);
                    if (item.equals(specItems.get(s))){ //{'颜色': '红色', '版本': '8GB+128GB'}
                        map.put("checked", true);
                    }else{
                        map.put("checked", false);
                    }
                    Map spec = JSON.parseObject(sku.getSpec());
                    spec.put(s, item);
                    String specJson1 = JSON.toJSONString(JSON.parse(sku.getSpec()), SerializerFeature.MapSortField);
                    map.put("url", urlMap.get(specJson1));
                    listMap.add(map);
                }
                specListMap.put(s, listMap);
            }
            dataModel.put("specMap", specListMap);
            context.setVariables(dataModel);
            File dir = new File(pagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, sku.getId() + ".html");
            try {
                PrintWriter pw = new PrintWriter(dest, "UTF-8");
                templateEngine.process("item", context, pw);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}
