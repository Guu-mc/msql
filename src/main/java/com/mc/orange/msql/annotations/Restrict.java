package com.mc.orange.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Restrict {
    /**
     * Case 对象属性
     */
    String key();
    /**
     * sql 语法, @ 表属性, # 对象属性 例: @ like #%
     */
    String value();
    /**
     *  isAnd : true and, false or
     */
    boolean isAnd() default true;

}
