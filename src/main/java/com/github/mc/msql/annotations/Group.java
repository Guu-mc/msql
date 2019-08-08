package com.github.mc.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Group {
    /**
     * Case 对象属性
     */
    String key();
}
