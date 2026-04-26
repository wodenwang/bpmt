/**
 * 
 */
package com.riversoft.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类
 * 
 * @author wodenwang
 */
public class ReflectionUtils {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * 获取对象指定属性值
     * 
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> klass = obj.getClass();
            Field field = klass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            logger.error("反射获取对象值错误", e);
            return null;
        }
    }

    /**
     * 设置对象属性
     * 
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Class<?> klass = obj.getClass();
            Field field = klass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            logger.error("反射设置对象值错误", e);
        }
    }

    /**
     * 反射方法调用（无入参）
     * 
     * @param obj
     * @param methodName
     * @return
     */
    public static Object getMethodValue(Object obj, String methodName) {
        return getMethodValue(obj, methodName, new Class[] {}, new Object[] {});
    }

    /**
     * 反射方法调用
     * 
     * @param klass
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @param args
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object getMethodValue(Class<?> klass, Object obj, String methodName, Class[] parameterTypes,
            Object... args) {
        try {
            Method method = klass.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error("反射获取对象值错误", e);
            return null;
        }
    }

    /**
     * 反射方法调用
     * 
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @param args
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object getMethodValue(Object obj, String methodName, Class[] parameterTypes, Object... args) {
        Class<?> klass = obj.getClass();
        return getMethodValue(klass, obj, methodName, parameterTypes, args);
    }

}
