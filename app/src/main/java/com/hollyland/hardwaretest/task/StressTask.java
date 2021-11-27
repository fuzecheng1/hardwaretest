package com.hollyland.hardwaretest.task;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.storage.StorageManager;
//import android.os.storage.VolumeInfo;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hollyland.hardwaretest.constants.Constants;
import com.hollyland.hardwaretest.entity.EventMessage;
import com.hollyland.hardwaretest.entity.ScreenTest;
import com.hollyland.hardwaretest.entity.StressValueBean;
import com.hollyland.hardwaretest.entity.WifiTestBean;
import com.hollyland.hardwaretest.manager.A2DPManager;
import com.hollyland.hardwaretest.manager.CustomStorageManager;
import com.hollyland.hardwaretest.manager.MediaManager;
import com.hollyland.hardwaretest.manager.ScreenManager;
import com.hollyland.hardwaretest.utils.EventBusUtils;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StressTask extends ThreadUtils.Task<Void> {

    private boolean isStop = false;

    private Context context;

    private StressValueBean stressValueBean;

    private A2DPManager a2DPManager;

    private MediaManager mediaManager;

    private ScreenManager screenManager;

    private Timer wifiTimer, screenTimer ,storageTimer;

    private NetworkUtils.OnNetworkStatusChangedListener onNetworkStatusChangedListener;

    public StressTask(StressValueBean stressValueBean, Context context) {
        this.stressValueBean = stressValueBean;
        a2DPManager = A2DPManager.getInstance(context);
        a2DPManager.setInverval(stressValueBean.getIntervalTime());
        mediaManager = MediaManager.getInstance(context);
        screenManager = ScreenManager.getScreenManagerInstance(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    public Void doInBackground() throws Throwable {
        new Thread(() -> {
            long begin = System.currentTimeMillis();
            while (!isStop) {
                SystemClock.sleep(1000);
                int time = Long.valueOf((System.currentTimeMillis() - begin) / 1000).intValue();
                EventBusUtils.post(new EventMessage(Constants.EVENT.STRESS_CURRENT_TIME, time));
            }
        }).start();
        switch (stressValueBean.getTestType()) {
            case StressValueBean.BLUETOOTH_STRESS:
                BluetoothDevice device = (BluetoothDevice) stressValueBean.getSpinnerBean().getData();
                mediaManager.startMedia("music.mp3");
                a2DPManager.setTestBluetoothDevice(device);
                a2DPManager.setStart(true);
                boolean firstLogin = true;
                while (!isStop) {
                    if (firstLogin && !a2DPManager.isEnable()) {
                        a2DPManager.enableA2DP();
                        firstLogin = false;
                        Log.d(Constants.TAG, "a2DPManager.enableA2DP():");
                    } else if (firstLogin && a2DPManager.isEnable()) {
                        Log.d(Constants.TAG, "a2DPManager.disableA2DP(): ");
                        firstLogin = false;
                        a2DPManager.disableA2DP();
                    }
                    if (!a2DPManager.isStart()) {
                        Log.d(Constants.TAG, "!isStart()");
                        break;
                    }
                }
                if (isStop) {
                    a2DPManager.setStart(false);
                }
                break;
            case StressValueBean.WIFI_STRESS:
                ToastUtils.showLong("Wifi 压力测试开始");
                WifiTestBean wifiTestBean = new WifiTestBean();
                if (NetworkUtils.isWifiConnected()) {
                    NetworkUtils.setWifiEnabled(false);
                }
                wifiTimer = new Timer();
                wifiTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        wifiTask(wifiTestBean);
                    }
                }, 2000);
                break;
            case StressValueBean.SCREEN_STRESS:
                ScreenTest screenTest = new ScreenTest();
                TimerTask sTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            screenManager.goToSleep();
                            screenTest.setSleepSuccess(screenTest.getSleepSuccess() + 1);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                            screenTest.setSleepError(screenTest.getSleepError() + 1);
                        }
                        EventBusUtils.post(new EventMessage(Constants.EVENT.SCREEN_TEST_SLEEP, screenTest));
                        Log.d(Constants.TAG, "run: goToSleep");
                    }
                };

                TimerTask wTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            screenManager.wakeUp();
                            screenTest.setWakeUpSuccess(screenTest.getWakeUpSuccess() + 1);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            screenTest.setWakeUpError(screenTest.getWakeUpError() + 1);
                        }
                        EventBusUtils.post(new EventMessage(Constants.EVENT.SCREEN_TEST_WAKEUP, screenTest));
                        Log.d(Constants.TAG, "run: wakeUp");
                    }
                };
                screenTimer = new Timer();
                int inverval = stressValueBean.getIntervalTime() * 1000;
                Log.d(Constants.TAG, "inverval: " + inverval);
                if (StringUtils.isNotBlank(stressValueBean.getData().toString())){
                    String data = stressValueBean.getData().toString();
//                    if (StringUtils.equals(Constants.ONOS_PACKAGE_NAME,data)){
//                        AppUtils.launchApp(data);
//                    }
//                    else {
//                        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
//                    }
                }
                else {
                    Log.d(Constants.TAG, " AppUtils.launchApp: " + "error instanceOf");

                }
                screenTimer.schedule(sTask, 2000, 10 * 1000);
                screenTimer.schedule(wTask, 2000 + inverval, 10 * 1000);
                break;
            case StressValueBean.USB_STRESS:
                @SuppressLint("WrongConstant") StorageManager storageManager = (StorageManager)context.getSystemService("storage");
                if (storageManager == null){
                    EventBusUtils.post(new EventMessage(Constants.EVENT.EVENT_USB_STRESS_RESULT,"usb无数据"));
                    Log.d(Constants.TAG, "usb无数据: ");
                    return null;
                }
                else {

                    storageTimer = new Timer();
                    storageTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.d(Constants.TAG, "run: 进入U盘挂载Task");
                            List<String> uuids = null;

                            CustomStorageManager customStorageManager = CustomStorageManager.getInstance(context);
                            if (stressValueBean .getData() != null){
                                uuids =  (List<String>) stressValueBean.getData();
                                if (uuids.size() != 0){
                                    for (String uuid : uuids) {
                                        if (uuid == null){
                                            continue;
                                        }

                                        int status = customStorageManager.getState(uuid);
                                        switch (status){
                                            case VolumeInfo.STATE_MOUNTED:
                                                Log.d(Constants.TAG, "STATE_MOUNTED: "+uuid);
                                                customStorageManager.unMount(uuid);
                                                break;
                                            case VolumeInfo.STATE_UNMOUNTED:
                                                Log.d(Constants.TAG, "STATE_UNMOUNTED: "+uuid);
                                                customStorageManager.mount(uuid);
                                                break;
                                            case VolumeInfo.STATE_REMOVED:
                                                Log.d(Constants.TAG, "STATE_REMOVED: "+uuid);
                                                break;
                                            case VolumeInfo.STATE_EJECTING:
                                                Log.d(Constants.TAG, "STATE_EJECTING: "+uuid);
                                                break;
                                            case VolumeInfo.STATE_BAD_REMOVAL:
                                                Log.d(Constants.TAG, "STATE_BAD_REMOVAL: "+uuid);
                                                break;
                                            case VolumeInfo.STATE_CHECKING:
                                                Log.d(Constants.TAG, "STATE_CHECKING: "+uuid);
                                                break;
                                        }
                                        SystemClock.sleep(stressValueBean.getIntervalTime() * 1000);
                                    }
                                }
                            }
                        }
                    }, 2000,3000);

                }
                break;
        }

        return null;
    }


    @Override
    public void onSuccess(Void result) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFail(Throwable t) {

    }


    public void wifiTask(WifiTestBean wifiTestBean) {
        NetworkUtils.setWifiEnabled(true);
        wifiTestBean.setEnableNum(wifiTestBean.getEnableNum() + 1);
        if (onNetworkStatusChangedListener == null) {
            onNetworkStatusChangedListener = new NetworkUtils.OnNetworkStatusChangedListener() {
                @Override
                public void onDisconnected() {
                    ToastUtils.showLong("断开连接");
                    wifiTestBean.setDisableNum(wifiTestBean.getDisableNum() + 1);
                    EventBusUtils.post(new EventMessage(Constants.EVENT.EVENT_WIFI_STESS_RESULT, wifiTestBean));
                    if (!isStop) {
                        new Handler().postDelayed(() -> {
                            wifiTask(wifiTestBean);
                        }, 5000);
                    }
                }

                @Override
                public void onConnected(NetworkUtils.NetworkType networkType) {
                    wifiTestBean.setConnectedNum(wifiTestBean.getConnectedNum() + 1);
                    Log.d(Constants.TAG, "Network: 已经 onConnected");
                    if (networkType == NetworkUtils.NetworkType.NETWORK_WIFI) {
                        ToastUtils.showLong("已连接");
                        NetworkUtils.isAvailableByPingAsync(aBoolean -> {
                            if (aBoolean) {
                                Log.d(Constants.TAG, "Network: 正常访问网络");
                                wifiTestBean.setSufNetNum(wifiTestBean.getSufNetNum() + 1);
                                NetworkUtils.setWifiEnabled(false);
                            } else {
                                /*
                                如果首次异步网络未正常访问，5秒后再次访问，如果还未正常，则停止
                                 */
                                new Handler().postDelayed(() -> {
                                    boolean available = NetworkUtils.isAvailableByPing("www.baidu.com");
                                    if (!available) {
                                       stop();
                                       return;
                                    }
                                    Log.d(Constants.TAG, "Network: 首次访问网络失败");
                                    wifiTestBean.setSufNetNum(wifiTestBean.getSufNetNum() + 1);
                                    NetworkUtils.setWifiEnabled(false);
                                }, 5000);
                            }
                        });

                    }
                }
            };
        }
        NetworkUtils.registerNetworkStatusChangedListener(onNetworkStatusChangedListener);
    }

    public void stop() {
        isStop = true;
        if (mediaManager != null) {
            mediaManager.destroyPlayer();
        }
        if (a2DPManager != null) {
            a2DPManager.unBondAllDevice();
        }
        if (wifiTimer != null) {
            wifiTimer.cancel();
        }
        if (screenTimer != null) {
            screenTimer.cancel();
        }
        if (storageTimer != null){
            storageTimer.cancel();
        }

        if (onNetworkStatusChangedListener != null) {
            NetworkUtils.unregisterNetworkStatusChangedListener(onNetworkStatusChangedListener);
        }

    }


}
