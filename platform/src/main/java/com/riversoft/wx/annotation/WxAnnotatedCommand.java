package com.riversoft.wx.annotation;

import com.riversoft.platform.translate.WxCommandSupportType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @borball on 4/15/2016.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WxAnnotatedCommand {

    String name();
    WxCommandSupportType[] types();
    String desc();

}
