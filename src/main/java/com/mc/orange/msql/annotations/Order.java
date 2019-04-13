package com.mc.orange.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Order {
    /**
     * Case 对象属性
     */
    String key();
    /**
     * asc/desc , 默认asc
     */
    String value() default "asc";
}