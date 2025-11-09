package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tries to make a connection to an Database with provided connect data.
 * Stores a boolean (string) result in a property.
 */
public class CheckDBConnectionTask extends BaseRiverTask {

    static final String PROMPT_CONNECTING = NEW_LINE + "尝试连接: %s";
    static final String PROMPT_DB_FAILED = NEW_LINE + "数据库连接失败:";
    static final String PROMPT_DB_SUCCESSFUL = NEW_LINE + "数据库连接成功.";

    private String addProperty = "false";
    private String url = null;
    private String driver = null;
    private String username = "";
    private String password = "";

    public void setResultFlag(String addProperty) {
        this.addProperty = addProperty;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Tries to load the driver and will throw a BuildException if that
     * fails
     */
    private void checkDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e1) {
            // Fail
            throw new BuildException("没有找到数据库驱动:" + driver);
        }
    }

    private void checkAttributes() {
        if (url == null) {
            throw new BuildException(getOwningTarget() + " : " + getTaskName()
                    + " : 缺少属性: 'url'. 请设置.");
        }

        if (driver == null) {
            throw new BuildException(getOwningTarget() + " : " + getTaskName()
                    + " : 缺少属性: 'driver'. 请设置.");
        }

        if (username == null) {
            throw new BuildException(getOwningTarget() + " : " + getTaskName()
                    + " : 缺少属性: 'username'. 请设置.");
        }

        if (password == null) {
            throw new BuildException(getOwningTarget() + " : " + getTaskName()
                    + " : 缺少属性: 'password'. 请设置.");
        }

        if (addProperty == null) {
            throw new BuildException(getOwningTarget() + " : " + getTaskName()
                    + " : 缺少属性: 'addProperty'. 请设置.");
        }
    }

    public void doExecute() throws BuildException {
        checkAttributes();
        checkDriver();
        if (testConnection(url)) {
            setProjectProperty(addProperty, "true");
        }
        if (saving) {
            saveProperties();
        }
    }

    private boolean testConnection(String url) {
        log(String.format(PROMPT_CONNECTING, url), DRUTT_LOG_LVL);
        Connection connection = null;
        boolean isWorking = false;

        try {
            // Create a connection to the database
            connection = DriverManager.getConnection(url, username, password);
            if (connection.isReadOnly()) {
                throw new BuildException("数据库连接时只读的.");
            }
            connection.close();
            log(PROMPT_DB_SUCCESSFUL, DRUTT_LOG_LVL);
            isWorking = true;

        } catch (SQLException e) {

            // Could not connect to the database
            log(PROMPT_DB_FAILED + e.getMessage(), DRUTT_LOG_LVL);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Do nothing
                }
            }
        }

        return isWorking;
    }
}
