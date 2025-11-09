package com.riversoft.scheduler.annotation;

import java.lang.annotation.*;

/**
 * Created by exizhai on 6/21/2015.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzJob {

    String name();
    String group() default "DEFAULT_GROUP";
    String cronExp();
    String desc();
}
