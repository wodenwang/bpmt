package com.riversoft.core.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action类型<br>
 * PC端/移动端/自动适配
 * 
 * @author woden
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ActionMode {
	public static enum Mode {
		XHTML, H5, FIT, EXT
	}

	public Mode value() default Mode.XHTML;

	public String ext() default "";
}
