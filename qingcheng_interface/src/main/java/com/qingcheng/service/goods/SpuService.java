package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String,Object> searchMap);


    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);


    public Spu findById(String id);

    public void add(Spu spu);


    public void update(Spu spu);


    /**
     * 删除商品 逻辑删除
     * @param ids 商品spuId，使用","连接
     * @return
     */
    public int delete(String ids);

    /**
     * 回复逻辑删除的商品
     * @param ids 商品spuId，使用","连接
     * @return
     */
    public int resume(String ids);

    /**
     * 删除商品 永久删除
     * @param ids 商品spuId，使用","连接
     * @return
     */
    public int clear(String ids);

    void saveGoods(Goods goods);

    Goods findGoodsById(String id);

    /**
     * 商品上架
     * @param ids 商品spuId，使用","连接
     * @return  操作成功条数
     */
    int marketable(String ids);

    int disMarketable(String ids);

    /**
     * 审核商品
     * @param id
     * @param status
     * @param massage
     */
    public void audit(String id,String status,String massage);


}
