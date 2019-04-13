package com.mc.orange.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MField {
    /**
     *  对应数据库 字段名
     */
    String value();
}
