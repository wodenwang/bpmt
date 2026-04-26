package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;

public class InitInstallTask extends Task {

    public final static String GLOBALS_FILE_NAME = "globals.tmp";

    public static File getOutFile(Project p) {
        String outF = p.getProperty(BaseRiverTask.COMMAND_LINE_OUTFILE);
        File outFile = null;

        File installerConfigDir = getInstallerConfigDir(p);

        if (outF == null || outF.equals(BaseRiverTask.EMPTY)) {
            outFile = new File(installerConfigDir, "install.conf");
        } else {
            outFile = new File(outF);
        }

        return outFile;
    }

    public static File getInFile(Project p) {
        String inF = p.getProperty(BaseRiverTask.COMMAND_LINE_INFILE);
        File configInFile;

        if (inF != null && !inF.equals(BaseRiverTask.EMPTY)) {
            configInFile = new File(inF);
        } else {
            configInFile = null;
        }

        return configInFile;
    }

    public static File getInstallerConfigDir(Project p) {
        String confDir = p.getProperty(BaseRiverTask.INSTALLATION_INSTALLER_CONFIG_DIR);
        File bDir = null;

        if (confDir == null) {
            confDir = "../tmp/river-installer";
        }

        bDir = new File(confDir);
        if (!bDir.exists()) {
            bDir.mkdirs();
        }

        return bDir;
    }

    public static File getGlobals(Project p) {
        return new File(getInstallerConfigDir(p), GLOBALS_FILE_NAME);
    }

    @Override
    public void execute() throws BuildException {
        getProject().log("Initializing install", BaseRiverTask.DRUTT_LOG_LVL);

        File globals = new File(getInstallerConfigDir(getProject()), GLOBALS_FILE_NAME);

        if (globals.exists() && !globals.delete())
            throw new BuildException("Could not delete " + globals.getAbsoluteFile());

        try {
            globals.createNewFile();
        } catch (IOException e) {
            throw new BuildException(e);
        }

        getProject().log("Initializing completed", BaseRiverTask.DRUTT_LOG_LVL);
    }
}
