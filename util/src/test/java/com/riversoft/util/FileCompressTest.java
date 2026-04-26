package com.riversoft.util;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * Created by exizhai on 14/02/2015.
 */
public class FileCompressTest {

    @Ignore
    public void testUnzip() {
        try {
            FileCompress.unzip(new File("c:\\tmp\\upload-2015.zip"), new File("c:\\tmp"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
