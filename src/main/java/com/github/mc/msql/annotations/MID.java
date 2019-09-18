package com.github.mc.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MID {
    /**
     * 对应数据库 主键名
     */
    String value();
}
