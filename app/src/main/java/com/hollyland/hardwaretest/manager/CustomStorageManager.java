package com.hollyland.hardwaretest.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;


import java.util.List;

public class CustomStorageManager {

    private static CustomStorageManager customStorageManager = null;

    private StorageManager storageManager;

    @SuppressLint("WrongConstant")
    private CustomStorageManager(Context ctx) {
        storageManager = (StorageManager) ctx.getSystemService("storage");
    }

    public static CustomStorageManager getInstance(Context ctx) {
        if (customStorageManager == null) {
            customStorageManager = new CustomStorageManager(ctx);
            return customStorageManager;
        }
        return customStorageManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<StorageVolume> getStorageVolumes() {
        return storageManager.getStorageVolumes();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mount(String uuid) {
        try {
            Object volume = StorageManager.class.getMethod("findVolumeByUuid", String.class).invoke(storageManager, uuid);
            String id = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getId").invoke(volume);
            int type = (int) Class.forName("android.os.storage.VolumeInfo").getMethod("getType").invoke(volume);
            // public
            if (type == 0) {
                StorageManager.class.getMethod("mount", String.class).invoke(storageManager, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("U盘弹出异常信息 :" + e.getMessage());
        }
    }

    /**
     * 通过UUID查询状态
     * @param uuid
     * @return
     */
    public int getState(String uuid){
        //getState
        int state = 0;
        try {
        Object volume = StorageManager.class.getMethod("findVolumeByUuid", String.class).invoke(storageManager, uuid);
        state = (int)Class.forName("android.os.storage.VolumeInfo").getMethod("getState").invoke(volume);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("获取U盘状态异常 :" + e.getMessage());
        }
        return state;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unMount(String uuid) {
        try {
            Object volume = StorageManager.class.getMethod("findVolumeByUuid", String.class).invoke(storageManager, uuid);
            String id = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getId").invoke(volume);
            int type = (int) Class.forName("android.os.storage.VolumeInfo").getMethod("getType").invoke(volume);
            // public
            if (type == 0) {
                StorageManager.class.getMethod("unmount", String.class).invoke(storageManager, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("U盘弹出异常信息 :" + e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unMount(StorageVolume storageVolume) {
        try {
            List<Object> list = (List<Object>) StorageManager.class.getMethod("getVolumes").invoke(storageManager);
            for (int i = 0; i < list.size(); i++) {
                Object volume = list.get(i);
                if (volume != null) {
                    String uuid = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getFsUuid").invoke(volume);
                    if (StringUtils.equals(storageVolume.getUuid(), uuid)) {
                        String id = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getId").invoke(volume);
                        int type = (int) Class.forName("android.os.storage.VolumeInfo").getMethod("getType").invoke(volume);
                        // public
                        if (type == 0) {
                            StorageManager.class.getMethod("unmount", String.class).invoke(storageManager, id);
                            break;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("U盘弹出异常信息 :" + e.getMessage());
        }
    }

    //挂载固定的U盘
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mount(StorageVolume storageVolume) {
        try {

            List<Object> list = (List<Object>) StorageManager.class.getMethod("getVolumes").invoke(storageManager);
            for (int i = 0; i < list.size(); i++) {
                Object volume = list.get(i);
                if (volume != null) {
                    String uuid = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getFsUuid").invoke(volume);
                    if (StringUtils.equals(storageVolume.getUuid(), uuid)) {
                        String id = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getId").invoke(volume);
                        int type = (int) Class.forName("android.os.storage.VolumeInfo").getMethod("getType").invoke(volume);
                        // public
                        if (type == 0) {
                            StorageManager.class.getMethod("mount", String.class).invoke(storageManager, id);
                            break;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("U盘挂载异常信息 :" + e.getMessage());
        }
    }

}
