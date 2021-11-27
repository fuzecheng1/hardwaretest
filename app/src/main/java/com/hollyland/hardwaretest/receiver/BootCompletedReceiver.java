package com.hollyland.hardwaretest.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.SPUtils;
import com.hollyland.hardwaretest.constants.Constants;
import com.hollyland.hardwaretest.ui.stress.StressActivity;


/**
 * 开机完成广播
 */

@SuppressLint("CommitPrefEdits")
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, "BootCompletedReceiver onReceive: ");
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.equals(Intent.ACTION_BOOT_COMPLETED, action)) {//开机完成



//            //判断是否启动自动重启界面
//            String jsonStr = SPUtils.getInstance().getString(Constants.SP.SP_AUTO_REBOOT_VALUES);
//            if (!TextUtils.isEmpty(jsonStr)) {
//                AutoRebootValues autoRebootValues = GsonUtils.fromJson(jsonStr, GsonUtils.getType(AutoRebootValues.class));
//                if (autoRebootValues.isAutoReboot()) {
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                        Intent intent1 = new Intent(context, AutoRebootActivity.class);
//                        intent1.putExtra("isAutoTest", true);
//                        ActivityUtils.startActivity(intent1);
//                    }, 10000);
//                    return;
//                }
//            }
//
//            //判断是否在进行屏幕异常测试 --- 重启
//            if (SPStaticUtils.getBoolean(Constants.SP_SCREEN_EXCEPTION_TEST_STATUS, false)) {
//                new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                    Intent intent1 = new Intent(context, ScreenExceptionActivity.class);
//                    intent1.putExtra("isRebootTest", true);
//                    ActivityUtils.startActivity(intent1);
//                }, 10000);
//                return;
//            }



            //SharedPreferences sharedPreferences = context.getSharedPreferences("StressData",Context.MODE_PRIVATE);
            //SharedPreferences.Editor sharePreferenceWriter = context.getSharedPreferences("StressData",Context.MODE_PRIVATE).edit();
            int restart = SPStaticUtils.getInt(Constants.SP.UPDOWN_RESTART,0);
            int totalCount = SPStaticUtils.getInt(Constants.SP.UPDOWN_TOTAL,0);
            boolean isTest = SPStaticUtils.getBoolean(Constants.SP.UPDOWN_IS_TEST,false);
            Log.d(Constants.TAG, "BootCompletedReceiver: resart :" + restart + ", totalCount :" + totalCount +",isTest:" +isTest );
            if (restart > 0 && totalCount >= restart && isTest){

                Log.d(Constants.TAG, "onReceive: 符合条件");
                //                //继续执行开关机
                new Handler(Looper.getMainLooper()).postDelayed(() ->{
                    Intent intent1 = new Intent(context, StressActivity.class);
                    intent1.putExtra("isRestartTest",true);
                    Log.d(Constants.TAG, ": StressActivity" );
                    ActivityUtils.startActivity(intent1);
                },6000);
                return;
            }


            PackageManager pm = context.getApplicationContext().getPackageManager();
            ComponentName name = new ComponentName(context.getApplicationContext(), BootCompletedReceiver.class);
            pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }
}
