package com.github.mc.msql.annotations;

import java.lang.annotation.*;


/**
 * 解析说明:
 * 模板对象(mo)选取 -> 如果value()返回 Object class 则选取接口方法第一个请求参数作为模板对象
 * 示例:1     @MInsert
 *            @Options(useGeneratedKeys = true, keyProperty = "id")
 *            int insert(User user);
 *
 * 示例:2    @MInsert(User.class)
 *           @Options(useGeneratedKeys = true, keyProperty = "id")
 *           int insert(@Param("id") Integer id, @Param("name") String name);
 *
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MInsert {
    /**
     * 模板对象 class
     */
    Class value() default Object.class;
}