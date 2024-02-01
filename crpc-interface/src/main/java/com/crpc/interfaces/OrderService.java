package com.crpc.interfaces;

import java.util.List;

/**
 * 订购服务
 *
 * @author cong
 * @date 2024/02/01
 */
public interface OrderService {

    List<String> getOrderNoList();

    String testMaxData(int i);
}
