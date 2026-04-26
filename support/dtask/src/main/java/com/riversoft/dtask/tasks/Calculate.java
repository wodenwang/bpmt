package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;

public class Calculate extends BaseRiverTask {

    /**
     * Calculates two values with each other.
     * <p/>
     * Example: [valueOne][operation][valueTwo]=[Return value]
     * 4 + 2 = 6
     * 8 / 4 = 2
     *
     * @param valueOne the first value to use in the calculation
     * @param valueTwo the second value to use in the calculation
     * @param operation the operation to use on the two values, e.g. +, -, * or /.
     * @return result the result of the operation
     */
    private String valueOne = null;
    private String valueTwo = null;
    private String operation = null;
    private String propertyName = null;

    public void setValueOne(String valueOne) {
        this.valueOne = valueOne;
    }

    public void setValueTwo(String valueTwo) {
        this.valueTwo = valueTwo;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setAddProperty(String p) {
        propertyName = p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.drutt.ant.tasks.BaseDruttTask#doExecute()
     */
    @Override
    protected void doExecute() throws BuildException {
        if (valueOne != null && valueTwo != null && operation != null && propertyName != null) {
            Float v1 = Float.parseFloat(valueOne);
            Float v2 = Float.parseFloat(valueTwo);
            Float tmpResult = null;
            String result = null;

            if (operation.equals("+")) {
                tmpResult = v1 + v2;
                result = Integer.toString(tmpResult.intValue());
            } else if (operation.equals("-")) {
                tmpResult = v1 - v2;
                result = Integer.toString(tmpResult.intValue());
            } else if (operation.equals("*")) {
                tmpResult = v1 * v2;
                result = Integer.toString(tmpResult.intValue());
            } else if (operation.equals("/")) {
                tmpResult = v1 / v2;
                result = Float.toString(tmpResult);
            } else {
                throw new BuildException("Operation parameter must be one of the following: +, -, * or /");
            }

            setProjectProperty(propertyName, result);
        } else {
            throw new BuildException("Parameter missing");
        }
    }
}
