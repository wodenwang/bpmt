/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woden
 *
 */
public class JxlsUtils {

	static Logger logger = LoggerFactory.getLogger(JxlsUtils.class);

	public static void exportFromTemplate(OutputStream out, InputStream template, Map<String, Object> varMap) throws IOException {
		Context context = new Context(varMap);
		JxlsHelper.getInstance().processTemplate(template, out, context);
	}

}
