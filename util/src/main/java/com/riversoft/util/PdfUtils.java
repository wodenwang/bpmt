package com.riversoft.util;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @borball on 3/28/2016.
 */

public class PdfUtils {

    /**
     * 把pdf文件里面form抽取出来放到map里面
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static Map<String, String> parse(InputStream inputStream) throws IOException {
        PdfReader reader = new PdfReader(inputStream);
        AcroFields fields = reader.getAcroFields();
        Set<String> fldNames = fields.getFields().keySet();

        Map<String, String> map = new HashMap<>();
        for (String fieldName : fldNames) {
            map.put(fieldName, fields.getField(fieldName));
        }

        return map;
    }

}
