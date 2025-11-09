/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.script.BasicScriptExecutionContext;
import com.riversoft.util.Formatter;

/**
 * @author Borball
 * 
 */
public class ExpressionAndScriptExecutorsTest {

	@BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:applicationContext-scripts-test.xml");
	}

	@Test
	public void testBasicEl() {

		String expression = "${((G1 + G2 + G3) * 0.1) + G4}";

		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("G1", 100f);
		scope.put("G2", 200f);
		scope.put("G3", 300f);
		scope.put("G4", 1000f);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals(1060.0f, Float.valueOf(executors.evaluateEL(expression, context).toString()), 0);
	}

	@Test
	public void testFormatter() {
		String expression = "fmt:formatDate(date)";

		Date date = new Date();
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("date", date);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals(Formatter.formatDate(date),
				executors.evaluateScript(ScriptType.JSR223, expression, context));

	}

	@Test
	public void testFormatterWithParameters() {
		String expression = "${fmt:formatDatetime(date, 'yyyy-MM-dd HH:mm:ss')}";

		Date date = new Date();
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("date", date);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals(Formatter.formatDatetime(date, "yyyy-MM-dd HH:mm:ss"),
				executors.evaluateEL(expression, context));

	}

	@Test
	public void testFormatChinesePrice() {
		String expression = "${fmt:formatChinesePrice(price)}";

		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("price", new BigDecimal(100003.25));

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");
		Assert.assertEquals("壹拾万零叁元贰角伍分", executors.evaluateEL(expression, context));

	}

	@Test
	public void testStringAppend() {
		String expression = "${str1 + str2 + str3}";

		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("str1", "String 1;");
		scope.put("str2", "String 2;");
		scope.put("str3", "String 3;");

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals("String 1;String 2;String 3;", executors.evaluateEL(expression, context));

	}

	@Test
	public void testUserInfo() {
		User user = createUser();

		MockHttpExecutionContext context = new MockHttpExecutionContext(user);

		String expression1 = "${user.name}";
		String expression2 = "${user.name == 'test'}";

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals("test", executors.evaluateEL(expression1, context));

		Assert.assertTrue((Boolean) executors.evaluateEL(expression2, context));

	}

	@Test
	public void testInHttpContext() {
		User user = createUser();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user1", user);
		request.setSession(session);
		request.setAttribute("user2", user);

		ScriptExecutionContext context = new HTTPExecutionContext(request);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		String expression1 = "${user1.name}";
		Assert.assertEquals("test", executors.evaluateEL(expression1, context));

		String expression2 = "${user2.name == 'test'}";
		Assert.assertTrue((Boolean) executors.evaluateEL(expression2, context));

		String expression3 = "${size(user1.roles)}";
		Assert.assertEquals(2, (Integer) executors.evaluateEL(expression3, context), 0);

	}

	@Test
	public void testBasicScriptJSR223() {
		String code = "while (x < 10) x = x + 1;";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("x", 1);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertEquals(10, (Integer) executors.evaluateScript(ScriptType.JSR223, code, context), 0);
	}

	@Test
	public void testElwithJSR223() {
		String code = "${true}";
		Map<String, Object> scope = new HashMap<String, Object>();

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertTrue((Boolean) executors.evaluateEL(code, context));
	}

	@Test
	public void testBasicScriptGroovy() {
		Date date = new Date();

		String script = "now = fmt.formatDate(date)\n" + "now == dateString";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("date", date);
		scope.put("dateString", Formatter.formatDate(date));
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Assert.assertTrue((Boolean) executors.evaluateScript(ScriptType.GROOVY, script, context));
	}

	@Test
	public void testBasicReturnGroovy() {
		String script = "return true;";
		Map<String, Object> scope = new HashMap<String, Object>();
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");
		Assert.assertTrue((Boolean) executors.evaluateScript(ScriptType.GROOVY, script, context));
	}

	@Test
	public void testBasicScriptGroovyWithNoMethodExisting() {
		Date date = new Date();

		String script = "now = fmt.formatDate1(date)\n" + "now == dateString";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("date", date);
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		try {
			executors.evaluateScript(ScriptType.GROOVY, script, context);
			Assert.fail();
		} catch (SystemRuntimeException ex) {
			Assert.assertTrue(ExceptionUtils
					.getRootCause(ex)
					.getMessage()
					.startsWith(
							"No signature of method: com.riversoft.core.script.function.FormatterFunction.formatDate1()"));
		}
	}

	@Test
	public void testBasicScriptGroovyWithNoPropertyExisting() {
		User user = createUser();

		MockHttpExecutionContext context = new MockHttpExecutionContext(user);

		String script = "email = user.email";
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		try {
			executors.evaluateScript(ScriptType.GROOVY, script, context);
			Assert.fail();
		} catch (SystemRuntimeException ex) {
			Assert.assertTrue(ExceptionUtils.getRootCause(ex).getMessage()
					.startsWith("No such property: email for class: com.riversoft.core.script.User"));
		}
	}

	@Test(expected = SystemRuntimeException.class)
	public void testBasicScriptGroovyWithCompileError() {
		String script = "abc'test[].error";
		Map<String, Object> scope = new HashMap<String, Object>();

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		executors.evaluateScript(ScriptType.GROOVY, script, context);
		Assert.fail();
	}

	private User createUser() {
		User user = new User();
		user.setId(1);
		user.setName("test");

		Role role1 = new Role();
		role1.setId(1);
		role1.setName("role1");

		Role role2 = new Role();
		role2.setId(2);
		role2.setName("role2");
		Set<Role> roles = new HashSet<Role>();
		roles.add(role1);
		roles.add(role2);

		user.setRoles(roles);
		return user;
	}

	@Test
	public void testAnnotation() {
		Date date = new Date();
		String script = "now=test.format(date)";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("date", date);
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		long now = (long) executors.evaluateScript(ScriptType.GROOVY, script, context);
		Assert.assertEquals(date.getTime(), now);
	}

	@Test
	public void testDBHelperGrrovy() {
		String sql = "select * from US_USER where USER_ID = ?";
		String para = "admin";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("sqlxx", sql);
		scope.put("para", para);
		String script = "db.find(sqlxx, para)";
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Object result = executors.evaluateScript(ScriptType.GROOVY, script, context);
		System.out.println(JsonMapper.defaultMapper().toJson(result));
	}

	@Test
	public void testDBHelperEL() {
		String sql = "select * from US_USER where USER_ID = ?";
		String para = "admin";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("sql", sql);
		scope.put("para", para);
		String script = "${db:find(sql,para)}";
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
				"expressionAndScriptExecutors");

		Object result = executors.evaluateEL(script, context);
		System.out.println(JsonMapper.defaultMapper().toJson(result));
	}

	@Test
	public void testPerformance() {
		Date date = new Date();
		String script = "now=test.format(date)";
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);
		int i = 0;
		while (i < 1000000) {
			BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
			ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
					"expressionAndScriptExecutors");

			long now = (long) executors.evaluateScript(ScriptType.GROOVY, script, context);
			Assert.assertEquals(date.getTime(), now);
			i++;
		}
	}
}
