/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Wodensoft System, all rights reserved.
 */
package com.riversoft.dtask.office;

import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.office.*;
import org.artofsolving.jodconverter.process.*;
import org.artofsolving.jodconverter.util.PlatformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.artofsolving.jodconverter.process.ProcessManager.PID_NOT_FOUND;
import static org.artofsolving.jodconverter.process.ProcessManager.PID_UNKNOWN;

/**
 * @author Woden
 *
 */
public class OfficeServiceHelper {

    private static Logger logger = LoggerFactory.getLogger("OfficeServiceHelper");

    private static OfficeServiceHelper instance = null;

    private OfficeServiceHelper(){
    }

    public synchronized static OfficeServiceHelper getInstance(){
        if(instance == null) {
            instance = new OfficeServiceHelper();
        }
        return instance;
    }

    public void start(File program, File tmpDirectory, int port) throws OfficeException {
        UnoUrl unoUrl = UnoUrl.socket(port);
        OfficeProcess officeProcess = new OfficeProcess(program, unoUrl, null, null, tmpDirectory, findBestProcessManager());
        try {
            officeProcess.start();

            sleep(2000l);

            Integer exitCode = officeProcess.getExitCode();
            if (exitCode != null && exitCode.equals(Integer.valueOf(81))) {
                officeProcess.start(true);
                sleep(2000l);
            }
        } catch (Exception e) {
            throw new OfficeException("启动Office服务进程失败:", e);
        }

    }

    public void stop(File program, File tmpDirectory, int port) throws OfficeException {
        UnoUrl unoUrl = UnoUrl.socket(port);
        OfficeProcess officeProcess = new OfficeProcess(program, unoUrl, null, null, FileUtils.getTempDirectory(), findBestProcessManager());
        try {
            int exitCode = officeProcess.forciblyTerminate(500l, 10000l);
            logger.info("停止Office服务进程, 返回码:" + exitCode);
        } catch (Exception exception) {
            throw new OfficeException("停止Office服务进程失败，可能需要手工介入:", exception);
        }
    }


    public boolean isRunning(int port){
        UnoUrl unoUrl = UnoUrl.socket(port);
        ProcessQuery processQuery = new ProcessQuery("soffice", unoUrl.getAcceptString());
        ProcessManager processManager = findBestProcessManager();

        long foundPid;
        try {
            foundPid = processManager.findPid(processQuery);
            if (foundPid == PID_NOT_FOUND || foundPid == PID_UNKNOWN) {
                logger.info("未找到Office服务进程:" + foundPid);
                return false;
            } else {
                logger.info("找到Office服务进程:" + foundPid);
                return true;
            }
        } catch (Exception e) {
            logger.error("未找到Office服务进程:", e);
            throw new OfficeException("未找到Office服务进程:", e);
        }
    }

    private ProcessManager findBestProcessManager() {
        if (PlatformUtils.isLinux()) {
            return new LinuxProcessManager();
        } else if (isSigarAvailable()) {
            return new SigarProcessManager();
        } else {
            // NOTE: UnixProcessManager can't be trusted to work on Solaris
            // because of the 80-char limit on ps output there
            return new PureJavaProcessManager();
        }
    }

    private boolean isSigarAvailable() {
        try {
            Class.forName("org.hyperic.sigar.Sigar", false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
