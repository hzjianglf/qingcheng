package com.qingcheng.service.order;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/22 16:37
 * @Description:
 */
public interface CartService {

    /**
     * 获取购物车列表
     * @param username
     * @return
     */
    List<Map<String, Object>> findCartList(String username);


    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    void addItem(String username, String skuId, Integer num);

    /**
     * 更新商品选中状态
     * @param username
     * @param skuId
     * @param checked
     * @return
     */
    boolean updateChecked(String username, String skuId, boolean checked);

    /**
     * 删除选中的购物车
     * @param username
     */
    void deleteCheckedCart(String username);

    /**
     * 计算当前选中的购物车的优惠金额
     * @param username
     * @return
     */
    int preferential(String username);


    /**
     * 获取最新的购物车列表
     * @param username
     * @return
     */
    List<Map<String, Object>> findNewOrderItemList(String username);
}
