package com.riversoft.dtask;

import java.io.PrintStream;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.DateUtils;

import com.riversoft.dtask.tasks.BaseRiverTask;

public class BuildLogger extends DefaultLogger {

    private boolean hasStartedTarget = false;
    private long timerInstallStarted = 0;

    private static final String LOGFILE_TEMP_LOCATION = "tmp";
    private static final String LOGFILE_NAME = "riversoft.log";

    /**
     * Captures most, if not all, messages sent by ANT to the console.
     */
    public void messageLogged(BuildEvent event) {
        // Print a newline for new targets
        if(hasStartedTarget && event.getPriority() <= msgOutputLevel) {
            println(out, "");
            hasStartedTarget = false;
        }

        // Print normal messages
        if(event.getPriority() == BaseRiverTask.DRUTT_CONT_LOG_LVL) {
            print(out, event.getMessage());
        } else if(event.getPriority() <= msgOutputLevel) {
            if(event.getPriority() == Project.MSG_ERR) {
                println(out, "***** ");
                print(out, "***** " +event.getMessage());
                println(out, "*****");
            } else {
                println(out, event.getMessage());
            }
        }
    }

    /**
     * Called by ANT when a new target has been initiated.
     */
    public void targetStarted(BuildEvent event) {
        // This method has been left empty on purpose, so the logger won't
        // print out all the target names.
        hasStartedTarget = true;
    }

    /**
     * Called by ANT when a build completes.
     */
    public void buildFinished(BuildEvent event) {
        long elapsedTime = System.currentTimeMillis() - timerInstallStarted;

        // Do the normal "build finished" routine.
        Throwable error = event.getException();
        if(error == null) {
            println(out, "执行完毕.");
        } else {
            println(err, "程序终止:" + error.getMessage());
        }
        println(out, "执行耗费时间: " +DateUtils.formatElapsedTime(elapsedTime));

        //Move logfile to installation directory
        String filepath = event.getProject().getProperty("installation.installer.log.dir");
        if(filepath == null) {
            println(err, "Property 'installation.installer.log.dir' not set, storing logfile in /tmp/install-YYYYmm-HHMM.log");
            filepath = "/tmp";
        }

        FileHandler.getInstance().moveFile(filepath);
    }

    /**
     * Called by ANT when it starts. Used similarly to a constructor.
     */
    public void buildStarted(BuildEvent event) {
        FileHandler.getInstance(LOGFILE_TEMP_LOCATION, LOGFILE_NAME);
        timerInstallStarted = System.currentTimeMillis();
        super.buildStarted(event);
    }


    /**
     * Makes a normal print() to both a given stream and the log file.
     * @param stream Stream to write to, usually 'out' or 'err'
     * @param message Message that will be printed.
     */
    private void print(PrintStream stream, String message) {
        stream.print(message);
        FileHandler.getInstance().write(message);
    }

    /**
     * Makes a print() with a newline to both a given stream and the log file.
     * @param stream Stream to write to, usually 'out' or 'err'
     * @param message Message that will be printed.
     */
    private void println(PrintStream stream, String message) {
        message = message.concat("\n");
        print(stream, message);
    }
}
