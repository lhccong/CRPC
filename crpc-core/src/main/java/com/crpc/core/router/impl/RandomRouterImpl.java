package com.crpc.core.router.impl;

import com.alibaba.fastjson.JSON;
import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.registry.URL;
import com.crpc.core.router.CRouter;
import com.crpc.core.router.Selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.crpc.core.common.cache.CommonClientCache.*;

/**
 * 随机路由器impl
 *
 * @author liuhuaicong
 * @date 2023/08/22
 */
public class RandomRouterImpl implements CRouter {
    @Override
    public void refreshRouteArr(Selector selector) {
        //获取服务提供者数目
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        //提前生成调用先后顺序的随机数组
        int[] result = createRandomIndex(arr.length);
        //生成对应服务集群的每台机器的调用顺序
        for (int i = 0; i < result.length; i++) {
            arr[i] = channelFutureWrappers.get(result[i]);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(),arr);
        URL url = new URL();
        url.setServiceName(selector.getProviderServiceName());
        //更新权重
        CROUTER.updateWeight(url);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getChannelFutureWrappers());
    }

    @Override
    public void updateWeight(URL url) {
        //服务节点的权重
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(url.getServiceName());
        Integer[] weightArr = createWeightArr(channelFutureWrappers);
        Integer[] finalArr = createRandomArr(weightArr);
        ChannelFutureWrapper[] finalChannelFutureWrappers = new ChannelFutureWrapper[finalArr.length];
        for (int i = 0; i < finalArr.length; i++) {
            finalChannelFutureWrappers[i] = channelFutureWrappers.get(finalArr[i]);
        }
        SERVICE_ROUTER_MAP.put(url.getServiceName(),finalChannelFutureWrappers);
    }

    /**
     * 创建随机乱序数组
     *
     * @param arr 数组
     * @return {@link Integer[]}
     */
    static Integer[] createRandomArr(Integer[] arr) {
        int total = arr.length;
        Random ra = new Random();
        for (int i = 0; i < total; i++) {
            int j = ra.nextInt(total);
            if (i == j) {
                continue;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    /**
     * 获取权重数组
     *
     * @return {@link Integer[]}
     */
    static Integer[] createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers) {
        List<Integer> weightArr = new ArrayList<>();
        for (int k = 0; k < channelFutureWrappers.size(); k++) {
            Integer weight = channelFutureWrappers.get(k).getWeight();
            int c = weight / 100;
            for (int i = 0; i < c; i++) {
                weightArr.add(k);
            }
        }
        Integer[] arr = new Integer[weightArr.size()];
        return weightArr.toArray(arr);
    }
    private int[] createRandomIndex(int len) {
        int[] arrInt = new int[len];
        Random ra = new Random();
        Arrays.fill(arrInt, -1);
        int index = 0;
        while (index < arrInt.length) {
            int num = ra.nextInt(len);
            //如果数组中不包含这个元素则赋值给数组
            if (!contains(arrInt, num)) {
                arrInt[index++] = num;
            }
        }
        return arrInt;
    }

    public boolean contains(int[] arr, int key) {
        for (int j : arr) {
            if (j == key) {
                return true;
            }
        }
        return false;
    }
}
