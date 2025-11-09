/**
 *
 */
package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Supported operators: '>', '<', '==', '!=', '||', '&&'
 * <p/>
 * '<' and '>' are evaluated by comparing <code>property</code> with <code>value</code> after
 * trying to parse their values as integers. Failure to do so will cause a BuildException.
 * <p/>
 * '||' and '&&' are evaluated by comparing <code>property</code> with <code>value</code> after
 * trying to parse their values as booleans. A bad value will be parsed as "false"
 * <p/>
 * '==' and '!=' are string compared, equals or !equals
 */
public class If extends Task implements TaskContainer {

    private List<Task> tasks = new ArrayList<Task>();

    private String property;
    private String value;
    private String operator;

    private boolean result;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean getResult() {
        return result;
    }

    private boolean eval() {
        result = false;
        String propVal = getProject().getProperty(property);

        if (operator == null && value == null) {
            result = propVal != null;
        } else if (operator == null) {
            throw new BuildException("Missing operator attribute.");
        } else if (value == null) {
            throw new BuildException("Missing value attribute.");
        } else if ("<".equals(operator)) {
            // Numeric less than
            try {
                result = Integer.parseInt(propVal) < Integer.parseInt(value);
            } catch (Exception e) {
                throw new BuildException(String.format("Failed to evaluate '%1$s %2$s %3$s'", propVal, operator, value));
            }
        } else if (">".equals(operator)) {
            // Numeric greater than
            try {
                result = Integer.parseInt(propVal) > Integer.parseInt(value);
            } catch (Exception e) {
                throw new BuildException(String.format("Failed to evaluate '%1$s %2$s %3$s'", propVal, operator, value));
            }
        } else if ("&&".equals(operator)) {
            // Boolean AND
            result = Boolean.parseBoolean(value) && Boolean.parseBoolean(propVal);
        } else if ("||".equals(operator)) {
            // Boolean OR
            result = Boolean.parseBoolean(value) || Boolean.parseBoolean(propVal);
        } else if ("==".equals(operator)) {
            // Equality
            result = value.equals(propVal);
        } else if ("!=".equals(operator)) {
            // Not equal to
            result = !value.equals(propVal);
        } else {
            throw new BuildException(String.format("Bad operator '%1$s' invalid.", operator));
        }

        return result;
    }

    public void execute() throws BuildException {
        if (eval()) {
            for (Iterator<Task> i = tasks.iterator(); i.hasNext(); ) {
                i.next().perform();
            }
        }
    }
}