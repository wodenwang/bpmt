package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;

public class ConfirmTask extends Task {

    private final static String CONFIRM_PROMPT = "Confirm : %1$s [y]/n";

    private String value;
    private String addProperty;

    public void setValue(String v) {
        value = v;
    }

    public void setAddproperty(String addProperty) {
        this.addProperty = addProperty;
    }

    private boolean result = false;

    public boolean getResult() {
        return result;
    }

    @Override
    public void execute() throws BuildException {
        InputHandler inputHandler = this.getProject().getInputHandler();
        InputRequest cr = new InputRequest(String.format(CONFIRM_PROMPT, value));

        boolean inputAccepted = false;
        while (!inputAccepted) {
            inputHandler.handleInput(cr);

            String inp = cr.getInput();
            if (BaseRiverTask.EMPTY.equals(inp) || inp.equalsIgnoreCase("y")) {
                result = true;
                inputAccepted = true;
            } else if (inp.equalsIgnoreCase("n")) {
                inputAccepted = true;
            }
        }

        if (addProperty != null) {
            getProject().setProperty(addProperty, Boolean.toString(result));
        }
    }

}
