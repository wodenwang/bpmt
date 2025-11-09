package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;

public class DPropertyTask extends BaseRiverTask {

    private String value;
    private String addProperty;
    private boolean failOnUndef = false;

    public DPropertyTask() {
        globalProperty = true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAddproperty(String addProperty) {
        this.addProperty = addProperty;
    }

    public void setFailOnUndef(boolean value) {
        failOnUndef = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.drutt.ant.tasks.BaseDruttTask#doExecute()
     */
    @Override
    protected void doExecute() throws BuildException {
        checkAttributes();

        if (value != null) {
            setProperty(addProperty, value);
            setProjectProperty(addProperty, value);
            if (saving) {
                saveProperties();
            }
        } else {
            value = getProperty(addProperty);
            if (value != null) {
                setProperty(addProperty, value); // Make sure it is written to output configuration
                setProjectProperty(addProperty, value);
                if (saving) {
                    saveProperties();
                }
            } else if (failOnUndef) {
                throw new BuildException("Property " + addProperty + " not defined!");
            }
        }
    }

    private void checkAttributes() {
        if (addProperty == null || addProperty.length() == 0) {
            throw new BuildException("Missing addProperty attribute on " + getTaskName());
        }
    }
}
