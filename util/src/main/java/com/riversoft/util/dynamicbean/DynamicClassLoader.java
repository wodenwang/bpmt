/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.dynamicbean;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class DynamicClassLoader {

    private static DynamicClassLoader instance;
    private static JavaCompiler compiler;
    private static JavaFileManager fileManager;

    private DynamicClassLoader() {
        // We get an instance of JavaCompiler. Then we create a file manager (our custom implementation of it)
        compiler = ToolProvider.getSystemJavaCompiler();
        fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
    }

    public static DynamicClassLoader getInstance() {
        if (instance == null)
            instance = new DynamicClassLoader();
        return instance;
    }

    private ClassLoader getClassLoader() {
        return fileManager.getClassLoader(null);
    }

    /**
     * 先从 Thread.currentThread().getContextClassLoader() 拿?
     * 
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            if (getClassLoader() != null) {
                return getClassLoader().loadClass(className);
            }
            throw new ClassNotFoundException(className + " was not found.");
        }
    }

    public Class<?> compileAndLoadClass(String className, String sourcecode) throws ClassNotFoundException {
        compile(className, sourcecode);
        return loadClass(className);
    }

    public void compile(String className, String sourcecode) {
        // Dynamic compiling requires specifying a list of "files" to compile. In our case this is a list containing one
        // "file" which is in our case our own implementation (see details below)
        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(className, sourcecode));

        // set the classpath
        List<String> options = new ArrayList<String>();

        options.add("-classpath");
        StringBuilder sb = new StringBuilder();
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);            
        }
        options.add(sb.toString());
        
        StringWriter writer = new StringWriter();
        // We specify a task to the compiler. Compiler should use our file manager and our list of "files".
        // Then we run the compilation with call()
        JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, null, options, null, jfiles);
        if (!task.call()) {
            throw new RuntimeException(writer.toString());
        }
        
    }

}
