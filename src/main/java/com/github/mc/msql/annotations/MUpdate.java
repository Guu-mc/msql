package com.github.mc.msql.annotations;

import com.github.mc.msql.constant.Case;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MUpdate {
  /**
   * Case 对象属性
   *  值1 -- > 表字段的命名风格 (只支持小驼峰和下划线  默认下划线 )
   *  值2 -- > 表名 (默认为 JavaBean 的下划线命名)
   */
  String[] value() default {Case.UNDER_SCORE_CASE, Case.UNDER_SCORE_CASE};

  /**
   * 实例化对象 class
   * @return
   */
  Class pojo() default Object.class;
}