package com.hollyland.hardwaretest.ui;

import static com.hollyland.hardwaretest.constants.Constants.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hollyland.hardwaretest.App;
import com.hollyland.hardwaretest.R;
import com.hollyland.hardwaretest.adapter.TestItemRecyclerViewAdpater;
import com.hollyland.hardwaretest.entity.TestItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements TestItemRecyclerViewAdpater.onClickItemListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rv)
    RecyclerView mReycyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_version)
    TextView mTversion;

    TestItemRecyclerViewAdpater testItemRecyclerViewAdpater ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int setContentView() {
        return R.layout.main_activity;
    }

    //初始化布局
    @SuppressLint("SetTextI18n")
    public void initView(){
        mTversion.setText("version :" + AppUtils.getAppVersionName());
        GridLayoutManager gridLayoutManager = null;
        Configuration configuration = this.getResources().getConfiguration();
        int orientation = configuration.orientation;
        //判断当前屏幕方向
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(App.getContext(),6);
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(App.getContext(),3);
        }
        mReycyclerView.setLayoutManager(gridLayoutManager);
        testItemRecyclerViewAdpater = new TestItemRecyclerViewAdpater();
        mReycyclerView.setAdapter(testItemRecyclerViewAdpater);
        //设置监听器
        testItemRecyclerViewAdpater.setOnClickItemListener(this);
    }


    /**
     * 初始化测试项Adapter数据
     */
    public void initData(){
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<ArrayList<TestItem>>() {

            @Override
            public ArrayList<TestItem> doInBackground() throws Throwable {
                ArrayList<TestItem> testItems = new ArrayList<>();
                String[] names = App.getContext().getResources().getStringArray(R.array.test_item_base_names);
                String[] classNames = App.getContext().getResources().getStringArray(R.array.test_item_base_class_names);
                if (names.length != classNames.length){
                    ToastUtils.showLong("testItem arrays wrong");
                    return new ArrayList<>();
                }
                for (int i = 0; i < names.length; i++) {
                    TestItem testItem = new TestItem(names[i],classNames[i]);
                    testItems.add(testItem);
                }

                return testItems;
            }

            @Override
            public void onSuccess(ArrayList<TestItem> result) {
                testItemRecyclerViewAdpater.setTestItems(result);
            }
        });
    }

    /**
     * 点击每个测试项的监听
     * @param position
     */
    @Override
    public void onClick(int position) {
        TestItem testItem = testItemRecyclerViewAdpater.getTestItem(position);
        if (testItem != null){
            Intent testItemIntent = new Intent();
            Log.d(TAG, "onClick getClassName: " + testItem.getClassName());
            testItemIntent.setClassName(App.getContext(),testItem.getClassName());
            startActivity(testItemIntent);
        }
    }
}
