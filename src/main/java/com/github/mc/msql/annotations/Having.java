package com.github.mc.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Having {
    /**
     * mo 对象属性
     */
    String key();

    /**
     * sql 语法, @ 表属性, # 对象属性 例: @ like concat(#, '%')
     */
    String value();

    /**
     * isAnd : true and, false or
     */
    boolean isAnd() default true;
}
