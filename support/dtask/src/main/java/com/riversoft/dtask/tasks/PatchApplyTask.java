/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.ImportExportResponse;
import com.riversoft.dbtool.export.Importer;
import com.riversoft.patch.Patch;
import com.riversoft.patch.Version;
import com.riversoft.patch.commands.Command;
import com.riversoft.patch.commands.CommandException;
import com.riversoft.patch.commands.CommandFactory;
import com.riversoft.patch.util.ZipUtils;
import com.riversoft.util.PropertiesLoader;
import com.riversoft.util.SHA1;
import com.riversoft.util.sys.SystemInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 平台软件自动升级，包括程序包和数据库
 *
 * @author Borball
 */
public class PatchApplyTask extends RiverSupportTask {

    private Logger logger = LoggerFactory.getLogger("PatchApplyTask");

    public final static String TEMP_PATCH_DIR_ROOT_NAME = "river_patch";
    public final static String PATCH_FILE_EXT_NAME = "pat";

    private boolean strict = true;

    private File tmpPatchRoot;
    private List<File> patchFiles;// ordered patch files
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPwd;
    private String dbType;
    private DataSource dataSource;

    public void setStrict(String strict) {
        if(!StringUtils.isEmpty(strict)) {
            if("true".equalsIgnoreCase(strict.trim()) || "false".equalsIgnoreCase(strict.trim())) {
                this.strict = Boolean.valueOf(strict.trim().toLowerCase());
            }
        }
    }

    @Override
    protected void support() {
        if (!getIdentifier().isRegister()) {
            logger.error("当前版本为非注册版本，不能进行当前操作，请联系BPMT销售支持。");
            return;
        }

        String currentVersion = getPlatformVersionFromJar();
        if (StringUtils.isEmpty(currentVersion)) {
            logger.error("获取当前系统版失败，请检查文件是否完整。");
            throw new BuildException("获取当前系统版失败，请检查文件是否完整。");
        }

        sortPatchFiles();// 列出升级包并排序

        if (!patchFiles.isEmpty()) {
            // 检查升级路径是否连续
            validateUpgradePath();

            String firstVersion = getVersionFromFileName(patchFiles.get(0).getName()).getFromVersion().toString();

            // 检查是否有从当前客户版本升级的升级包
            if (currentVersion.equals(firstVersion)) {
                apply();
            } else {
                logger.error("缺少当前版本[" + currentVersion + "] 到 [" + firstVersion + "] 的软件包，请检查。");
                throw new BuildException("缺少当前版本[" + currentVersion + "] 到 [" + firstVersion + "] 的软件包，请检查。");
            }

        }
    }

    private void sortPatchFiles() {
        Collection<File> files = FileUtils.listFiles(downloadDir, new String[]{PATCH_FILE_EXT_NAME}, false);
        patchFiles = new ArrayList<>(files);
        Collections.sort(patchFiles, new VersionComparator());

        if (!patchFiles.isEmpty()) {
            StringBuffer sb = new StringBuffer("找到以下升级包:\n");
            for (File file : patchFiles) {
                sb.append(file.getName()).append("\n");
            }
            logger.info(sb.toString());
        } else {
            logger.warn("没有可用的升级包。");
        }
    }

    public void apply() {
        logger.info("准备软件升级, 使用严格模式:" + strict);
        try {
            cleanUpTempPatchDir();// 清理临时目录

            initDataBase();

            File upgradeResult = null;
            for (File zipFile : patchFiles) {
                if (!applyPatch(zipFile)) {
                    break;
                } else {
                    upgradeResult = zipFile;
                }
            }

            if (upgradeResult != null) {
                String upgradeVersion = getVersionFromFileName(upgradeResult.getName()).getToVersion().toString();

                logger.info("软件升级完毕, 当前版本为:" + upgradeVersion + "，请重新启动应用程序。");
            }

        } catch (Exception e) {
            logger.error("软件升级出错:" + e.getLocalizedMessage());
            throw new BuildException("软件升级出错:" + e.getLocalizedMessage());
        }
    }

    private void initDataBase() {
        File jdbcConfFile = new File(confDir, "jdbc.properties");
        try {
            String jdbcConfResource = "file:" + jdbcConfFile.getCanonicalPath();
            PropertiesLoader propertiesLoader = new PropertiesLoader(jdbcConfResource);

            jdbcDriver = propertiesLoader.getProperty("jdbc.driverClassName");
            jdbcUrl = propertiesLoader.getProperty("jdbc.url");
            jdbcUser = propertiesLoader.getProperty("jdbc.username");
            jdbcPwd = propertiesLoader.getProperty("jdbc.password");
            dbType = propertiesLoader.getProperty("database.type");
        } catch (Exception e) {
            throw new BuildException("获取数据库配置文件出错:", e);
        }

    }

    private boolean applyPatch(File zipFile) throws IOException {
        File tmpPatchDir = new File(tmpPatchRoot, zipFile.getName());
        ZipUtils.unCompress(zipFile, tmpPatchDir);// 解压到临时目录

        checkCommercialUsed(tmpPatchDir, zipFile.getCanonicalPath());

        String fromVersion = getVersionFromFileName(zipFile.getName()).getFromVersion().toString();
        String toVersion = getVersionFromFileName(zipFile.getName()).getToVersion().toString();

        logger.info("准备升级:[" + fromVersion + "]->[" + toVersion + "].");
        CommandFactory commandFactory = new CommandFactory(resolvePatch(tmpPatchDir, fromVersion, toVersion),
                installationRoot, strict);
        List<Command> commands = commandFactory.getCommands();

        try {

            doOthers(tmpPatchDir, toVersion);

            logger.info("#################### 准备替换文件: ####################");
            for (Command command : commands) {
                command.execute();
            }

            return true;
        } catch (Exception e) {
            logger.error("升级失败:[" + fromVersion + "]->[" + toVersion + "]:", e);
            logger.error("将会恢复到版本:" + fromVersion);
            boolean rollback = true;
            StringBuffer error = new StringBuffer("失败原因:");
            for (Command command : commands) {
                try {
                    command.undo();
                } catch (CommandException undoException) {
                    rollback = false;
                    error.append(undoException.getMessage()).append("\n");
                }
            }
            if (!rollback) {
                logger.warn("恢复到版本:" + fromVersion + "出错，" + error.toString());
            } else {
                logger.warn("恢复到版本:" + fromVersion + "成功。");
            }

            return false;
        }

    }

    private void validateUpgradePath() {
        for (int i = 0; i < patchFiles.size(); i++) {
            if (i != (patchFiles.size() - 1)) {
                PatchVersion patchVersion = getVersionFromFileName(patchFiles.get(i).getName());
                PatchVersion nextPatchVersion = getVersionFromFileName(patchFiles.get(i + 1).getName());
                Version v1 = patchVersion.getToVersion();
                Version v2 = nextPatchVersion.getFromVersion();
                if (v1.compareTo(v2) == 0) {
                    // valid
                } else {
                    // invalid
                    throw new BuildException("缺少从[" + v1 + "-" + v2 + "]的升级包，请检查。");
                }
            }
        }
    }

    // TODO: 检查platform是否一致
    public PatchVersion getVersionFromFileName(String fileName) {
        String patchExt = "." + PATCH_FILE_EXT_NAME;
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

    private void doOthers(File tmpPatchDir, String toVersion) throws Exception {
        File versionDir = new File(tmpPatchDir, toVersion);
        if (versionDir.exists() && versionDir.isDirectory()) {
            //有辅助文件
            File sqlDir = new File(versionDir, "sql");
            if (sqlDir.exists() && sqlDir.isDirectory()) {
                executeSQL(sqlDir);
            }

            File excelDir = new File(versionDir, "excel");
            if(excelDir.exists() && excelDir.isDirectory()) {
                importExcel(excelDir);
            }

            //do other

        } else {
            //没有辅助文件
        }
    }

    private void importExcel(File excelDir) throws Exception {
        logger.info("#################### 准备导入excel文件: ####################");
        dataSource = DataSourceInstance.getInstance(jdbcDriver, jdbcUrl, jdbcUser, jdbcPwd).getDataSource();

        Importer importer = new Importer(dataSource);

        Collection<File> files = FileUtils.listFiles(excelDir, new String[] {"xls", "xlsx"}, true);
        if(files == null || files.isEmpty()) {
            logger.warn("没有数据需要导入.");
        } else {
            File file = files.iterator().next();
            ImportExportResponse response = importer.doImport(file, false, true, true);
            if(response.isSuccess()) {
                logger.info("数据导入成功:" + response.getDetailes());
            } else {
                logger.error("数据导入失败:" + response.getDetailes());
                throw new Exception("导入 " + file.getCanonicalPath() + "失败:" + response.getDetailes() );
            }
        }
    }

    private void executeSQL(File versionDir) {
        File sqlFile = null;
        for (File f : FileUtils.listFiles(versionDir, new String[]{"sql"}, false)) {
            String fileName = f.getName();
            if (isScriptCommonUsed(fileName)) {
                if (sqlFile == null) {
                    sqlFile = f;
                }
            } else {// db specific
                if (perfectMatchThisDB(fileName)) {
                    sqlFile = f;
                }
            }
        }

        if (sqlFile != null) {
            logger.info("#################### 将会执行以下SQL: #################### ");
            try {
                String sql = FileUtils.readFileToString(sqlFile);
                logger.info(sql);
            } catch (IOException e) {
                //will not happen
            }
            SQLExec sqlExec = new SQLExec();
            sqlExec.setProject(this.getProject());
            sqlExec.setDriver(jdbcDriver);
            sqlExec.setUrl(jdbcUrl);
            sqlExec.setUserid(jdbcUser);
            sqlExec.setPassword(jdbcPwd);
            sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
            sqlExec.setPrint(true);

            sqlExec.setSrc(sqlFile);
            sqlExec.execute();
            logger.info("数据库升级成功.");
        }

    }

    public boolean perfectMatchThisDB(String name) {
        return name.contains("-" + dbType.toLowerCase());
    }

    public boolean isScriptCommonUsed(String name) {
        return !name.contains("-");
    }

    private void checkCommercialUsed(File tmpPatchDir, String sourceName) {
        String identifier = SystemInfo.getIdentifier();
        String skey = getIdentifier().getName() + ":::" + identifier;
        String checkString = tmpPatchDir.getName() + ":::" + getIdentifier().getName() + ":::" + identifier + ":::"
                + SHA1.sha1(skey);

        File keyFile = new File(tmpPatchDir, "skey.dat");
        if (!keyFile.exists()) {
            throw new BuildException("升级失败，请检查升级文件" + sourceName + "是否合法途径获得。");
        } else {
            try {
                String keyInPatch = FileUtils.readFileToString(keyFile);
                if (!keyInPatch.equals(SHA1.sha1(checkString))) {
                    throw new BuildException("升级失败，请检查升级文件" + sourceName + "是否合法途径获得。");
                }
            } catch (IOException e) {
                throw new BuildException("升级失败，请检查升级文件是否合法途径获得。");
            }
        }

    }

    private void cleanUpTempPatchDir() throws IOException {
        tmpPatchRoot = new File("tmp", TEMP_PATCH_DIR_ROOT_NAME);
        if (tmpPatchRoot.exists()) {
            FileUtils.deleteDirectory(tmpPatchRoot);
            tmpPatchRoot.mkdirs();
        }
    }

    private String readCommnds(File tmpPatchDir) throws IOException {
        File commandsFile = new File(tmpPatchDir, "commands");
        if (commandsFile.exists()) {
            return FileUtils.readFileToString(commandsFile);
        } else {
            return "";
        }
    }

    private Patch resolvePatch(File tmpPatchDir, String fromVersion, String toVersion) throws IOException {
        Patch patch = new Patch();
        patch.setFromVersion(fromVersion);
        patch.setToVersion(toVersion);
        patch.setCommands(readCommnds(tmpPatchDir));
        patch.setTmpPatchDir(tmpPatchDir);

        return patch;
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
