/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.office.pdf;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;

/**
 * @author woden
 * 
 */
public class LibreOfficeStarter {
	public static void main(String[] args) {
		new DefaultOfficeManagerConfiguration().setPortNumber(2012).buildOfficeManager().start();
	}
}