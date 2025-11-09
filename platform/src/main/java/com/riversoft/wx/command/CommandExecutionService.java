package com.riversoft.wx.command;

import com.riversoft.core.BeanFactory;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

import java.util.Map;

/**
 * Created by exizhai on 2/15/2016.
 */
public class CommandExecutionService {

    public static CommandExecutionService getInstance() {
        return BeanFactory.getInstance().getSingleBean(CommandExecutionService.class);
    }

    public Object executeCommand(ScriptTypes type, String script, Map<String, Object> context) {
        return ScriptHelper.evel(type, script, context);
    }
}
