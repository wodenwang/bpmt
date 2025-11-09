package com.riversoft.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * Created by exizhai on 12/02/2015.
 */
public class FileCompress {

    public static void unzip(File zipFile, File targetFolder) throws Exception {
        if (endsWithZip(zipFile)) {
            ZipFile zip = new ZipFile(zipFile);
            Enumeration<ZipArchiveEntry> e = zip.getEntries();
            while (e.hasMoreElements()) {
                ZipArchiveEntry entry = e.nextElement();
                File file = new File(targetFolder, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    InputStream is = zip.getInputStream(entry);
                    File parent = file.getParentFile();
                    if (parent != null && parent.exists() == false) {
                        parent.mkdirs();
                    }
                    FileOutputStream os = new FileOutputStream(file);
                    try {
                        IOUtils.copy(is, os);
                    } finally {
                        os.close();
                        is.close();
                    }
                    file.setLastModified(entry.getTime());
                }
            }
        } else {
            throw new Exception("不支持非zip文件的解压.");
        }
    }

    private static boolean endsWithZip(File file) {
        return file.getName().toLowerCase().endsWith("zip");
    }

}
