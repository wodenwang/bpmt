package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;

import java.util.ArrayList;
import java.util.List;

public class MenuSelectTask extends BaseRiverTask {

    private static final String CHOICE_PROMPT = "请选择 :";

    private static final String DEFAULT_SEPARATOR = ",";

    private String choiceValue;
    private String addproperty;
    private String prompt = CHOICE_PROMPT;
    private String choices = null;
    private String separator = DEFAULT_SEPARATOR;

    public String getChoice() {
        return choiceValue;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setSeparator(String sep) {
        this.separator = sep;
    }

    private List<String> choiceList = new ArrayList<String>();
    private String choiceListStr;

    public void setChoices(String choices) {
        this.choices = choices;
    }

    private void generateChoices() {
        String mItemFormat = "[%1$s] : %2$s";
        StringBuffer sb = new StringBuffer(prompt);
        sb.append(NEW_LINE);

        int cIx = 1;
        for (String t : choices.split(separator)) {
            t = t.trim();
            choiceList.add(t);
            sb.append(String.format(mItemFormat, cIx, t));
            sb.append(NEW_LINE);
            cIx++;
        }

        choiceListStr = sb.toString();
    }

    public void setAddproperty(String s) {
        addproperty = s;
    }

    private void printChoices(String choice) {
        InputHandler inputHandler = getProject().getInputHandler();

        StringBuffer sb = new StringBuffer(choiceListStr);
        if (choice != null && !choice.equals(EMPTY)) {
            sb.append("Default : [").append(choice).append("] ");
        }

        InputRequest ir = new InputRequest(sb.toString());

        boolean choiceMade = false;
        String lastMatch = null;
        String selection = null;

        while (!choiceMade) {
            selection = null;
            inputHandler.handleInput(ir);
            String input = ir.getInput();
            if (input.equals(EMPTY) && choice != null) {
                input = choice;
            }

            input = input.toLowerCase();

            try {
                int choiceIx = Integer.parseInt(input) - 1;
                selection = choiceList.get(choiceIx);
            } catch (Exception e) {
                //
            }

            if (selection == null) {
                int matchCount = 0;
                for (String s : choiceList) {
                    if (s.equalsIgnoreCase(input)) {
                        selection = s;
                        matchCount = 0;
                        break;
                    } else if (s.toLowerCase().startsWith(input)) {
                        // Support writing longer answers than the required uniqPrefix
                        matchCount++;
                        lastMatch = s;
                    }
                }
                if (matchCount == 1) {
                    selection = lastMatch;
                }
            }
            choiceMade = selection != null;
        }
        choiceValue = selection;
    }

    public void doExecute() throws BuildException {
        if (addproperty != null) {
            choiceValue = getProperty(addproperty);
        }

        generateChoices();

        if (choiceValue != null && isUnattended && choiceListStr.indexOf(choiceValue) >= 0) {
            // Do nothing
        } else {
            printChoices(choiceValue);
        }

        if (addproperty != null) {
            setProjectProperty(addproperty, choiceValue);
            if (saving) {
                setProperty(addproperty, choiceValue);
                if (saving) {
                    saveProperties();
                }
            }
        }
    }
}
