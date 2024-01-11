package com.crpc.core.spi.jdk;


/**
 * 默认 ISPI 测试
 *
 * @author cong
 * @date 2024/01/05
 */
public class DefaultISpiTest implements ISpiTest{

    @Override
    public void doTest() {
        System.out.println("执行测试方法");
    }

}
