/**
 *
 */
package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;

public class InputSelectTask extends BaseRiverTask {

    private static final String DEFAULT_PROMPT = "请输入: ";

    private String value = null;
    private String propertyName;

    private boolean confirm = false;
    private boolean isNumeric = false;
    private boolean isSet = false;
    private boolean preferDefault = false;

    /**
     * Format to validate against, only allow properly formatted input
     */
    private String format;

    private String prompt;

    public void setFormat(String format) {
        this.format = format;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setDefaultValue(String defaultValue) {
        value = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public void setGlobal(String global) {
        globalProperty = Boolean.parseBoolean(global);
    }

    public void setAddproperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setConfirm(String confirm) {
        this.confirm = Boolean.parseBoolean(confirm);
    }

    public void setNumeric(String numeric) {
        this.isNumeric = Boolean.parseBoolean(numeric);
    }

    public void setPreferDefault(boolean preferDefault) {
        this.preferDefault = preferDefault;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.drutt.ant.tasks.BaseDruttTask#doExecute()
     */
    @Override
    protected void doExecute() throws BuildException {
        if (propertyName != null) {
            if (!preferDefault) {
                value = getProperty(propertyName, value);
                if (getGlobalProperty(propertyName) != null) {
                    isSet = true;
                }
            }

            if (value != null && (isUnattended || isSet)) {
                setProjectProperty(propertyName, value);
                setProperty(propertyName, value);

                if (saving) {
                    saveProperties();
                }
                return;
            }
        }

        InputHandler inputHandler = this.getProject().getInputHandler();
        String tmpInput = null;

        Object[] args = new String[]{(prompt != null ? prompt : DEFAULT_PROMPT),
                (value != null ? (NEW_LINE + "[" + value + "]") : EMPTY)};

        InputRequest ir = new InputRequest(String.format("%1$s%2$s", args));
        boolean confirmed = !confirm;

        do {
            String oldVal = EMPTY;
            boolean inputAccepted = false;
            while (!inputAccepted) {
                inputHandler.handleInput(ir);
                tmpInput = ir.getInput();

                if (EMPTY.equals(tmpInput)) {
                    tmpInput = value != null ? value : EMPTY;
                }

                if (isNumeric) {
                    try {
                        Integer.parseInt(tmpInput);
                    } catch (NumberFormatException nfe) {
                        getProject().log("错误的输入, 请输入数字：", DRUTT_LOG_LVL);
                        continue;
                    }
                } else if (format != null && !tmpInput.matches(format)) {
                    getProject().log("Bad input, your input dose not match： [" + format + "].", DRUTT_LOG_LVL);
                    continue;
                }

                oldVal = value;
                value = tmpInput;
                inputAccepted = true;
            }

            if (confirm && !isUnattended) {
                ConfirmTask ct = new ConfirmTask();
                ct.setProject(getProject());
                ct.setOwningTarget(getOwningTarget());
                ct.setValue(value);
                ct.execute();
                confirmed = ct.getResult();

                if (!confirmed) {
                    value = EMPTY.equals(oldVal) ? value : oldVal;
                }
            }
        } while (!confirmed || value.equals(EMPTY));

        if (propertyName != null) {
            setProjectProperty(propertyName, value);
            setProperty(propertyName, value);
        }

        if (saving) {
            saveProperties();
        }
    }
}
