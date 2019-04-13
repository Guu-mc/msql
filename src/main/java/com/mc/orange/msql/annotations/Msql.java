package com.mc.orange.msql.annotations;

import com.mc.orange.msql.Start;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(Start.class)
public @interface Msql {
    boolean showSql() default false;
}
