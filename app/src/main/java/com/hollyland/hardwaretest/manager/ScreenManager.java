package com.hollyland.hardwaretest.manager;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;

import java.lang.reflect.InvocationTargetException;

public class ScreenManager {

    private static ScreenManager screenManager;

    private PowerManager powerManager ;


    private ScreenManager(Context context){
        powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    }

    public static ScreenManager getScreenManagerInstance(Context context){
        if (screenManager == null) {
            screenManager = new ScreenManager(context);
        }
        return screenManager;
    }

    public void goToSleep() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            powerManager.getClass().getMethod("goToSleep",new Class[]{long.class}).invoke(powerManager,SystemClock.uptimeMillis());
    }

    public void wakeUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            powerManager.getClass().getMethod("wakeUp",new Class[]{long.class}).invoke(powerManager,SystemClock.uptimeMillis());
    }
}
