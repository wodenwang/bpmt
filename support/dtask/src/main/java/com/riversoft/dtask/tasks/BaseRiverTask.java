package com.riversoft.dtask.tasks;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * The BaseRiverTask
 * <p/>
 * Configuration files are named after the Task Classname e.g. CreateUsersTask
 * and the owning target in the Ant build script. So if called from a target
 * called "CreateUsers" the config would be called CreateUsersTask-CreateUsers.xml
 * The property files are put under the ANT script basedir in a subfolder named
 * <p/>
 * <pre>
 * properties
 * </pre>
 * <p/>
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
 * <properties>
 *   <entry key="ManagerUN">manager</entry>
 *   <entry key="ManagerPW">managerpass</entry>
 *   <entry key="ProfileUN">device</entry>
 *   <entry key="ProfilePW">devicepass</entry>
 * </properties>
 * </pre>
 * <p/>
 * Properties are loaded in <code>init()</code> and are thus available in subclasses
 * during <code>execute()</code> by accessing <code>prop.getProperty()</code>
 * <p/>
 * If the task should save its properties, make a call to saveProperties() before exiting
 * from <code>execute</code>
 * <p/>
 * When writing DEBUG strings using the log() functionality, set project.debug=true in the ANT project
 * <p/>
 * External Project Properties used in the Task:
 * <p/>
 * - installation.installer.dir
 * - installation.installer.config.dir
 * - installation.installer.debug [true/false]
 * - installation.installer.unattended [true/false]
 * <p/>
 * Properties that can be set in installation configuration file (not install.properties
 * but rather the configuration file storing installation properties)
 * <p/>
 * - installation.installer.singleHostInstall [true/false]
 * - installation.installer.singleSchemaInstall [true/false]
 */
public abstract class BaseRiverTask extends Task {

    /**
     * Empty string
     */
    protected static final String EMPTY = "";
    /**
     * Default answers to yes or no questions
     */
    protected static final String[] INPUT_POSITIVE_DEFAULT = {"y", "yes", EMPTY};
    protected static final String[] INPUT_POSITIVE_NOTDEFAULT = {"y", "yes"};
    protected static final String[] INPUT_NEGATIVE_DEFAULT = {"n", "no", EMPTY};
    protected static final String[] INPUT_NEGATIVE_NOTDEFAULT = {"n", "no"};
    /**
     * Log level used by all Drutt specific messages.
     * Is a negative number so it always gets captured,
     * even if BuildLogger isn't used.
     */
    public static int DRUTT_LOG_LVL = -30;
    /**
     * Continuous log level, used for printing messages without newlines
     */
    public static int DRUTT_CONT_LOG_LVL = -31;
    /**
     * Define NEW_LINE constant
     */
    public static String NEW_LINE = System.getProperty("line.separator");
    protected static final String PROMPT_INVALID_INPUT = NEW_LINE + "Invalid input";
    /**
     * Indicate if Debug prints should be output
     */
    public static String INSTALLATION_INSTALLER_DEBUG = "installation.installer.debug";
    /**
     * Installer Dir. Install information is put here
     */
    public static String INSTALLATION_INSTALLER_DIR = "installation.installer.dir";
    /**
     * Installer config dir, default location for configuration files to write to
     */
    public static String INSTALLATION_INSTALLER_CONFIG_DIR = "installation.installer.config.dir";
    /**
     * Can be set by installing applications to indicate all ConnectStrings will be the same
     */
    public static String INSTALLATION_SINGLE_SCHEMA_INSTALL = "installation.installer.singleSchemaInstall";
    public static String INSTALLATION_ISUNATTENDED = "installation.installer.unattended";
    public static String INSTALLATION_ISVERBOSE = "installation.installer.verbose";
    public static String COMMAND_LINE_OUTFILE = "cfile.out";
    public static String COMMAND_LINE_INFILE = "cfile.in";
    /**
     * The properties output file
     */
    protected File globals;
    /**
     * The properties input file *
     */
    protected File configInFile;
    /**
     * The properties
     */
    protected Properties prop;
    /**
     * Properties defined in supplied configuration file. *
     */
    protected Properties confProp;
    protected boolean isVerbose = false;
    protected boolean saving = false;
    protected boolean searchSimilarConfigs = false;
    protected boolean isUnattended = false;
    protected boolean isSingleSchemaInstall = false;
    protected boolean globalProperty = false;
    private Logger logger = LoggerFactory.getLogger("BaseRiverTask");
    private String unattendedOverride = null;

    public void setSave(String save) {
        saving = Boolean.parseBoolean(save);
    }

    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
    }

    public void setUnattended(String unattended) {
        unattendedOverride = unattended;
    }

    public void setSearchSimilarConfigs(String doSearch) {
        searchSimilarConfigs = Boolean.parseBoolean(doSearch);
    }

    public void overridePropertyFileName(String filename) {
        File f = new File(filename);
        if (!f.isAbsolute()) {
            f = new File(InitInstallTask.getInstallerConfigDir(getProject()), filename);
        }
        globals = f;
    }

    /**
     * Generates a prefix based on task, project and target
     *
     * @return a prefix
     */
    @SuppressWarnings("unused")
    private String getPropertyPrefix() {
        String tt = getTaskType();
        String projName = getProject().getName();

        return (globalProperty ? EMPTY : String.format("%1$s.%2$s.", projName, tt));
    }

    protected void setProperty(String name, String value) {
        prop.setProperty(name, value);
    }

    /**
     * @param name           the name of the property to search for (addPropery name)
     * @param pattern        the pattern to look for, such as "hostname" or "dbUrl". Does
     *                       not support regular expressions, wild cards or anything similar.
     * @param recordMatching true if the Task Contains several properties. And the
     *                       similar property should be matching only the one with the same name
     * @return any selected property if any found, null otherwise
     */
    protected String getSimilarProperty(String name, String pattern, boolean recordMatching) {
        String value = null;

        List<String> sim = new ArrayList<String>();

        Enumeration<Object> e = prop.keys();
        while (e.hasMoreElements()) {
            String n = (String) e.nextElement();
            if (n.matches(pattern)) {
                String propVal = prop.getProperty(n);
                if (sim.contains(propVal)) {
                    continue;
                }

                /*
                 * if ((recordMatching && n.endsWith(name)) || !recordMatching ) {
                 * sim.add(propVal);
                 * }
                 */
            }
        }

        StringBuffer sb = new StringBuffer();
        for (String s : sim) {
            if (sb.length() > 0) {
                sb.append("-@@@-");
            }
            sb.append(s);
        }

        if (sb.length() == 0) {
            return null;
        } else if (sim.size() == 1) {
            if (getClass().getName().equals(CheckDBConnectionTask.class.getName()) && isSingleSchemaInstall) {
                return sim.get(0);
            }
        }

        sb.append("-@@@- New Value");

        MenuSelectTask st = new MenuSelectTask();
        st.setProject(getProject());
        st.setOwningTarget(getOwningTarget());
        st.setSeparator("-@@@-");
        // Don't save this result
        st.setSave("false");
        st.setChoices(sb.toString());
        st.setPrompt("Previously entered values are available, choose one or select New Value:");
        st.doExecute();

        String choice = st.getChoice();
        if (!choice.equals("New Value")) {
            value = choice;
        }

        return value;
    }

    protected String getProperty(String name) {
        return prop.getProperty(name) != null ? prop.getProperty(name) : confProp.getProperty(name);
    }

    protected String getProperty(String name, String defaultValue) {
        String ret = getProperty(name);
        return (ret != null ? ret : defaultValue);
    }

    protected String getGlobalProperty(String name) {
        return prop.getProperty(name);
    }

    /**
     * Set and override a property in global (unprefixed) context
     *
     * @param name  parameter name
     * @param value parameter value
     */
    protected void setGlobalProperty(String name, String value) {
        prop.setProperty(name, value);
    }

    /**
     * Overrides <code>execute()</code> from {@link Task} and calls the doExcute() function
     * in the implementing class.
     * <p/>
     * Also loads properties from XML propertyfile with name based on Task class name and the
     * ANT target used when calling the task. After completion, the {@link Properties} <code>prop</code> can be accessed
     * to retrieve loaded properties.
     * <p/>
     * Not done in <code>init()</code> because <code>init()</code> is called twice for each occurrence
     * of the task in the script.
     */
    public final void execute() {

        isVerbose = Boolean.parseBoolean(getProject().getProperty(INSTALLATION_ISVERBOSE));

        isUnattended = unattendedOverride == null ? Boolean.parseBoolean(getProject().getProperty(
                INSTALLATION_ISUNATTENDED)) : Boolean.parseBoolean(unattendedOverride);

        initProperties();

        String s = prop.getProperty(INSTALLATION_SINGLE_SCHEMA_INSTALL);
        isSingleSchemaInstall = s != null ? Boolean.parseBoolean(s) : isSingleSchemaInstall;

        doExecute();
    }

    /**
     * The actual task code is done in doExecute(), retrieve preset properties from <code>prop</code> and make a call to
     * saveProperties to save properties set by prop.setProperty()
     *
     * @throws BuildException when the build is required to abort
     */
    protected abstract void doExecute() throws BuildException;

    /**
     * Sets (and overrides) a previously set project property, to set a property which cannot be
     * overwritten, use setUserProperty
     *
     * @param propertyName
     * @param value
     */
    protected void setProjectProperty(String propertyName, String value) {
        getProject().setProperty(propertyName, value);
    }

    private void initProperties() {
        if (prop == null) {
            prop = new Properties();
            confProp = new Properties();

            if (globals == null) {
                globals = InitInstallTask.getGlobals(this.getProject());
            }

            if (configInFile == null) {
                configInFile = InitInstallTask.getInFile(this.getProject());
            }

            // Load input
            TaskUtil.loadProperties(globals, prop); // Load the variables we've already set
            TaskUtil.loadProperties(configInFile, confProp);
        }
    }

    protected void saveProperties() {
        TaskUtil.saveProperties(InitInstallTask.getGlobals(this.getProject()), prop);
    }

    protected boolean askYesOrNo(String prompt, String[] legitPositive, String[] legitNegative) {
        InputHandler ih = this.getProject().getInputHandler();
        InputRequest ir = new InputRequest(prompt);

        boolean answerGiven = false;
        boolean answer = false;
        while (!answerGiven) {
            ih.handleInput(ir);
            String inp = ir.getInput();

            if (stringHasEqual(inp, legitPositive)) {
                answer = true;
                answerGiven = true;
            } else if (stringHasEqual(inp, legitNegative)) {
                answer = false;
                answerGiven = true;
            } else {
                log(PROMPT_INVALID_INPUT, DRUTT_LOG_LVL);
            }
        }

        return answer;
    }

    protected boolean stringHasEqual(String a, String[] set) {
        for (String s : set) {
            if (a.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    protected String getPlatformVersionFromJar() {
        return getPlatformVersionFromJar("util");
    }

    protected String getPlatformVersionFromJar(String artifact) {
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

                        String art = mainAttribs.getValue("X-River-Artifact");

                        if (artifact == null) {
                            if (!StringUtils.isEmpty(art)) {
                                version = mainAttribs.getValue("Implementation-Version");
                                if (!StringUtils.isEmpty(version)) {
                                    is.close();
                                    break;
                                }
                            }
                        } else {
                            if (artifact.equalsIgnoreCase(art)) {
                                version = mainAttribs.getValue("Implementation-Version");
                                if (!StringUtils.isEmpty(version)) {
                                    is.close();
                                    break;
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    logger.error("获取系统版本信息出错:", e);
                }
            }
        } catch (IOException e) {
            logger.error("获取系统版本信息出错:", e);
        }

        return version;

    }

    protected Map<String, String> getCANameAndVersionsFromJar() {
        Map<String, String> caVersions = new HashMap<>();
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

                        String caName = mainAttribs.getValue("X-River-CA");
                        if (!StringUtils.isEmpty(caName)) {
                            String version = mainAttribs.getValue("Implementation-Version");
                            if (!StringUtils.isEmpty(version)) {
                                caVersions.put(caName.toLowerCase(), version);
                                is.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("获取CA版本信息出错:", e);
                }
            }
        } catch (IOException e) {
            logger.error("获取CA版本信息出错:", e);
        }

        return caVersions;

    }
}
