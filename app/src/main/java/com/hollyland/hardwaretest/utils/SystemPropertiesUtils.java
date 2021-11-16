package com.hollyland.hardwaretest.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 反射获取系统属性值
 */
public class SystemPropertiesUtils {


    @SuppressLint("PrivateApi")
    public static String get(String key,String defineValue){
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get",String.class,String.class);
            return (String)method.invoke(clazz,key,defineValue);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }


    @SuppressLint("PrivateApi")
    public static void set(String key,String value){
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get",String.class,String.class);
            method.invoke(clazz,key,value);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
