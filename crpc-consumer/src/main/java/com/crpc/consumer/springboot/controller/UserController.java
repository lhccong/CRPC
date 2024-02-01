package com.crpc.consumer.springboot.controller;

import com.crpc.interfaces.OrderService;
import com.crpc.interfaces.UserService;
import com.crpc.starter.common.CRpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 用户控制器
 *
 * @author cong
 * @date 2024/02/01
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @CRpcReference
    private UserService userService;

    /**
     * 验证各类参数配置是否异常
     */
    @CRpcReference(group = "order-group",serviceToken = "order-token")
    private OrderService orderService;

    @GetMapping(value = "/test")
    public void test(){
        userService.test();
    }


    @GetMapping(value = "/testMaxData")
    public String testMaxData(int i){
        String result = orderService.testMaxData(i);
        System.out.println(result.length());
        return result;
    }


    @GetMapping(value = "/get-order-no")
    public List<String> getOrderNo(){
        List<String> result =  orderService.getOrderNoList();
        System.out.println(result);
        return result;
    }


}
