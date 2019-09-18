package com.github.mc.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MSelect {
    /**
     * 模板对象(mo) class
     */
    Class value() default Object.class;

    /**
     * 条件
     * 值存放where 内
     */
    Where[] where() default {};

    Group[] group() default {};

    Having[] having() default {};

    Order[] order() default {};

}
