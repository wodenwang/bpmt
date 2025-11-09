/**
 *
 */
package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IfElse extends Task {

    private If _if;
    private Else _else;
    private List<If> _elseIfs = new ArrayList<If>();

    public void addIf(If _if) {
        this._if = _if;
    }

    public void addElseIf(If _if) {
        this._elseIfs.add(_if);
    }

    public void addElse(Else _else) {
        this._else = _else;
    }

    @Override
    public void execute() throws BuildException {
        if (_if != null) {
            _if.execute();
            if (!_if.getResult()) {
                boolean executed = false;
                if (_elseIfs.size() > 0) {
                    for (Iterator<If> i = _elseIfs.iterator(); i.hasNext(); ) {
                        If nIf = i.next();
                        nIf.perform();
                        if (nIf.getResult()) {
                            executed = true;
                            break;
                        }
                    }
                }

                if (!executed && _else != null) {
                    _else.execute();
                }
            }
        }
    }

}
