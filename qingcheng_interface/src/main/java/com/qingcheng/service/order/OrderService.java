package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderInfo;

import java.time.LocalDate;
import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String,Object> searchMap);


    public PageResult<Order> findPage(Map<String,Object> searchMap,int page, int size);


    public Order findById(String id);

    /**
     * 修改OrderService的add方法的定义，修改返回值，用于返回订单号和金额
     * @param order
     * @return
     */
    public Map<String,Object> add(Order order);


    public void update(Order order);


    public void delete(String id);

    /**
     * 通过id获取订单
     * @param id
     * @return
     */
    OrderInfo findOrderInfoById(String id);

    /**
     * 订单批量发货
     */
    void sendOrder(List<Order> orders);

    void agreeRefund(String id);

    void refuse(String id,String message);



    /**
     * 订单超时
     */

    /**
     * 订单合并
     */

    /**
     * 订单拆分
     */



}
