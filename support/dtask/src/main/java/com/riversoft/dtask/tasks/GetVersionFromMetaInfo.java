package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;

import java.util.Map;

/**
 * Created by borball on 14-3-9.
 */
public class GetVersionFromMetaInfo extends RiverSupportTask {

    private String version = "";
    private String artifact = "";

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    protected void support() throws BuildException {
        checkAttributes();

        Map<String, String> caVersions = getCANameAndVersionsFromJar();

        String value = caVersions.get(artifact);

        if (value != null) {
            setProperty(version, value);
            setProjectProperty(version, value);
        } else {
            value = getProperty(version);
            if (value != null) {
                setProperty(version, value); // Make sure it is written to output configuration
                setProjectProperty(version, value);
            }
        }
    }

    private void checkAttributes() {
        if (version == null || version.length() == 0) {
            throw new BuildException("Missing version attribute on " + getTaskName());
        }
    }

}
