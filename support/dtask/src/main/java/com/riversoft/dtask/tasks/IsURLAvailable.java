package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.LogLevel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Check if a URL is available by making a URL connection and checking the response code.
 * <p/>
 * Configuration
 * <ul>
 * <li>url (required) - URL to connect to</li>
 * <li>timeout (optional) - timeout for both read and connect timeout (in seconds) (default: 3s)</li>
 * </ul>
 */
public class IsURLAvailable extends Task implements Condition {

    private String url;
    private int timeout = 3;

    private boolean result;

    /**
     * The URL to connect to.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Timeout for both read and connect in seconds.
     *
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean getResult() {
        return result;
    }

    public void execute() throws BuildException {
        eval();
    }

    public boolean eval() throws BuildException {
        if (url == null) {
            throw new BuildException("url attribute is required.");
        }

        result = false;
        try {
            String msg = String.format("Check if URL: %s is available. Timeout: %s ms\n", url, timeout);
            log(msg, LogLevel.DEBUG.getLevel());
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(timeout * 1000);
            conn.setReadTimeout(timeout * 1000);
            result = conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (MalformedURLException e) {
            String msg = String.format("Malformed URL: %s. %s\n", url, e.getMessage());
            log(msg, LogLevel.ERR.getLevel());
        } catch (SocketTimeoutException e) {
            String msg = String.format("Timeout reaching URL: %s. %s\n", url, e.getMessage());
            log(msg, LogLevel.DEBUG.getLevel());
        } catch (IOException e) {
            String msg = String.format("Could not connect to URL: %s. %s\n", url, e.getMessage());
            log(msg, LogLevel.DEBUG.getLevel());
        }
        String msg = String.format("URL: %s returned: %s\n", url, result);
        log(msg, LogLevel.VERBOSE.getLevel());
        return result;
    }
}