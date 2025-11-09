/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.junit.Assert;
import org.junit.Test;

import com.riversoft.patch.Version;

/**
 * @author Borball
 * 
 */
public class PatchApplyTaskTest {

    @Test
    public void testGetVersionFromFileName() {
        String fileName = "riversoft-1.2.1-1.2.2-win323.pat";
        PatchVersion version = getVersionFromFileName(fileName);

        Assert.assertEquals("1.2.1", version.getFromVersion().toString());
        Assert.assertEquals("1.2.2", version.getToVersion().toString());
    }

    @Test
    public void testFileOrder() {
        File file1 = new File("riversoft-1.4.6-1.4.9-win323.pat");
        File file2 = new File("riversoft-1.4.9-1.4.11-win323.pat");
        File file3 = new File("riversoft-1.4.11-1.5.0-win323.pat");
        File file4 = new File("riversoft-1.5.0-1.5.11-win323.pat");
        File file5 = new File("riversoft-1.5.11-1.6.12-win323.pat");

        List<File> files = new ArrayList<>();
        files.add(file3);
        files.add(file2);
        files.add(file1);
        files.add(file5);
        files.add(file4);
        
        Collections.sort(files, new VersionComparator());
        
        Assert.assertTrue(files.get(0).getName().equals("riversoft-1.4.6-1.4.9-win323.pat"));
        Assert.assertTrue(files.get(1).getName().equals("riversoft-1.4.9-1.4.11-win323.pat"));
        Assert.assertTrue(files.get(2).getName().equals("riversoft-1.4.11-1.5.0-win323.pat"));
        Assert.assertTrue(files.get(3).getName().equals("riversoft-1.5.0-1.5.11-win323.pat"));
        Assert.assertTrue(files.get(4).getName().equals("riversoft-1.5.11-1.6.12-win323.pat"));
        
    }

    class VersionComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            String filename1 = o1.getName();
            String filename2 = o2.getName();

            PatchVersion patchVersion1 = getVersionFromFileName(filename1);
            PatchVersion patchVersion2 = getVersionFromFileName(filename2);

            return patchVersion1.getFromVersion().compareTo(patchVersion2.getFromVersion());
        }

    }

    public PatchVersion getVersionFromFileName(String fileName) {
        String patchExt = ".pat";
        if (fileName.indexOf(patchExt) > 0) {
            String nameWithoutExt = fileName.substring(0, fileName.indexOf(patchExt));
            String[] parameters = nameWithoutExt.split("-");
            if (parameters.length == 4) {
                PatchVersion patchVersion = new PatchVersion();
                Version fromVersion = Version.valueOf(parameters[1]);
                Version toVersion = Version.valueOf(parameters[2]);

                patchVersion.setFromVersion(fromVersion);
                patchVersion.setToVersion(toVersion);
                return patchVersion;
            }
        }
        throw new BuildException("软件升级包不是预期的格式，请检查:" + fileName);
    }

    public class PatchVersion {
        Version fromVersion;
        Version toVersion;

        /**
         * @return the fromVersion
         */
        public Version getFromVersion() {
            return fromVersion;
        }

        /**
         * @param fromVersion the fromVersion to set
         */
        public void setFromVersion(Version fromVersion) {
            this.fromVersion = fromVersion;
        }

        /**
         * @return the toVersion
         */
        public Version getToVersion() {
            return toVersion;
        }

        /**
         * @param toVersion the toVersion to set
         */
        public void setToVersion(Version toVersion) {
            this.toVersion = toVersion;
        }

    }

}
