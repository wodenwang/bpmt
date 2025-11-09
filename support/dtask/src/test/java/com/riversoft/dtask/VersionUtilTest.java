package com.riversoft.dtask;

import com.riversoft.util.Version;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by borball on 14-2-12.
 */
public class VersionUtilTest {

    public String getVersionFromFileName(String name){
        if(containsDBInFileName(name)) {
            return name.substring(0, name.indexOf("-"));
        } else {
            return name.substring(0, name.lastIndexOf("."));
        }
    }

    public boolean containsDBInFileName(String name) {
        return name.contains("-");
    }

    @Test
    public void testGetVersionFromFileName(){
        String fileName = "1.2.10-mysql.sql";

        String version = getVersionFromFileName(fileName);
        Assert.assertEquals("1.2.10", version);

    }

    @Test
    public void testSortFilesForMysql(){
        URL url = ClassLoader.getSystemClassLoader().getResource("version.properties");
        File root = new File(url.getFile());

        String db = "Mysql";
        TreeMap<Version, File> files = getVersionFileTreeMap(root, db);

        for(Version v: files.keySet()) {
            System.out.println(files.get(v).getName());
        }

        db = "Oracle";
        files = getVersionFileTreeMap(root, db);

        for(Version v: files.keySet()) {
            System.out.println(files.get(v).getName());
        }

        db = "sqlserver";
        files = getVersionFileTreeMap(root, db);

        for(Version v: files.keySet()) {
            System.out.println(files.get(v).getName());
        }



    }

    private TreeMap<Version, File> getVersionFileTreeMap(File root, String db) {
        TreeMap <Version, File> files = new TreeMap<>();

        for (File file : FileUtils.listFiles(root.getParentFile(), new String[]{"sql"}, false)) {
            Version v = Version.valueOf(getVersionFromFileName(file.getName()));

            if(files.containsKey(v) && containsDBInFileName(files.get(v).getName())) {
                //keep that one
            } else {
                files.put(v, file);
            }

        }
        return files;
    }
}
