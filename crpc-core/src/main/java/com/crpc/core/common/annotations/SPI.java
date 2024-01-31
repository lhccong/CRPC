package com.crpc.core.common.annotations;

import java.lang.annotation.*;

/**
 * SPI
 *
 * @author cong
 * @date 2024/01/31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {

    String value() default "";
}
