package com.riversoft.function;

import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * 自定义函数DEMO
 * 
 * @author river
 * 
 */
@ScriptSupport("demo")
public class DemoFunction {

    /**
     * 界面调用:<br>
     * demo.hello('Tom')
     */
    public static String hello(String name) {
        return "Hello," + name;
    }
}
