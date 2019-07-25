package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/1 08:55
 * @Description:
 */

public class OrderInfo implements Serializable {

    private Order order;

    private List<OrderItem> orderItems;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
