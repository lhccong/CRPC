package com.crpc.core.spi.jdk;

import java.util.Iterator;
import java.util.ServiceLoader;


/**
 * 测试SPI演示
 *
 * @author admin
 * @date 2024/01/05
 */
public class TestSpiDemo {

    public static void doTest(ISpiTest iSpiTest){
        System.out.println("begin");
        iSpiTest.doTest();
        System.out.println("end");
    }

    public static void main(String[] args) {
        ServiceLoader<ISpiTest> serviceLoader = ServiceLoader.load(ISpiTest.class);
        for (ISpiTest iSpiTest : serviceLoader) {
            TestSpiDemo.doTest(iSpiTest);
        }
    }
}
