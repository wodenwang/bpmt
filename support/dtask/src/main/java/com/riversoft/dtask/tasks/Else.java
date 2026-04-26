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

public class Else extends Task implements TaskContainer {

    private List<Task> tasks = new ArrayList<Task>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void execute() throws BuildException {
        for (Iterator<Task> i = tasks.iterator(); i.hasNext(); ) {
            i.next().perform();
        }
    }
}