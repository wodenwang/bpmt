package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Tstamp;

import java.io.File;
import java.util.LinkedList;
import java.util.Map.Entry;

public class ExitInstall extends BaseRiverTask {

    protected boolean purgeTemporary = false;

    public void setPurgeTemporary(boolean purge) {
        purgeTemporary = purge;
    }

    @Override
    protected void doExecute() throws BuildException {
        File configOutFile = InitInstallTask.getOutFile(this.getProject());
        backupConfigOutFile(configOutFile);

        if (purgeTemporary) {
            // Remove any entry that starts with "temp"
            LinkedList<String> keys = new LinkedList<String>();
            for (Entry<Object, Object> entry : prop.entrySet()) {
                if (((String) entry.getKey()).startsWith("temp")) {
                    keys.add((String) entry.getKey());
                }
            }
            for (String key : keys) {
                prop.remove(key);
            }
        }

        TaskUtil.saveProperties(configOutFile, prop);

        if (!InitInstallTask.getGlobals(getProject()).delete()) {
            getProject().log("Could not remove file: " + InitInstallTask.getGlobals(getProject()).getAbsolutePath(),
                    DRUTT_LOG_LVL);
        }
    }

    private void backupConfigOutFile(File configOutFile) {
        if (configOutFile.exists()) {
            // backup
            Task ts = new Tstamp();
            ts.setProject(getProject());
            ts.execute();

            String ext = String.format("%1$s-%2$s", getProject().getProperty("DSTAMP"),
                    getProject().getProperty("TSTAMP"));

            File d = configOutFile.getParentFile();
            File bak = configOutFile.getAbsoluteFile();

            File toF = new File(d, bak.getName() + "." + ext);
            int num = 1;
            while (toF.exists()) {
                toF = new File(d, bak.getName() + "." + ext + "." + num++);
            }

            if (!bak.renameTo(toF)) {
                throw new BuildException("Could not create backup of " + bak.getAbsolutePath());
            }
        }
    }
}
