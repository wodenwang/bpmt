package com.riversoft.dtask.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import com.riversoft.util.sys.SystemInfo;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 最优:
 * 2G 内存: 1024M
 * 4G: 2048M
 * 4G 以上 64: 4096M, 32: 2048M
 * 最小: 512M (待验证)
 * Created by exizhai on 05/01/2015.
 */
public class JvmSettingTask extends BaseRiverTask {

    private Logger logger = LoggerFactory.getLogger("JvmSettingTask");
    private File installationRoot;
    private String option;

    public void setInstallationRoot(String installationRoot) {
        this.installationRoot = new File(installationRoot);
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    protected void doExecute() throws BuildException {
        try {
            long mem = SystemInfo.getMem().getTotal() / (1000 * 1000 * 1000);
            logger.info("当前系统总内存:" + mem + "GB.");
            if (miniUsed()) {
                logger.info("选择最小资源模式.");
                copy2path(512);
            } else {
                if (mem <= 2) {
                    copy2path(1024);
                } else {
                    if(is64OS()) {
                        if (mem >= 6) {
                            copy2path(4096);
                        } else {
                            copy2path(2048);
                        }
                    } else {
                        logger.warn("当前正使用32位版本,为得到更优性能建议使用64位版本.");
                        copy2path(2048);
                    }
                }
            }
        }catch (Exception e) {
            logger.error("优化失败:", e);
            throw new BuildException("优化失败:" + e.getMessage());
        }
    }

    private boolean is64OS() {
        return getVersion().contains("64");
    }

    private boolean miniUsed() {
        return "min".equalsIgnoreCase(option);
    }

    private void copy2path(int size) throws IOException {
        logger.info("将会使用" + size + "M堆内存.");
        Path templatePath = Paths.get(installationRoot.getAbsolutePath(), "tools", "internal", "files", "jvm");
        File templateFolder = templatePath.toFile();
        Path setEnvPath = Paths.get(installationRoot.getAbsolutePath(), "common", "CATALINA_BASE", "bin", getDestName());
        File setEnvFile = setEnvPath.toFile();

        File batTemplate = new File(templateFolder, getTemplateName(size));
        FileUtils.copyFile(batTemplate, setEnvFile);

    }

    private String getDestName() {
        if(windows()) {
            return "setenv.bat";
        } else {
            return "setenv.sh";
        }
    }

    private boolean windows() {
        return SystemInfo.getOSInfo().toLowerCase().contains("windows");
    }

    private String getVersion(){
        File conf = new File(installationRoot, "conf");
        if(conf != null && conf.isDirectory()) {
            Collection<File> plats = FileUtils.listFiles(conf, new String[]{"plat"}, false);
            if(!plats.isEmpty()) {
                File plat = plats.iterator().next();
                try {
                    return FileUtils.readFileToString(plat);
                } catch (IOException e) {
                }
            }
        }

        return "";
    }

    private String getTemplateName(int size) {
        if(windows()) {
            return "setenv-" + size + ".bat";
        } else {
            return "setenv-" + size + ".sh";
        }
    }
}
