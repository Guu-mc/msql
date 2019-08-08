package com.github.mc.msql.annotations;

import com.github.mc.msql.Start;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(Start.class)
public @interface Msql {
    boolean showSql() default false;
}
