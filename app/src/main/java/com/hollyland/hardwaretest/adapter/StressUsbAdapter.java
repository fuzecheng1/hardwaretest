package com.hollyland.hardwaretest.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;


import com.hollyland.hardwaretest.App;
import com.hollyland.hardwaretest.R;

import java.util.ArrayList;
import java.util.List;

public class StressUsbAdapter extends BaseAdapter {

    List<StorageVolume> storageVolumes = new ArrayList<>();

    List<StorageVolume> checkedStorageVolumes = new ArrayList<>();

    @Override
    public int getCount() {
        return storageVolumes.size();
    }

    @Override
    public Object getItem(int i) {
        return storageVolumes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 初始化checkbox状态
     */
    public void initCheckBox() {

    }

    /***
     * 设置数据
     * @param storageVolumes
     */

    public void setData(List<StorageVolume> storageVolumes) {
        if (storageVolumes.size() != 0) {
            this.storageVolumes.clear();
            this.storageVolumes.addAll(storageVolumes);
            notifyDataSetChanged();
        }
    }


    /**
     * 获取被选中的U盘设备
     */
    public List<StorageVolume> getCheckedVolumes() {
        return checkedStorageVolumes;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view;
        StorageVolume storageVolume = (StorageVolume) getItem(i);
        if (convertView == null) {
            view = LayoutInflater.from(App.getContext()).inflate(R.layout.item_autoreboot_usb, null);
            viewHolder = new ViewHolder();
            viewHolder.text = view.findViewById(R.id.tvTitle);
            viewHolder.checkBox = view.findViewById(R.id.cbCheckBox);
            viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    checkedStorageVolumes.add(storageVolume);
                } else {
                    checkedStorageVolumes.remove(storageVolume);
                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.text.setText("uuid:"
                + (storageVolume.getUuid() == null ? storageVolume.toString()
                : storageVolume.getUuid()) + ",状态:" + storageVolume.getState());
        return view;
    }

    public static class ViewHolder {

        public TextView text;

        public CheckBox checkBox;

    }
}
