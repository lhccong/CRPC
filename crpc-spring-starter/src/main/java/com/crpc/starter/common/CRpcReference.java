package com.crpc.starter.common;

import java.lang.annotation.*;

/**
 * CRPC
 *
 * @author cong
 * @date 2024/02/01
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CRpcReference {

    String url() default "";

    String group() default "default";

    String serviceToken() default "";

    int timeOut() default 3000;

    int retry() default 1;

    boolean async() default false;

}
