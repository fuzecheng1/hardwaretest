package com.hollyland.hardwaretest.manager;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hollyland.hardwaretest.constants.Constants;
import com.hollyland.hardwaretest.entity.EventMessage;
import com.hollyland.hardwaretest.utils.ClsUtils;
import com.hollyland.hardwaretest.utils.EventBusUtils;

import java.util.HashSet;
import java.util.Set;

public class A2DPManager {


    private static A2DPManager mManager;

    private BluetoothAdapter mAdapter;

    private BluetoothA2dp mA2dp;

    private Set<BluetoothDevice> testBlueToothDevices;

    private BluetoothDevice testBluetoothDevice;

    private boolean isRegister;

    private boolean isPlaying;

    private boolean start = false;

    private int inverval = 10;


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    private boolean isBroadCastRegister() {
        return isRegister;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    public BluetoothDevice getTestBluetoothDevice() {
        return testBluetoothDevice;
    }

    public void setTestBluetoothDevice(BluetoothDevice testBluetoothDevice) {
        this.testBluetoothDevice = testBluetoothDevice;
    }

    public Set<BluetoothDevice> getDeviceResult() {
        return testBlueToothDevices;
    }

    public int getInverval() {
        return inverval;
    }

    public void setInverval(int inverval) {
        this.inverval = inverval;
    }

    public static A2DPManager getInstance(Context context) {
        if (mManager == null) {
            mManager = new A2DPManager(context);
        }
        return mManager;
    }

    public BluetoothAdapter getmAdapter() {
        return mAdapter;
    }

    public Set<BluetoothDevice> getConnectDevice() {
        return mAdapter.getBondedDevices();
    }


    private A2DPManager(Context context) {
        testBlueToothDevices = new HashSet<>();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                mA2dp = (BluetoothA2dp) proxy;
            }

            @Override
            public void onServiceDisconnected(int profile) {
                mA2dp = null;
            }
        }, BluetoothProfile.A2DP);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ACL_CONNECTED);
        intentFilter.addAction(ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(ACTION_FOUND);
        intentFilter.addAction(ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(ACTION_REQUEST_ENABLE);
        intentFilter.addAction(ACTION_STATE_CHANGED);
        intentFilter.addAction(ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        if (!isBroadCastRegister()) {
            context.registerReceiver(mReceiver, intentFilter);
            setRegister(true);

        }

    }

    /**
     * 打开蓝牙
     */
    public void enableA2DP() {
        if (!mAdapter.isEnabled()) {
            Log.i(Constants.TAG, "开启蓝牙");
            mAdapter.enable();
        }
    }

    /**
     * @param
     */
    public boolean isEnable() {
        return mAdapter.isEnabled();
    }

    /**
     * 查看是否存在该设备
     *
     * @param device
     * @return
     */

    public boolean isDeviceFound(BluetoothDevice device) {
        Set<BluetoothDevice> set = new HashSet<>();
        set.addAll(getBondeDevices());
        set.addAll(testBlueToothDevices);
        return set.contains(device);
    }


    /**
     * 常用设备查找
     *
     * @param
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public BluetoothDevice findCustomBlueToothDevice(String deviceName) {
        for (BluetoothDevice device : testBlueToothDevices) {
            String name = device.getName();
            if (StringUtils.equals(name, deviceName)) {
                return device;
            }
        }
        return null;
    }


    public void destroyManager(Context context) {
        mAdapter.closeProfileProxy(BluetoothProfile.A2DP, mA2dp);
        disableA2DP();
        mAdapter = null;
        mManager = null;
        mA2dp = null;
        if (isBroadCastRegister()) {
            context.unregisterReceiver(mReceiver);
            setRegister(false);
        }


    }

    /**
     * 关闭蓝牙
     */
    public void disableA2DP() {
        if (mAdapter.isEnabled()) {
            mAdapter.disable();
            testBlueToothDevices.clear();
        }
    }

    public boolean isDiscovering() {
        return mAdapter.isDiscovering();
    }


    public boolean discovery() {
        if (!testBlueToothDevices.isEmpty()) {
            testBlueToothDevices.clear();
        }
        if (mAdapter.isEnabled()) {
            Log.i(Constants.TAG, "已开启 ，开始搜索");
            return mAdapter.startDiscovery();
        }
        return false;
    }

    /**
     * 连接设备
     *
     * @param device
     */
    public boolean connect(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            //如果设备未配对，先进行配对，收到配对成功广播后，连接设备
            pair(device);
        } else {
            if (mA2dp != null) {
                if (mA2dp.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED) {
                    try {
                        return (boolean) mA2dp.getClass()
                                .getMethod("connect", BluetoothDevice.class)
                                .invoke(mA2dp, device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * 断开设备
     *
     * @param device
     */
    public boolean disConnect(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        boolean disConnect = false;
        if (mA2dp != null) {
            if (mA2dp.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED) {
                try {
                    disConnect = (boolean) mA2dp.getClass()
                            .getMethod("disconnect", BluetoothDevice.class)
                            .invoke(mA2dp, device);
                    Log.d(Constants.TAG, "disconnect:" + disConnect);
                    ClsUtils.removeBond(BluetoothDevice.class, device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return disConnect;
    }

    /**
     * 蓝牙配对
     *
     * @param device
     */
    public void pair(final BluetoothDevice device) {
        HandlerThread handlerThread = new HandlerThread("pair");
        handlerThread.start();
        Handler pairHandler = new Handler(handlerThread.getLooper());
        try {
            ClsUtils.createBond(BluetoothDevice.class, device);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d(Constants.TAG, "pair: 进入高版本 安卓11");
                ClsUtils.cancelPairingUserInput(BluetoothDevice.class, device);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已经配对的设备
     */
    public Set<BluetoothDevice> getBondeDevices() {
        return mAdapter.getBondedDevices();
    }


    /**
     * 是否建立连接
     *
     * @return
     */
    public boolean isConnected() {
        return (mAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED);
    }

    public void unBondAllDevice() {
        for (BluetoothDevice bondeDevice : getBondeDevices()) {
            disConnect(bondeDevice);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    /**
                     * 屏蔽蓝牙配对框
                     */
                    if (testBluetoothDevice.getName().equals(btDevice.getName())) {
                        abortBroadcast();
                        btDevice.setPairingConfirmation(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int playStauts = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);

            if (playStauts == BluetoothA2dp.STATE_PLAYING) {
                EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_PLAYING));
                setPlaying(true);
                if (isStart()) {
                    new Handler().postDelayed(() -> disConnect(getTestBluetoothDevice()), inverval * 1000);
                }
            } else if (playStauts == BluetoothA2dp.STATE_NOT_PLAYING) {
                setPlaying(false);
            }

            switch (action) {
                case ACTION_ACL_CONNECTED:
                    EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_CONNECT));
                    break;
                case ACTION_REQUEST_ENABLE:
                    break;
                case ACTION_FOUND:
                    testBlueToothDevices.add(device);
                    EventBusUtils.post(new EventMessage(Constants.EVENT.EVENT_BT_SEARCH_DEVICE_LIST, testBlueToothDevices));
                    break;
                case ACTION_BOND_STATE_CHANGED:
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            Log.d(Constants.TAG, "正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d(Constants.TAG, "完成配对");
                            EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_BOND));
                            if (isStart()) {
                                new Handler().postDelayed(() -> {
                                    boolean isConnect = connect(getTestBluetoothDevice());
                                    Log.d(Constants.TAG, "connect: " + isConnect);
                                }, 2000);
                            }
                            //完成配对后再次连接 设备
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.d(Constants.TAG, "取消配对");
                            if (isStart()) {
                                new Handler().postDelayed(() -> {
                                    disableA2DP();
                                }, 3000);

                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_DISCOVERY_FINISHED:
                    Log.d(Constants.TAG, "搜索完成");
                    testBlueToothDevices.addAll(A2DPManager.getInstance(context).getBondeDevices());
                    if (isStart()) {
                        EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_DISCOVERY));
                        if (getTestBluetoothDevice() != null) {
                            ToastUtils.showLong("蓝牙压力测试: 开始绑定设备");
                            pair(getTestBluetoothDevice());
                        } else {
                            ToastUtils.showLong("设备未设置");
                        }

                    }
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    break;
                case ACTION_STATE_CHANGED:
                    int blueStatus = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueStatus) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i(Constants.TAG, "onReceive---------蓝牙正在打开中");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_EABLE));
                            Log.i(Constants.TAG, "onReceive---------蓝牙已经打开");
                            discovery();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.i(Constants.TAG, "onReceive---------蓝牙正在关闭中");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i(Constants.TAG, "onReceive---------蓝牙已经关闭");
                            EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STRESS_BT_DISABLE));
                            if (isStart()) {
                                new Handler().postDelayed(() -> {
                                    enableA2DP();
                                }, 2000);
                            }
                            break;
                    }
                    Log.i(Constants.TAG, "广播 - > {} 状态变更");
                    break;
            }
        }
    };

}
