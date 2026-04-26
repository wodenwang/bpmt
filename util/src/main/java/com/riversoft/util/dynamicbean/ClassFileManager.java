/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.dynamicbean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

@SuppressWarnings("rawtypes")
public class ClassFileManager extends ForwardingJavaFileManager {
    /**
     * Instance of JavaClassObject that will store the compiled bytecode of our class
     */
    private JavaClassObject jclassObject;

    /**
     * Will initialize the manager with the specified standard java file manager
     * 
     * @param standardManger
     */
    @SuppressWarnings("unchecked")
    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    /**
     * Will be used by us to get the class loader for our compiled class. It creates an anonymous class extending the
     * SecureClassLoader which uses the byte code created by the compiler and stored in the JavaClassObject, and returns
     * the Class for it
     */
    @Override
    public ClassLoader getClassLoader(Location location) {
        return new DynamicClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Gives the compiler an instance of the JavaClassObject so that the compiler can write the byte code into it.
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
            throws IOException {
        jclassObject = new JavaClassObject(className, kind);
        return jclassObject;
    }

    class DynamicClassLoader extends URLClassLoader {

        public DynamicClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                byte[] b = jclassObject.getBytes();
                try {
                    Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class,
                            byte[].class, int.class, int.class);
                    defineClassMethod.setAccessible(true);
                    defineClassMethod.invoke(getParent(), name, b, 0, b.length);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e1) {
                    // 无能为力
                    e1.printStackTrace();
                }
                return super.defineClass(name, b, 0, b.length);
            }
        }

    }

}