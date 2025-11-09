/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;

import com.riversoft.core.BeanFactory;

/**
 * @author Woden
 * 
 */
public class FormWidgetTest {

	@Before
	public void before() {
		BeanFactory.init();
	}

	@Ignore
	public void testParse() {

		String cmd = "select[Demo;name;value]{required:true}";
		FormWidget formWidget = new FormWidget(cmd);
		Assert.assertEquals(formWidget.getCmd(), "select");
		Assert.assertEquals(formWidget.getFormParams().get(0).getName(), "Demo");
		Assert.assertEquals(formWidget.getFormParams().get(1).getName(), "name");
		Assert.assertEquals(formWidget.getFormParams().get(2).getName(), "value");
		Assert.assertEquals(formWidget.getValidateParam(), "{required:true}");
	}

	@Ignore
	public void testTextToHtml() {
		String cmd = "text{required:true}";
		FormWidget formWidget = new FormWidget(cmd);
		System.out.println(formWidget.toHtml("a", WidgetState.readonly, "ddd", null));
	}

	@Ignore
	public void testColorPickerToHtml() {
		String cmd = "colorpicker{required:true}";
		FormWidget formWidget = new FormWidget(cmd);
		System.out.println(formWidget.toHtml("a", WidgetState.readonly, "ddd", null));
	}
}
