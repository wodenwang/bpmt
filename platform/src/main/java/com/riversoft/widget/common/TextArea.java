package com.riversoft.widget.common;

import com.riversoft.core.web.widget.DefaultWidget;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;

/**
 * Created by borball on 14-1-12.
 */
@WidgetAnnotation(cmd = "textarea", ftl = "classpath:widget/{mode}/common/textarea.ftl")
public class TextArea extends DefaultWidget {

	private String width = "null";
	private String height = "null";

	@Override
	public void setParams(FormValue... values) {
		if (values.length > 0) {
			width = values[0].getName();
		}

		if (values.length > 1 && !"null".equalsIgnoreCase(values[1].getName())) {
			height = values[1].getName();
		}
	}

	@Override
	public String show(Object value) {
		if (value == null) {
			return "";
		}

		return new FormWidget("textarea[" + width + ";" + height + "]").toHtml("_tmp", WidgetState.readonly, value, null);
	}
}
