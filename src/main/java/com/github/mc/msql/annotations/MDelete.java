package com.github.mc.msql.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MDelete {

    /**
     * 实例化对象(mo) class
     */
    Class value() default Object.class;
}
