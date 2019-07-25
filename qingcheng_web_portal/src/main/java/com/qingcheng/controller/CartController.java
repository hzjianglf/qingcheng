package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.user.Address;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.user.AddressService;
import com.qingcheng.service.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/22 16:56
 * @Description:
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private AddressService addressService;

    @Reference
    private OrderService orderService;

    @GetMapping("/findCartList")
    public List<Map<String, Object>> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cartList = cartService.findCartList(username);
        return cartList;
    }

    @GetMapping("/addCartList")
    public Result addCartList(String skuId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username, skuId, num);
        return new Result();
    }

    @GetMapping("/buy")
    public void buy(HttpServletResponse response, String skuId, Integer num) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username, skuId, num);
        response.sendRedirect("/cart.html");
    }

    @GetMapping("/updateChecked")
    public Result updateChecked(String skuId, boolean checked) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateChecked(username, skuId, checked);
        return new Result();
    }

    @GetMapping("/deleteCheckedCart")
    public Result deleteCheckedCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteCheckedCart(username);
        return new Result();
    }

    @GetMapping("/preferential")
    public Map preferential() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int preferential = cartService.preferential(username);
        Map<String, Integer> map = new HashMap<>();
        map.put("preferential", preferential);
        return map;
    }

    @GetMapping("/findNewOrderItemList")
    public List<Map<String, Object>> findNewOrderItemList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> newOrderItemList = cartService.findNewOrderItemList(username);
        return newOrderItemList;
    }

    @GetMapping("/findAddressList")
    public List<Address> findAddressList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findByUsername(username);
    }

    @PostMapping("/saveOrder")
    public Map<String,Object> saveOrder(@RequestBody Order order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUsername(username);
        Map<String, Object> map = orderService.add(order);
        return map;
    }
}
