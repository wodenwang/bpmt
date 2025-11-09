package com.riversoft.core.web.widget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by borball on 14-1-12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WidgetAnnotation {

    /**
     * widget cmd
     *
     * @return
     */
    String cmd();

    /**
     * ftl resource location, i.g. classpath:widget/test/test.ftl
     *
     * @return
     */
    String ftl() default "";

    /**
     * widget 描述
     *
     * @return
     */
    String description() default "";
    
    /**
     * API文档位置,此项不配则会查询classpath:doc/widget/{cmd}.html
     * @return
     */
    String doc() default "";

}
