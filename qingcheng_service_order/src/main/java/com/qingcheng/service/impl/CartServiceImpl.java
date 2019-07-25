package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.CacheKey;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.PreferentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/22 16:48
 * @Description:
 */

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PreferentialService preferentialService;

    @Reference
    private SkuService skuService;


    @Reference
    private SpuService spuService;

    @Reference
    private CategoryService categoryService;

    /**
     * 获取购物车列表
     *
     * @param username
     * @return List<Map < String, Object>>
     * [
     * {
     * "item":"",   // type:OrderItem
     * "checked":""  // type:boolean
     * },
     * ...
     * ]
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        List<Map<String, Object>> cartList = null;
        try {
            cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);
        } catch (Exception e) {
            redisTemplate.boundHashOps(CacheKey.CART_LIST).delete(username);
        }
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 加入购物车
     *
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {
        // 获取该用户的购物车
        List<Map<String, Object>> cartList = findCartList(username);
        //判断商品的合法性
        Sku sku = skuService.findById(skuId);
        if (sku == null) {
            throw new RuntimeException("商品不存在！");
        }
        if (!sku.getStatus().equals("1")) {
            throw new RuntimeException("商品已下架");
        }
        if (sku.getNum() <= 0) {
            throw new RuntimeException("商品数量非法");
        }
        boolean flag = false;
        for (Map<String, Object> map : cartList) {
            OrderItem item = (OrderItem) map.get("item");
            // 购物车中已有该商品 追加数量
            if (item.getSkuId().equals(skuId)) {
                if (item.getNum() <= 0) {
                    cartList.remove(map);
                    break;
                }
                int weight = item.getWeight() / item.getNum();
                item.setNum(item.getNum() + num);
                item.setWeight(weight * item.getNum());
                item.setMoney(item.getPrice() * item.getNum());
                if (item.getNum() <= 0) {
                    cartList.remove(map);
                }
                flag = true;
                break;
            }
        }
        if (!flag) {
            Map<String, Object> map = new HashMap<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(skuId);
            if (num > sku.getNum()) {
                throw new RuntimeException("商品库存不足！");
            }
            orderItem.setNum(num);
            orderItem.setName(sku.getName());
            orderItem.setWeight(sku.getWeight() * num);
            orderItem.setMoney(sku.getPrice() * num);
            orderItem.setPrice(sku.getPrice());
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setImage(sku.getImage());
            Spu spu = spuService.findById(sku.getSpuId());
            if (spu == null) {
                throw new RuntimeException("商品错误！");
            }
            orderItem.setCategoryId3(spu.getCategory3Id());
            orderItem.setCategoryId2(spu.getCategory2Id());
            orderItem.setCategoryId1(spu.getCategory1Id());
//            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());
//            if (category3 == null) {
//                category3 = categoryService.findById(sku.getCategoryId());
//                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category3.getId(), category3);
//            }
//            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(category3.getParentId());
//            if (category2 == null) {
//                category2 = categoryService.findById(category3.getParentId());
//                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category2.getId(), category2);
//            }
//            orderItem.setCategoryId2(category2.getId());
//            orderItem.setCategoryId1(category2.getParentId());
            map.put("item", orderItem);
            map.put("checked", true);
            cartList.add(map);
        }
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
    }

    @Override
    public boolean updateChecked(String username, String skuId, boolean checked) {
        List<Map<String, Object>> cartList = findCartList(username);
        boolean isChange = false;
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (orderItem.getSkuId().equals(skuId)) {
                map.put("checked", checked);
                isChange = true;
            }
        }
        if (isChange) {
            redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
        }
        return isChange;
    }

    @Override
    public void deleteCheckedCart(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        // 获取未选中的购物车
        List<Map<String, Object>> uncheckedCartList = cartList.stream().filter(cart -> ((boolean) cart.get("checked") == false)).collect(Collectors.toList());
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, uncheckedCartList);
    }

    @Override
    public int preferential(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        List<OrderItem> orderItemList = cartList.stream().filter(cart -> ((boolean) cart.get("checked") == true)).map(cart -> (OrderItem) cart.get("item")).collect(Collectors.toList());
        Map<Integer, IntSummaryStatistics> cartMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getCategoryId3, Collectors.summarizingInt(OrderItem::getMoney)));
        int allPreMoney = 0;
        for (Integer categoryId : cartMap.keySet()) {
            int money = (int) cartMap.get(categoryId).getSum();
            int preMoney = (int) preferentialService.findPreMoneyByCategoryId(categoryId, money);
            allPreMoney += preMoney;
            System.out.println("分类：" + categoryId + " 消费金额：" + money + " 优惠金额：" + preMoney);
        }
        return allPreMoney;
    }

    @Override
    public List<Map<String, Object>> findNewOrderItemList(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        boolean isChange = false;
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            Sku sku = skuService.findById(orderItem.getSkuId());
            orderItem.setPrice(sku.getPrice());
            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
            isChange = true;
        }
        if (isChange) {
            redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
        }
        return cartList;
    }
}
