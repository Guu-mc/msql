package com.github.mc.msql.annotations;

import java.lang.annotation.*;

/**
 * 用于 Msql 忽略模板数据(mo)的字段
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MIgnore {
}
