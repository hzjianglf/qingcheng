package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderInfo;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderLog;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service(interfaceClass =OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CartService cartService;

    @Autowired
    private SkuService skuService;

    /**
     * 返回全部记录
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param order
     */
    public Map<String,Object> add(Order order) {
        List<Map<String, Object>> orderItemList = cartService.findNewOrderItemList(order.getUsername());
        List<OrderItem> orderItems = orderItemList.stream().filter(cart -> (boolean) cart.get("checked")).map(cart -> (OrderItem) cart.get("item")).collect(Collectors.toList());
        if (!skuService.deductionStock(orderItems)) {
            throw new RuntimeException("扣减库存失败！");
        }
        order.setId(idWorker.nextId() + "");
        IntStream numStream = orderItems.stream().mapToInt(OrderItem::getNum);
        IntStream moneyStream = orderItems.stream().mapToInt(OrderItem::getMoney);
        int toatalNum = numStream.sum();
        int totalMoney = moneyStream.sum();
        int preMoney = cartService.preferential(order.getUsername());
        order.setTotalNum(toatalNum);
        order.setTotalMoney(totalMoney);
        order.setPreMoney(preMoney);
        order.setPayMoney(totalMoney - preMoney);
        order.setCreateTime(new Date());
        order.setPayStatus("0"); // 0：未支付
        order.setOrderStatus("0"); // 订单状态
        order.setConsignStatus("0"); // 发货状态
        orderMapper.insert(order);

        double proportion = (double) order.getPayMoney() / totalMoney;
        //保存订单明细
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItem.setId(idWorker.nextId() + "");
            orderItem.setPayMoney((int) (orderItem.getMoney() * proportion));
            orderItemMapper.insert(orderItem);
        }
        // 清除缓存中的购物车
        cartService.deleteCheckedCart(order.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("ordersn", order.getId());
        map.put("money", order.getPayMoney());
        return map;
    }

    /**
     * 修改
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public OrderInfo findOrderInfoById(String id) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrder(orderMapper.selectByPrimaryKey(id));
        Example example =new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        orderInfo.setOrderItems(orderItems);
        return orderInfo;
    }

    @Override
    @Transactional
    public void sendOrder(List<Order> orders) {
        if (orders!=null&&orders.size()!=0){
            for (Order order : orders) {
                Date now = new Date();
                order.setConsignTime(now);
                order.setConsignStatus("1");
                OrderLog orderLog = new OrderLog();
                orderLog.setId(String.valueOf(idWorker.nextId()));
                orderLog.setConsignStatus("1");
                orderLog.setOperater("");
                orderLog.setOperateTime(now);
                orderLog.setPayStatus(order.getPayStatus());
                orderLog.setRemarks("");
                orderLog.setOrderId(Long.parseLong(order.getId()));
                orderLogMapper.insert(orderLog);
                orderMapper.updateByPrimaryKey(order);
            }
        }
    }

    @Override
    public void agreeRefund(String id) {

    }

    @Override
    public void refuse(String id, String message) {

    }



    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andLike("payType","%"+searchMap.get("payType")+"%");
            }
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
            }
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
            }
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
            }
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
            }
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
            }
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
            }
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
            }
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andLike("sourceType","%"+searchMap.get("sourceType")+"%");
            }
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
            }
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andLike("orderStatus","%"+searchMap.get("orderStatus")+"%");
            }
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andLike("payStatus","%"+searchMap.get("payStatus")+"%");
            }
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andLike("consignStatus","%"+searchMap.get("consignStatus")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
