package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.Exporter;
import com.riversoft.dtask.office.OfficeServiceHelper;
import com.riversoft.util.PropertiesLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.artofsolving.jodconverter.office.OfficeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by exizhai on 12/11/2014.
 */
public class OfficeTask extends BaseRiverTask{

    private Logger logger = LoggerFactory.getLogger("OfficeTask");
    private File installationRoot = null;
    private File officeProperties = null;
    private File tmpDirectory = null;
    private File officeInstallationFolder = null;
    private int servicePort = 2002;

    enum COMMAND {
        START, STOP, RESTART, STATUS;
    }

    private COMMAND command = COMMAND.START;

    /**
     * start, stop, restart, status
     * @param command
     */
    public void setCommand(String command) {
        this.command = COMMAND.valueOf(command.toUpperCase());
    }

    public void setInstallationRoot(String file) {
        File f = new File(file);
        try {
            installationRoot = new File(f.getCanonicalPath());
        } catch (IOException e) {
            //ignore, will not happen
        }

        init0();

    }

    private void init0() {
        tmpDirectory = new File(installationRoot, "office-tmp");
        if(!tmpDirectory.exists()) {
            tmpDirectory.mkdir();
        }
        File confFolder = new File(installationRoot, "conf");
        if(confFolder.exists() && confFolder.isDirectory()) {
            this.officeProperties = new File(confFolder, "office.properties");
            loadProperties();
        }
    }

    @Override
    protected void doExecute() throws BuildException {
        loadProperties();
        switch (command) {
            case START:
                start();
                break;
            case STOP:
                stop();
                break;
            case RESTART:
                stop();
                start();
                break;
            case STATUS:
                status();
                break;
            default:
                start();
        }
    }

    private void start() {
        logger.info("准备启动Office服务.");
        try {
            if(OfficeServiceHelper.getInstance().isRunning(servicePort)) {
                logger.info("Office服务已经启动，不用重复启动.");
                return;
            }
            OfficeServiceHelper.getInstance().start(officeInstallationFolder, tmpDirectory, servicePort);
            logger.info("成功启动Office服务.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("启动Office服务失败:", e);
        }

    }

    private void stop() {
        logger.info("准备停止Office服务.");
        try {
            OfficeServiceHelper.getInstance().stop(officeInstallationFolder, tmpDirectory, servicePort);
            logger.info("成功停止Office服务.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("停止Office服务失败:", e);
        }

    }

    private void status() {
        logger.info("正在检查Office状态.");
        try {
            if(OfficeServiceHelper.getInstance().isRunning(servicePort)) {
                logger.info("Office服务正常.");
            } else {
                logger.info("Office服务不存在.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("检查Office服务出现异常:", e);
        }

    }

    private void loadProperties(){
        PropertiesLoader loader;
        try {
            loader = new PropertiesLoader("file:" + officeProperties.getCanonicalPath());

            String officeInstallationPath = loader.getProperty("office.installation.path", "");
            if(StringUtils.isEmpty(officeInstallationPath)) {
                officeInstallationFolder = OfficeUtils.getDefaultOfficeHome();
            } else {
                officeInstallationFolder = new File(officeInstallationPath);
            }

            servicePort = loader.getInteger("office.port", 2002);

            logger.info("Office安装路径:" + officeInstallationPath);
            logger.info("Office服务端口:" + servicePort);
        } catch (IOException e) {
            throw new BuildException("读取配置文件出错:", e);
        }
    }


}
