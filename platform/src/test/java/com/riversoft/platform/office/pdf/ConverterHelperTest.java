package com.riversoft.platform.office.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.*;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.platform.office.ConverterHelper;

/**
 * Created by exizhai on 07/11/2014.
 */
public class ConverterHelperTest {

	@BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:applicationContext-office-test.xml");
	}

	@Ignore
	public void testWord2PDF() {
		try {
			URL debugURL = ClassLoader.getSystemClassLoader().getResource("debug.properties");
			File debugFile = new File(debugURL.getFile());
			File root = debugFile.getParentFile();

			File pdfFolder = new File(root, "pdf");
			File word = new File(pdfFolder, "20140923电商业务系统需求说明书V2.doc");
			for (int i = 0; i < 10; i++) {
				File pdf = new File(pdfFolder, "20140923电商业务系统需求说明书V2" + i + ".pdf");
				OutputStream os = new FileOutputStream(pdf);

				ConverterHelper.convert(new FileInputStream(word), "doc", os, "pdf");

				Assert.assertTrue(pdf.exists());
			}
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Ignore
	public void testExcel2PDF() {
		try {
			URL debugURL = ClassLoader.getSystemClassLoader().getResource("debug.properties");
			File debugFile = new File(debugURL.getFile());
			File root = debugFile.getParentFile();

			File pdfFolder = new File(root, "pdf");
			File excel = new File(pdfFolder, "动态表结构导出.xls");
			for (int i = 0; i < 10; i++) {
				File pdf = new File(pdfFolder, "动态表结构导出" + i + ".pdf");

				OutputStream os = new FileOutputStream(pdf);
				ConverterHelper.convert(new FileInputStream(excel), "xlsx", os, "pdf");

				Assert.assertTrue(pdf.exists());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
