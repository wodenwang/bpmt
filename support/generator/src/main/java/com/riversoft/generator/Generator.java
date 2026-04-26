/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import java.util.Map;

/**
 * @author Borball
 * 
 */
public interface Generator {

    public String getName();

    public String generate(Map<String, Object> context);

}
