package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.xml.crypto.Data;
import java.util.*;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private CheckMapper checkMapper;

    @Autowired
    private OperatingLogMapper operatingLogMapper;


    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     *
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 删除
     *
     * @param ids
     */
    public int delete(String ids) {
        int count = 0;
        List<String> idList = new ArrayList<>();
        if (ids.contains(",")){
            idList = Arrays.asList(ids.split(","));
        }else {
            idList.add(ids);
        }
        for (String id : idList) {
            Spu spu = spuMapper.selectByPrimaryKey(id);
            if ("1".equals(spu.getIsDelete())){
                continue;
            }
            spu.setIsDelete("1");
            count+=spuMapper.updateByPrimaryKeySelective(spu);
            // 日志相关
            OperatingLog operatingLog = new OperatingLog();
            operatingLog.setSpuId(id);
            operatingLog.setOperatingTime(new Date());
//                operatingLog.setUserId();
//                operatingLog.setUsername();
            operatingLog.setParamName("是否删除商品");
            operatingLog.setPreValue("0");
            operatingLog.setCurrentValue("1");
            operatingLogMapper.insert(operatingLog);
        }
        return count;
    }

    @Override
    public int resume(String ids) {
        int count = 0;
        List<String> idList = new ArrayList<>();
        if (ids.contains(",")){
            idList = Arrays.asList(ids.split(","));
        }else {
            idList.add(ids);
        }
        for (String id : idList) {
            Spu spu = spuMapper.selectByPrimaryKey(id);
            if ("0".equals(spu.getIsDelete())){
                continue;
            }
            spu.setIsDelete("0");
            count+=spuMapper.updateByPrimaryKeySelective(spu);
            // 日志相关
            OperatingLog operatingLog = new OperatingLog();
            operatingLog.setSpuId(id);
            operatingLog.setOperatingTime(new Date());
//                operatingLog.setUserId();
//                operatingLog.setUsername();
            operatingLog.setParamName("是否删除商品");
            operatingLog.setPreValue("1");
            operatingLog.setCurrentValue("0");
            operatingLogMapper.insert(operatingLog);
        }
        return count;
    }

    @Override
    public int clear(String ids) {
        int count = 0;
        List<String> idList = new ArrayList<>();
        if (ids.contains(",")){
            idList = Arrays.asList(ids.split(","));
        }else {
            idList.add(ids);
        }
        for (String id : idList) {
            Spu spu = spuMapper.selectByPrimaryKey(id);
            if ("1".equals(spu.getIsDelete())){
                Example example = new Example(Sku.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("spuId",id);
                skuMapper.deleteByExample(example);
                count+=spuMapper.deleteByPrimaryKey(id);
            }
            // 日志相关
            OperatingLog operatingLog = new OperatingLog();
            operatingLog.setSpuId(id);
            operatingLog.setOperatingTime(new Date());
//                operatingLog.setUserId();
//                operatingLog.setUsername();
            operatingLog.setParamName("永久删除商品");
            operatingLog.setPreValue(id);
            operatingLog.setCurrentValue("null");
            operatingLogMapper.insert(operatingLog);
        }
        return count;
    }

    /**
     * 添加商品
     *
     * @param goods {
     *              "spu": {
     *              "name": "这个是商品名称",
     *              "caption": "这个是副标题",
     *              "brandId": 12,
     *              "category1Id": 558,
     *              "category2Id": 559,
     *              "category3Id": 560,
     *              "freightId": 10,
     *              "image": "http://www.qingcheng.com/image/1.jpg",
     *              "images":
     *              "http://www.qingcheng.com/image/1.jpg,http://www.qingcheng.com/image/2.jp
     *              g",
     *              "introduction": "这个是商品详情，html代码",
     *              "paraItems": {"出厂年份":"2019","赠品":"充电器"},
     *              "saleService": "七天包退,闪电退货",
     *              "sn": "020102331",
     *              "specItems": {"颜色":["红","绿"],"机身内存":["64G","8G"]},
     *              "templateId": 42
     *              },
     *              "skuList": [{
     *              "sn": "10192010292",
     *              "num": 100,
     *              "alertNum": 20,
     *              "price": 900000,
     *              "spec": {"颜色":"红","机身内存":"64G"},
     *              "image": "http://www.qingcheng.com/image/1.jpg",
     *              "images":
     *              "http://www.qingcheng.com/image/1.jpg,http://www.qingcheng.com/image/2.jp
     *              g",
     *              "status": "1",
     *              "weight": 130
     *              },
     *              {
     *              "sn": "10192010293",
     *              "num": 100,
     *              "alertNum": 20,
     *              "price": 600000,
     *              "spec": {"颜色":"绿","机身内存":"8G"},
     *              "image": "http://www.qingcheng.com/image/1.jpg",
     *              2.3 代码实现
     *              2.3.1 SPU与SKU列表的保存
     *              代码实现：
     *              （1）qingcheng_pojo工程创建组合实体类
     *              "images":
     *              "http://www.qingcheng.com/image/1.jpg,http://www.qingcheng.com/image/2.jp
     *              g",
     *              "status": "1",
     *              "weight": 130
     *              }
     *              ]
     *              }
     */
    @Override
    @Transactional
    public void saveGoods(Goods goods) {
        boolean isUpdate = true;
        Spu spu = goods.getSpu();
        if(spu.getStatus()==null){
            spu.setStatus(0+"");
        }
        if (spu.getId() == null || "".equals(spu.getId())) {
            spu.setId(String.valueOf(idWorker.nextId()));
            spuMapper.insert(spu);
        } else {
            spuMapper.updateByPrimaryKey(spu);
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId", spu.getId());
            skuMapper.deleteByExample(example);
        }
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        List<Sku> skuList = goods.getSkuList();
        Date now = new Date();
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            categoryBrandMapper.insert(categoryBrand);
        }
        for (Sku sku : skuList) {
            if (sku.getId() == null || "".equals(sku.getId())) {
                sku.setId(idWorker.nextId() + "");
                sku.setCreateTime(now);
            }
            String name = spu.getName();
            if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                sku.setSpec("{}");
            }
            Map<String, String> specMap = (Map<String, String>) JSON.parse(sku.getSpec());
            for (String value : specMap.values()) {
                name += " " + value;
            }
            sku.setName(name);
            sku.setSpuId(spu.getId());
            sku.setUpdateTime(now);
            sku.setCategoryId(category.getId());
            sku.setCategoryName(category.getName());
            sku.setSaleNum(0);
            sku.setCommentNum(0);
            skuMapper.insert(sku);
        }
    }

    @Override
    public Goods findGoodsById(String id) {
        Goods goods = new Goods();
        goods.setSpu(spuMapper.selectByPrimaryKey(id));
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        goods.setSkuList(skuMapper.selectByExample(example));
        return goods;
    }

    /**
     * 商品上架
     *  Tip:需审核通过
     * @param ids 商品spuId，使用","连接
     */
    @Override
    public int marketable(String ids) {
        int count = 0;
        List<String> idList = new ArrayList<>();
        if (ids.contains(",")){
            idList = Arrays.asList(ids.split(","));
        }else {
            idList.add(ids);
        }
        for (String id : idList) {
            Spu spu = new Spu();
            spu.setId(id);
            Spu spu2 = spuMapper.selectByPrimaryKey(id);
            System.out.println(spu2);
            Spu spu1 = spuMapper.selectByPrimaryKey(spu);
            System.out.println("------------------------------------");
            System.out.println(spu1);
            if ("1".equals(spu1.getStatus())&&!"1".equals(spu1.getIsMarketable())){
                spu1.setIsMarketable("1");
                spuMapper.updateByPrimaryKeySelective(spu1);
                count++;
                // 日志相关
                OperatingLog operatingLog = new OperatingLog();
                operatingLog.setSpuId(id);
                operatingLog.setOperatingTime(new Date());
//                operatingLog.setUserId();
//                operatingLog.setUsername();
                operatingLog.setParamName("是否上架");
                operatingLog.setPreValue("0");
                operatingLog.setCurrentValue("1");
                operatingLogMapper.insert(operatingLog);
            }
        }
        return count;
    }

    @Override
    public int disMarketable(String ids) {
        int count = 0;
        List<String> idList = new ArrayList<>();
        if (ids.contains(",")){
            idList = Arrays.asList(ids.split(","));
        }else {
            idList.add(ids);
        }
        for (String id : idList) {
            Spu spu = new Spu();
            spu.setId(id);
            Spu spu1 = spuMapper.selectByPrimaryKey(spu);
            if ("1".equals(spu1.getStatus())&&"1".equals(spu1.getIsMarketable())){
                spu1.setIsMarketable("0");
                spuMapper.updateByPrimaryKeySelective(spu1);
                count++;
                // 日志相关
                OperatingLog operatingLog = new OperatingLog();
                operatingLog.setSpuId(id);
                operatingLog.setOperatingTime(new Date());
//                operatingLog.setUserId();
//                operatingLog.setUsername();
                operatingLog.setParamName("是否上架");
                operatingLog.setPreValue("1");
                operatingLog.setCurrentValue("0");
                operatingLogMapper.insert(operatingLog);
            }
        }
        return count;
    }

    /**
     * 商品审核
     * @param id
     * @param status
     * @param massage
     */
    @Override
    @Transactional
    public void audit(String id, String status, String massage) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);
        if (status.equals("1")) {//审核通过 自动上架
            spu.setIsMarketable("1");
        }
        spuMapper.updateByPrimaryKeySelective(spu);
        Check check = new Check();
        check.setId(idWorker.nextId()+"");
        check.setSpuId(id);
        check.setCheckReason(massage);
        check.setCheckTime(new Date());
        check.setCheckStatus(status);
        check.setSpuName(spuMapper.selectByPrimaryKey(spu).getName());
//        暂无
//        check.setCheckerId(0);
//        check.setCheckerName("");
        checkMapper.insert(check);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andLike("isMarketable", "%" + searchMap.get("isMarketable") + "%");
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andLike("isEnableSpec", "%" + searchMap.get("isEnableSpec") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
