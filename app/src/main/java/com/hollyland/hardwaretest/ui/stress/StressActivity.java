package com.hollyland.hardwaretest.ui.stress;

import static android.content.Intent.ACTION_MEDIA_EJECT;
import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_DETACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hollyland.hardwaretest.R;
import com.hollyland.hardwaretest.adapter.StressUsbAdapter;
import com.hollyland.hardwaretest.constants.Constants;
import com.hollyland.hardwaretest.entity.BlueToothTestItem;
import com.hollyland.hardwaretest.entity.EventMessage;
import com.hollyland.hardwaretest.entity.ScreenTestItem;
import com.hollyland.hardwaretest.entity.SpinnerItem;
import com.hollyland.hardwaretest.entity.StressValueItem;
import com.hollyland.hardwaretest.entity.WifiTestItem;
import com.hollyland.hardwaretest.manager.A2DPManager;
import com.hollyland.hardwaretest.manager.CustomStorageManager;
import com.hollyland.hardwaretest.receiver.BootCompletedReceiver;
import com.hollyland.hardwaretest.task.StressTask;
import com.hollyland.hardwaretest.ui.BaseActivity;
import com.hollyland.hardwaretest.utils.EventBusUtils;
import com.hollyland.hardwaretest.utils.SystemPropertiesUtils;



import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StressActivity extends BaseActivity {


    private StressValueItem stressValueItem;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.usb_listview)
    ListView mUsbListView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_stress_bind)
    Button mBtnStessBind;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_type_bt)
    RadioButton mRbBluetoothType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_type_wifi)
    RadioButton mRbWifiType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_type_screen)
    RadioButton mRbScreenType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_type_usb)
    RadioButton mRbUsbType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_type_reopen_device)
    RadioButton mRbReopenDeviceType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sp_stress_device)
    Spinner mSpStressDevice;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_start)
    Button mBtnStart;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_total_count)
    EditText mEtTotalCount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_current_time)
    EditText mEtCurrentTime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_intervals_time)
    EditText mEtIntervalsTime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_stress_device)
    TextView mEtStressDevice;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_headerview_title)
    TextView mTvTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_errror)
    TextView tvErrror;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_stress_http)
    CheckBox mCbhttpRequest;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_stress_usb)
    CheckBox mCbReopenByUSB;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_stress_poweron_startup)
    CheckBox mCbReopenPowerOnStartup;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_ws_hollyos)
    RadioButton mRbwsByonOS;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_stress_ws_settings)
    RadioButton mRbwsBySettings;

    private StressUsbAdapter stressUsbAdapter ;

    private ArrayAdapter<SpinnerItem> adapter;

    private A2DPManager btManager;

    private StressTask stressTask;

    private Timer mTimer;

    private SpinnerItem item;

    private CustomStorageManager customStorageManager;


    public List<SpinnerItem> getmTemplateSpinnerList() {
        return mTemplateSpinnerList;
    }

    public void setmTemplateSpinnerList(List<SpinnerItem> mTemplateSpinnerList) {
        this.mTemplateSpinnerList = mTemplateSpinnerList;
    }

    private List<SpinnerItem> mTemplateSpinnerList;

    private boolean isStop = false;

    private boolean isUSBTest = false;


    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //初始化


    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    private void initView() {
        //初始化sharedPreferences
//        ComponentName componentName = new ComponentName(Con)
        customStorageManager = CustomStorageManager.getInstance(this);
        stressValueItem = new StressValueItem(0, 0, 20, false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_MEDIA_MOUNTED);
        filter.addAction(ACTION_MEDIA_UNMOUNTED);
        filter.addAction(ACTION_MEDIA_EJECT);
//        监听挂载需要添加
        filter.addDataScheme("file");
        registerReceiver(stressReceiver, filter);
        initRestart();
        boolean isTest = SPStaticUtils.getBoolean(Constants.SP.UPDOWN_IS_TEST, false);
        boolean isHttpRequest = SPStaticUtils.getBoolean(Constants.SP.REQUIRE_HTTP, false);
        boolean isUsbMode = SPStaticUtils.getBoolean(Constants.SP.UPDOWN_USB_MODE, false);
        long interval = SPStaticUtils.getLong(Constants.SP.UPDOWN_INTERVALS, 0L);

        if (interval > 0L) {
            mEtIntervalsTime.setText(String.valueOf(interval));
        }
        if (isUsbMode) {
            mCbReopenByUSB.setChecked(true);
        }


        /**
         * 开关机状态
         */
        if (isTest) {
            if (isHttpRequest) {
                mCbhttpRequest.setChecked(true);
            }
            stressValueItem.setTestType(StressValueItem.RESTART_STRESS);
            mBtnStart.setEnabled(false);
            tvErrror.setText("开关机正在测试中...." + "\n" + "总次数: " + SPStaticUtils.getInt(Constants.SP.UPDOWN_TOTAL, 0) + "\n" + "已经执行次数: " + SPStaticUtils.getInt(Constants.SP.UPDOWN_RESTART, 0));
            Log.d(Constants.TAG, "重启时间间隔: " + mEtIntervalsTime.getText().toString());
            if (!isUsbMode) {
                shutDown(this, Long.parseLong(mEtIntervalsTime.getText().toString()) * 1000);
            }
            mRbReopenDeviceType.setChecked(true);

        }

        String mSysPowerStartup = SystemPropertiesUtils.get("persist.sys.powerup.startup","");
        Log.d(Constants.TAG, "persist.sys.powerup.startup: " + mSysPowerStartup);
        if (StringUtils.equals(mSysPowerStartup.trim(),"on")){
            mCbReopenPowerOnStartup.setChecked(true);
        }


        //初始化蓝牙
        if (mRbBluetoothType.isChecked()) {
            btManager = A2DPManager.getInstance(this);
            btManager.enableA2DP();
            btManager.discovery();
            //设置banner
            mTvTitle.setText(getResources().getText(R.string.stess_test));
            mSpStressDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item = (SpinnerItem) mSpStressDevice.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
        }

    }


    /**
     * 当测试选项被选择后，初始化设备选项
     */
    private void initSpinnerView(List<SpinnerItem> spinnerItems) {
        adapter = new ArrayAdapter<SpinnerItem>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpStressDevice.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    /**
     * 当测试选项被选择后，初始化设备选项
     */
    private void refreshSpinnerView(List<SpinnerItem> spinnerItems) {
        if (mTemplateSpinnerList == null) {
            mTemplateSpinnerList = new ArrayList<>();
        }
        if (mTemplateSpinnerList.size() == 0) {
            initSpinnerView(spinnerItems);
            setmTemplateSpinnerList(spinnerItems);
        } else {
            if (mTemplateSpinnerList.size() + 2 < spinnerItems.size()) {
                adapter.clear();
                adapter.addAll(spinnerItems);
                setmTemplateSpinnerList(spinnerItems);
                adapter.notifyDataSetChanged();
                Log.i(Constants.TAG, "refresh: ");
            }
        }
    }

    private void stop() {
        Log.d(Constants.TAG, "stop");
        if (stressTask != null) {
            setStop(true);
            stressTask.stop();
        }
        if (mTimer != null){
            mTimer.cancel();
        }
        clearRestart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @OnCheckedChanged({R.id.rb_stress_type_bt, R.id.rb_stress_type_wifi,R.id.rb_stress_type_usb, R.id.rb_stress_type_screen, R.id.rb_stress_type_reopen_device,R.id.cb_stress_poweron_startup})
    void onCheckedChange(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_stress_type_bt:
                if (isChecked) {
                    stressValueItem.setTestType(StressValueItem.BLUETOOTH_STRESS);
                    stressValueItem.setBtBean(new BlueToothTestItem());
                    btManager.unBondAllDevice();
                    mCbReopenByUSB.setVisibility(View.GONE);
                    mCbReopenPowerOnStartup.setVisibility(View.GONE);
                    mRbwsByonOS.setVisibility(View.GONE);
                    mRbwsBySettings.setVisibility(View.GONE);
                    mUsbListView.setVisibility(View.GONE);
                    tvErrror.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.rb_stress_type_wifi:
                if (isChecked) {
                    stressValueItem.setTestType(StressValueItem.WIFI_STRESS);
                    mCbReopenByUSB.setVisibility(View.GONE);
                    mCbReopenPowerOnStartup.setVisibility(View.GONE);
                    mRbwsByonOS.setVisibility(View.GONE);
                    mRbwsBySettings.setVisibility(View.GONE);
                    mUsbListView.setVisibility(View.GONE);
                    tvErrror.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_stress_type_screen:
                if (isChecked) {
                    mEtIntervalsTime.setText("5");
                    stressValueItem.setTestType(StressValueItem.SCREEN_STRESS);
                    mCbReopenByUSB.setVisibility(View.GONE);
                    mCbReopenPowerOnStartup.setVisibility(View.GONE);
                    mRbwsByonOS.setVisibility(View.VISIBLE);
                    mRbwsBySettings.setVisibility(View.VISIBLE);
                    mUsbListView.setVisibility(View.GONE);
                    tvErrror.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.rb_stress_type_usb:
                if (isChecked) {
                    stressValueItem.setTestType(StressValueItem.USB_STRESS);
                    stressValueItem.setIntervalTime(Integer.parseInt(mEtIntervalsTime.getText().toString()));
                    Log.d(Constants.TAG, "onCheckedChange: rb_stress_type_usb");
                    mCbReopenByUSB.setVisibility(View.GONE);
                    mCbReopenPowerOnStartup.setVisibility(View.GONE);
                    mRbwsByonOS.setVisibility(View.GONE);
                    mRbwsBySettings.setVisibility(View.GONE);
                    mUsbListView.setVisibility(View.VISIBLE);
                    tvErrror.setVisibility(View.GONE);
                    if (stressUsbAdapter == null) {
                        stressUsbAdapter = new StressUsbAdapter();
                        stressUsbAdapter.setData(customStorageManager.getStorageVolumes());
                        mUsbListView.setAdapter(stressUsbAdapter);
                    }

                }
                break;
            case R.id.rb_stress_type_reopen_device:
                if (isChecked) {
                    stressValueItem.setTestType(StressValueItem.RESTART_STRESS);
                    Log.d(Constants.TAG, "onCheckedChange: rb_stress_type_reopen_device");
                    mRbwsByonOS.setVisibility(View.GONE);
                    mRbwsBySettings.setVisibility(View.GONE);
                    mCbReopenByUSB.setVisibility(View.VISIBLE);
                    mCbReopenPowerOnStartup.setVisibility(View.VISIBLE);
                    mUsbListView.setVisibility(View.GONE);
                    tvErrror.setVisibility(View.VISIBLE);
                    mEtTotalCount.setText("30");
                }
                break;
            case R.id.cb_stress_poweron_startup:
                SystemPropertiesUtils.set("persist.sys.powerup.startup", isChecked ? "on" : "off");
                Log.d(Constants.TAG, "persist.sys.powerup.startup : " + SystemPropertiesUtils.get("persist.sys.powerup.startup",""));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.tv_headerview_back, R.id.btn_start, R.id.btn_stop, R.id.btn_clear_count, R.id.btn_stress_bind})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_stress_bind:
                if (item == null) {
                    ToastUtils.showLong("请输入设备");
                    break;
                }
                stressValueItem.setSpinnerBean(item);
                mEtStressDevice.setText(stressValueItem.getSpinnerBean().getName());
                break;
            case R.id.tv_headerview_back://返回
                finish();
                break;
            case R.id.btn_start://开始
                PackageManager pm = this.getApplicationContext().getPackageManager();
                ComponentName name = new ComponentName(this.getApplicationContext(), BootCompletedReceiver.class);
                pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                int enable = pm.getComponentEnabledSetting(name);
                Log.d(Constants.TAG, "getComponentEnabledSetting: " + enable);
                setStop(false);
                stressValueItem.setInTesting(true);
                createLogDir();
                stressValueItem.setIntervalTime(Integer.parseInt(mEtIntervalsTime.getText().toString()));

                if (stressValueItem.getTestType() == StressValueItem.RESTART_STRESS) {
                    if (StringUtils.isBlank(mEtTotalCount.getText())) {
                        ToastUtils.showLong("请输入测试次数");
                        return;
                    }
                    int totalCount = Integer.parseInt(mEtTotalCount.getText().toString());
                    SPStaticUtils.put(Constants.SP.UPDOWN_RESTART,0);
                    SPStaticUtils.put(Constants.SP.UPDOWN_TOTAL, totalCount);

                    if (mCbhttpRequest.isChecked()) {
                        SPStaticUtils.put(Constants.SP.REQUIRE_HTTP, true);
                    }
                    if (mCbReopenByUSB.isChecked()) {
                        SPStaticUtils.put(Constants.SP.UPDOWN_USB_MODE, true);
                    } else {
                        SPStaticUtils.put(Constants.SP.UPDOWN_INTERVALS, Long.parseLong(mEtIntervalsTime.getText().toString()));
                    }

                    SPStaticUtils.put(Constants.SP.UPDOWN_IS_TEST, true);
                    mBtnStart.setEnabled(false);
                    if (!mCbReopenByUSB.isChecked()) {
                        shutDown(this, Long.parseLong(mEtIntervalsTime.getText().toString()) * 1000);
                    }
                    return;
                } else if (stressValueItem.getTestType() == StressValueItem.SCREEN_STRESS) {
//                    if (mRbwsByonOS.isChecked()) {
//                        if (!AppUtils.isAppInstalled(Constants.ONOS_PACKAGE_NAME)) {
//                            ToastUtils.showLong("onOS未安装");
//                            return;
//                        }
//                        stressValueBean.setData(Constants.ONOS_PACKAGE_NAME);
//                    } else if (mRbwsBySettings.isChecked()) {
//                        stressValueBean.setData(Constants.SETTINS_PACKAGE_NAME);
//                    }
                }else if (stressValueItem.getTestType() == StressValueItem.USB_STRESS){
                    isUSBTest = true;
                    List<StorageVolume> checkedVolumes = stressUsbAdapter.getCheckedVolumes();
                    List<String> uuids = checkedVolumes.stream().map(StorageVolume::getUuid).collect(Collectors.toList());
                    stressValueItem.setData(uuids);

                    for (Method declaredMethod : StorageManager.class.getDeclaredMethods()) {
                        Log.d(Constants.TAG, "declaredMethod: " + declaredMethod.getName());
                    }
                    try {
                        Method[] methods = Class.forName("android.os.storage.VolumeInfo").getDeclaredMethods();
                        for (Method method :  methods) {
                            Log.d(Constants.TAG, "VolumeInfo: " + method.getName());
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }


                stressTask = new StressTask(stressValueItem, this);
                ThreadUtils.executeBySingle(stressTask);
                break;
            case R.id.btn_stop://停止
                pm = this.getApplicationContext().getPackageManager();
                name = new ComponentName(this.getApplicationContext(), BootCompletedReceiver.class);
                pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                if (isUSBTest){
                    isUSBTest = false;
                }
                Log.d(Constants.TAG, "btn_stop: COMPONENT_ENABLED_STATE_DISABLED");
                stop();
                break;
            case R.id.btn_clear_count:
                mEtTotalCount.setText("");
                mEtCurrentTime.setText("");
                tvErrror.setText("");
                mEtTotalCount.setEnabled(true);
                clearRestart();
                if (btManager != null) {
                    btManager.unBondAllDevice();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btManager != null) {
            btManager.destroyManager(this);
        }
        unregisterReceiver(stressReceiver);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onMessageOnMain(EventMessage eventMessage) {
        super.onMessageOnMain(eventMessage);
        if (eventMessage.getCode() == Constants.EVENT.STRESS_CURRENT_TIME) {
            mEtCurrentTime.setText(String.valueOf(eventMessage.getData()));
        } else if (eventMessage.getCode() == Constants.EVENT.EVENT_BT_SEARCH_DEVICE_LIST) {
            Set<BluetoothDevice> data = (Set<BluetoothDevice>) eventMessage.getData();
            List<SpinnerItem> beans = new ArrayList<>();
            for (BluetoothDevice datum : data) {
                SpinnerItem spinnerItem = new SpinnerItem();
                spinnerItem.setName(datum.getName());
                spinnerItem.setDesc(datum.getAddress());
                spinnerItem.setData(datum);
                beans.add(spinnerItem);
            }
            refreshSpinnerView(beans);
        } else if (eventMessage.getCode() == Constants.EVENT_STRESS.STOP_TEST) {
            stop();
        } else if (eventMessage.getCode() == Constants.EVENT.SCREEN_TEST_SLEEP) {
            ScreenTestItem screenTestItem = (ScreenTestItem) eventMessage.getData();
            tvErrror.setText("休眠success次数 :" + screenTestItem.getSleepSuccessNum() + "\n" +
                    "休眠error次数 :" + screenTestItem.getSleepErrorNum() + "\n" +
                    "唤醒success次数 :" + screenTestItem.getWakeUpSuccessNum() + "\n" +
                    "唤醒error次数 :" + screenTestItem.getWakeUpErrorNum() + "\n");
        } else if (eventMessage.getCode() == Constants.EVENT.SCREEN_TEST_WAKEUP) {
            ScreenTestItem screenTestItem = (ScreenTestItem) eventMessage.getData();
            tvErrror.setText("休眠success次数 :" + screenTestItem.getSleepSuccessNum() + "\n" +
                    "休眠error次数 :" + screenTestItem.getSleepErrorNum() + "\n" +
                    "唤醒success次数 :" + screenTestItem.getWakeUpSuccessNum() + "\n" +
                    "唤醒error次数 :" + screenTestItem.getWakeUpErrorNum() + "\n");
        } else if (eventMessage.getCode() == Constants.EVENT.EVENT_WIFI_STESS_RESULT) {
            StringBuilder builder = new StringBuilder();
            WifiTestItem wifiTestItem = (WifiTestItem) eventMessage.getData();
            if (wifiTestItem != null) {
                builder.append("Wi-fi开启次数:").append(wifiTestItem.getEnableNum()).append("\n")
                        .append("Wi-fi连接次数:").append(wifiTestItem.getConnectedNum()).append("\n")
                        .append("Wi-fi上网次数:").append(wifiTestItem.getSufNetNum()).append("\n")
                        .append("Wi-fi断开次数:").append(wifiTestItem.getDisableNum()).append("\n");
                tvErrror.setText(builder);
                Log.d(Constants.TAG, "onEventMessageByMain: getCurrentNum :" + wifiTestItem.getCurrentNum());
                mEtTotalCount.setText(wifiTestItem.getCurrentNum() + "");
            }
        } else if (stressValueItem.getBtBean() != null) {
            StringBuilder builder = new StringBuilder();
            switch (eventMessage.getCode()) {
                case Constants.EVENT_STRESS.STRESS_BT_EABLE:
                    stressValueItem.getBtBean().setEnableNum(stressValueItem.getBtBean().getEnableNum() + 1);
                    break;
                case Constants.EVENT_STRESS.STRESS_BT_DISCOVERY:
                    stressValueItem.getBtBean().setDiscoveryNum(stressValueItem.getBtBean().getDiscoveryNum() + 1);
                    break;
                case Constants.EVENT_STRESS.STRESS_BT_BOND:
                    stressValueItem.getBtBean().setBondNum(stressValueItem.getBtBean().getBondNum() + 1);
                    break;
                case Constants.EVENT_STRESS.STRESS_BT_CONNECT:
                    stressValueItem.getBtBean().setConnectNum(stressValueItem.getBtBean().getConnectNum() + 1);
                    break;
                case Constants.EVENT_STRESS.STRESS_BT_PLAYING:
                    stressValueItem.getBtBean().setPlayNum(stressValueItem.getBtBean().getPlayNum() + 1);
                    break;
                case Constants.EVENT_STRESS.STRESS_BT_DISABLE:
                    stressValueItem.getBtBean().setDisableNum(stressValueItem.getBtBean().getDisableNum() + 1);
                    break;
            }

            builder.append("蓝牙开启：").append(stressValueItem.getBtBean().getEnableNum()).append("\n")
                    .append("蓝牙搜索：").append(stressValueItem.getBtBean().getDiscoveryNum()).append("\n")
                    .append("蓝牙匹配设备：").append(stressValueItem.getBtBean().getBondNum()).append("\n")
                    .append("蓝牙播放：").append(stressValueItem.getBtBean().getPlayNum()).append("\n")
                    .append("蓝牙连接：").append(stressValueItem.getBtBean().getConnectNum()).append("\n")
                    .append("蓝牙关闭：").append(stressValueItem.getBtBean().getDisableNum()).append("\n");
            tvErrror.setText(builder);
        }


    }

    @Override
    protected int setContentView() {
        return R.layout.activity_stress;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void initRestart() {
        int restart = SPStaticUtils.getInt(Constants.SP.UPDOWN_RESTART, 0);
        int totalCount =  SPStaticUtils.getInt(Constants.SP.UPDOWN_TOTAL, 0);
        if (restart > totalCount) {
            clearRestart();
        }
    }


    private void clearRestart() {
        SPStaticUtils.clear();
//        stressDataWriter.putBoolean("isTest", false);
//        stressDataWriter.putInt("restart", 0);
//        stressDataWriter.putInt("totalCount", 0);
//        stressDataWriter.putBoolean("httpRequest", false);
//        stressDataWriter.putBoolean("usbMode", false);
//        stressDataWriter.putLong("inverval", 0L);
//        stressDataWriter.apply();
        mBtnStart.setEnabled(true);
    }


    private void ping() {
        new Thread(() -> {
            try {
                OkHttpClient okHttpClient = new OkHttpClient.Builder().
                        connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS) //读取超时
                        .writeTimeout(5, TimeUnit.SECONDS).
                                build();
                Request request = new Request.Builder().url("https://www.baidu.com").build();
                Response response = okHttpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    ToastUtils.showLong("请求不成功");
                    setStop(true);
                    EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STOP_TEST));
                } else {
                    ToastUtils.showLong("请求成功");
                }
            } catch (IOException e) {
                setStop(true);
                EventBusUtils.post(new EventMessage(Constants.EVENT_STRESS.STOP_TEST));
                ToastUtils.showLong("请求不成功");
                e.printStackTrace();
            }
        }).start();
    }


    /***
     * 关机
     * @param context
     */
    private void shutDown(Context context, long delay) {
        int restart = SPStaticUtils.getInt(Constants.SP.UPDOWN_RESTART, 0);
        int totalCount =  SPStaticUtils.getInt(Constants.SP.UPDOWN_TOTAL, 0);
        boolean isTest = SPStaticUtils.getBoolean(Constants.SP.UPDOWN_IS_TEST, false);
        Log.d(Constants.TAG, "restart :" + restart);
        Log.d(Constants.TAG, "totalCount :" + totalCount);
        if (mCbhttpRequest.isChecked()) {
            ping();
        }
        ToastUtils.showLong("准备关机中.......");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (restart < totalCount && isTest && !isStop) {
                    SPStaticUtils.put(Constants.SP.UPDOWN_RESTART, restart + 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        SystemClock.sleep(500);
                        SystemPropertiesUtils.set("sys.powerctl", "shutdown");
                    } else {
                        Intent shutdownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        shutdownIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        shutdownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(shutdownIntent);
                    }
                } else {
                    clearRestart();
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(task,delay);


    }


    /**
     * 广播
     */

    private final BroadcastReceiver stressReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.TAG, "onReceive: " + intent.getAction());
            if (StringUtils.equals(ACTION_USB_DEVICE_DETACHED, intent.getAction())) {
                /**
                 * usb上下电模式
                 */
                Log.d(Constants.TAG, "ACTION_USB_DEVICE_DETACHED: ");
                if (mCbReopenByUSB.isChecked()) {
                    shutDown(context, 4000L);
                }
                //下电 更新Adapter

            }else if (StringUtils.equals(ACTION_MEDIA_MOUNTED,intent.getAction())){
//                if (!isUSBTest){
//                    Log.d(Constants.TAG, "ACTION_MEDIA_MOUNTED: ");
//
//                }
                stressUsbAdapter.setData(customStorageManager.getStorageVolumes());
                /**
                 * USB上电 更新Adapter
                 */
            }else if (StringUtils.equals(ACTION_MEDIA_UNMOUNTED,intent.getAction())){
//                if (!isUSBTest){
//                    Log.d(Constants.TAG, "ACTION_MEDIA_UNMOUNTED: ");
//
//                }
                stressUsbAdapter.setData(customStorageManager.getStorageVolumes());
            }else if (StringUtils.equals(ACTION_MEDIA_EJECT,intent.getAction())){
                Log.d(Constants.TAG, "ACTION_MEDIA_EJECT: ");
                stressUsbAdapter.setData(customStorageManager.getStorageVolumes());
            }
        }
    };

    private void createLogDir() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "rkdebug";
        File file = new File(path);
        if (!file.isDirectory() || !file.exists()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(StressActivity.this);
            alert.setTitle("创建rkdebug目录");
            alert.setMessage("您是否要创建rkdebug目录?");
            alert.setPositiveButton("确定", (dialog, which) -> {
                boolean mkdir = file.mkdirs();
                if (!mkdir) {
                    ToastUtils.showLong("rkdebug 文件夹创建失败");
                }
            });
            alert.setNegativeButton("取消", (dialog, which) -> {
            });
            alert.show();
        }
    }


}
