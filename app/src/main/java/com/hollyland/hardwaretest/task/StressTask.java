package com.hollyland.hardwaretest.task;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.storage.StorageManager;
//import android.os.storage.VolumeInfo;
import android.os.storage.VolumeInfo;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hollyland.hardwaretest.constants.Constants;
import com.hollyland.hardwaretest.entity.EventMessage;
import com.hollyland.hardwaretest.entity.ScreenTestItem;
import com.hollyland.hardwaretest.entity.StressValueItem;
import com.hollyland.hardwaretest.entity.WifiTestItem;
import com.hollyland.hardwaretest.manager.A2DPManager;
import com.hollyland.hardwaretest.manager.CustomStorageManager;
import com.hollyland.hardwaretest.manager.MediaManager;
import com.hollyland.hardwaretest.manager.ScreenManager;
import com.hollyland.hardwaretest.utils.EventBusUtils;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StressTask extends ThreadUtils.Task<Void> {

    private boolean isStop = false;

    private Context context;

    private StressValueItem stressValueItem;

    private A2DPManager a2DPManager;

    private MediaManager mediaManager;

    private ScreenManager screenManager;

    private Timer wifiTimer, screenTimer ,storageTimer;

    private NetworkUtils.OnNetworkStatusChangedListener onNetworkStatusChangedListener;

    public StressTask(StressValueItem stressValueItem, Context context) {
        this.stressValueItem = stressValueItem;
        a2DPManager = A2DPManager.getInstance(context);
        a2DPManager.setInverval(stressValueItem.getIntervalTime());
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
        switch (stressValueItem.getTestType()) {
            case StressValueItem.BLUETOOTH_STRESS:
                BluetoothDevice device = (BluetoothDevice) stressValueItem.getSpinnerBean().getData();
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
            case StressValueItem.WIFI_STRESS:
                ToastUtils.showLong("Wifi 压力测试开始");
                WifiTestItem wifiTestItem = new WifiTestItem();
                if (NetworkUtils.isWifiConnected()) {
                    NetworkUtils.setWifiEnabled(false);
                }
                wifiTimer = new Timer();
                wifiTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        wifiTask(wifiTestItem);
                    }
                }, 2000);
                break;
            case StressValueItem.SCREEN_STRESS:
                ScreenTestItem screenTestItem = new ScreenTestItem();
                TimerTask sTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            screenManager.goToSleep();
                            screenTestItem.setSleepSuccessNum(screenTestItem.getSleepSuccessNum() + 1);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                            screenTestItem.setSleepErrorNum(screenTestItem.getSleepErrorNum() + 1);
                        }
                        EventBusUtils.post(new EventMessage(Constants.EVENT.SCREEN_TEST_SLEEP, screenTestItem));
                        Log.d(Constants.TAG, "run: goToSleep");
                    }
                };

                TimerTask wTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            screenManager.wakeUp();
                            screenTestItem.setWakeUpSuccessNum(screenTestItem.getWakeUpSuccessNum() + 1);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            screenTestItem.setWakeUpErrorNum(screenTestItem.getWakeUpErrorNum() + 1);
                        }
                        EventBusUtils.post(new EventMessage(Constants.EVENT.SCREEN_TEST_WAKEUP, screenTestItem));
                        Log.d(Constants.TAG, "run: wakeUp");
                    }
                };
                screenTimer = new Timer();
                int inverval = stressValueItem.getIntervalTime() * 1000;
                Log.d(Constants.TAG, "inverval: " + inverval);
                if (StringUtils.isNotBlank(stressValueItem.getData().toString())){
                    String data = stressValueItem.getData().toString();
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
            case StressValueItem.USB_STRESS:
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
                            if (stressValueItem.getData() != null){
                                uuids =  (List<String>) stressValueItem.getData();
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
                                        SystemClock.sleep(stressValueItem.getIntervalTime() * 1000);
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


    public void wifiTask(WifiTestItem wifiTestItem) {
        NetworkUtils.setWifiEnabled(true);
        wifiTestItem.setEnableNum(wifiTestItem.getEnableNum() + 1);
        if (onNetworkStatusChangedListener == null) {
            onNetworkStatusChangedListener = new NetworkUtils.OnNetworkStatusChangedListener() {
                @Override
                public void onDisconnected() {
                    ToastUtils.showLong("断开连接");
                    wifiTestItem.setDisableNum(wifiTestItem.getDisableNum() + 1);
                    EventBusUtils.post(new EventMessage(Constants.EVENT.EVENT_WIFI_STESS_RESULT, wifiTestItem));
                    if (!isStop) {
                        new Handler().postDelayed(() -> {
                            wifiTask(wifiTestItem);
                        }, 5000);
                    }
                }

                @Override
                public void onConnected(NetworkUtils.NetworkType networkType) {
                    wifiTestItem.setConnectedNum(wifiTestItem.getConnectedNum() + 1);
                    Log.d(Constants.TAG, "Network: 已经 onConnected");
                    if (networkType == NetworkUtils.NetworkType.NETWORK_WIFI) {
                        ToastUtils.showLong("已连接");
                        NetworkUtils.isAvailableByPingAsync(aBoolean -> {
                            if (aBoolean) {
                                Log.d(Constants.TAG, "Network: 正常访问网络");
                                wifiTestItem.setSufNetNum(wifiTestItem.getSufNetNum() + 1);
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
                                    wifiTestItem.setSufNetNum(wifiTestItem.getSufNetNum() + 1);
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
