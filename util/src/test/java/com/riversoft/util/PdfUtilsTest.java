package com.riversoft.util;

import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @borball on 3/28/2016.
 */
public class PdfUtilsTest {

    @Ignore
    public void testExtract() {
        try {
            Map<String, String> map = PdfUtils.parse(new FileInputStream(new File("Gasket md_form0.pdf")));
            for (String key : map.keySet()) {
                System.out.println(key + ":" + map.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
