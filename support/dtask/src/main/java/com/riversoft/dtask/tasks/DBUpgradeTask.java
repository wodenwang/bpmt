package com.riversoft.dtask.tasks;

import com.riversoft.patch.util.ZipUtils;
import com.riversoft.util.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * A task to handle DB upgrade task
 *
 * @author exizhai
 */
public class DBUpgradeTask extends BaseRiverTask {

    private Logger logger = LoggerFactory.getLogger("DBUpgradeTask");

    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPwd;
    private String dbType;
    private String upgradeDir;
    private String backupDir;

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public void setJdbcPwd(String jdbcPwd) {
        this.jdbcPwd = jdbcPwd;
    }

    public void setUpgradeDir(String upgradeDir) {
        this.upgradeDir = upgradeDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    protected void doExecute() throws BuildException {
        String versionString = getVersionFromJar();
        if (!isSnapshotVersion(versionString)) {
            Version version = Version.valueOf(getVersionFromJar());
            File upgradeFolder = new File(upgradeDir);
            log("Current version:" + version.toString(), DRUTT_LOG_LVL);
            log("Current DB:" + dbType, DRUTT_LOG_LVL);

            TreeMap<Version, File> files = getVersionFileTreeMap(upgradeFolder);
            if (!files.isEmpty()) {
                try {
                    for (Version v : files.keySet()) {
                        if (v.compareTo(version) > 0) {
                            SQLExec sqlExec = new SQLExec();
                            sqlExec.setProject(this.getProject());
                            sqlExec.setDriver(jdbcDriver);
                            sqlExec.setUrl(jdbcUrl);
                            sqlExec.setUserid(jdbcUser);
                            sqlExec.setPassword(jdbcPwd);
                            sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
                            sqlExec.setPrint(true);

                            sqlExec.setSrc(files.get(v));
                            sqlExec.execute();
                            log(files.get(v).getName() + " performs completed.", DRUTT_LOG_LVL);
                        }
                    }

                    backup(upgradeFolder);
                } catch (Exception e) {
                    log("Perform SQL failed:" + e, DRUTT_LOG_LVL);
                    throw new BuildException("Perform SQL failed:", e);
                }
            } else {
                log("No DB upgrade found.", DRUTT_LOG_LVL);
            }
        }
    }

    private boolean isSnapshotVersion(String versionString) {
        return versionString.contains("SNAPSHOT");
    }

    private void backup(File upgradeFolder) {
        for (File file : FileUtils.listFiles(upgradeFolder, new String[]{"ddl"}, false)) {
            try {
                FileUtils.moveFileToDirectory(file, new File(backupDir), true);
            } catch (IOException e) {
                //ignore the exception silently
            }
        }
    }

    public String getVersionFromFileName(String name) {
        if (isScriptCommonUsed(name)) {
            return name.substring(0, name.lastIndexOf("."));
        } else {
            return name.substring(0, name.indexOf("-"));
        }
    }

    public boolean perfectMatchThisDB(String name) {
        return name.contains("-" + dbType.toLowerCase());
    }

    public boolean isScriptCommonUsed(String name) {
        return !name.contains("-");
    }

    public TreeMap<Version, File> getVersionFileTreeMap(File root) {
        TreeMap<Version, File> files = new TreeMap<>();

        for (File file : FileUtils.listFiles(root, new String[]{"ddl"}, false)) {
            Version v = Version.valueOf(getVersionFromFileName(file.getName()));
            File versionRelatedFile = new File(root, v.toString());
            //unzip the ddl file
            ZipUtils.unCompress(file, versionRelatedFile);//解压出来
            for (File f : FileUtils.listFiles(versionRelatedFile, new String[]{"sql"}, false)) {
                String fileName = f.getName();
                if (isScriptCommonUsed(fileName)) {
                    if (!files.containsKey(v)) {
                        files.put(v, f);
                    }
                } else {// db specific
                    if (perfectMatchThisDB(fileName)) {
                        files.put(v, f);
                    }
                }
            }

        }
        return files;
    }

    protected String getVersionFromJar() {
        String version = null;
        Enumeration<URL> resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttribs = manifest.getMainAttributes();

                        String buildlevel = mainAttribs.getValue("X-River-Build-Order");
                        if (!StringUtils.isEmpty(buildlevel)) {
                            version = mainAttribs.getValue("Implementation-Version");
                            if (!StringUtils.isEmpty(version)) {
                                is.close();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore wrong manifests on classpath
                    logger.warn("Can not get system version:", e);
                }
            }
        } catch (IOException e) {
            // Silently ignore wrong manifests on classpath
            log("Can not get current system version.", DRUTT_LOG_LVL);
        }

        return version;

    }

}
