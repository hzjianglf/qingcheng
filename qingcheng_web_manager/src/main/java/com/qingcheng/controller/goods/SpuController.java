package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Check;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result();
    }

    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    // 逻辑删除
    @PostMapping("/delete")
    public Result delete(@RequestBody String ids){
        int count = spuService.delete(ids);
        return new Result(0,"成功删除"+count+"个商品！");
    }

    @PostMapping("/resume")
    public Result resume(@RequestBody String ids){
        int count = spuService.resume(ids);
        return new Result(0,"成功还原"+count+"个商品！");
    }

    // 永久删除
    @PostMapping("/clear")
    public Result clear(@RequestBody String ids){
        int count = spuService.clear(ids);
        return new Result(0,"成功删除"+count+"个商品！");
    }

    @GetMapping("/goods")
    public Goods findGoodsById(String id){
        return spuService.findGoodsById(id);
    }

    @PostMapping("/audit")
    public Result audit(@RequestBody Check check){
        spuService.audit(check.getSpuId(),check.getCheckStatus(),check.getCheckReason());
        return new Result();
    }

    /**
     *
     * @param ids   "1144570761713946624,62546500"
     * @return
     */
    @PostMapping("/marketable")
    public Result marketable(@RequestBody String ids){
        System.out.println(ids);
        int count = spuService.marketable(ids);
        return new Result(0,"成功上架"+count+"个商品！");
    }

    @PostMapping("/dismarketable")
    public Result disMarketable(@RequestBody String ids){
        System.out.println(ids);
        int count = spuService.disMarketable(ids);
        return new Result(0,"成功下架"+count+"个商品！");
    }



}
