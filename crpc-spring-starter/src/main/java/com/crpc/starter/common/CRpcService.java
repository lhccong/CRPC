package com.crpc.starter.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * CRPC服务
 *
 * @author cong
 * @date 2024/02/01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CRpcService {

    int limit() default 0;

    String group() default "default";

    String serviceToken() default "";

}
