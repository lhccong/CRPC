package com.crpc.provider.springboot.service.impl;


import com.crpc.interfaces.UserService;
import com.crpc.starter.common.CRpcService;

/**
 * 用户服务实现
 *
 * @author cong
 * @date 2024/02/01
 */
@CRpcService
public class UserServiceImpl implements UserService {

    @Override
    public void test() {
        System.out.println("test1111111");
    }
}
