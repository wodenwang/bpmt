/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.excel;

import java.io.InputStream;

/**
 * @author woden
 * 
 */
public interface Parser<T> {

	public T getResult();

	public Parser<T> parse(InputStream is) throws Exception;
}
