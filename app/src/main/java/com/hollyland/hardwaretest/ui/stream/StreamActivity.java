package com.hollyland.hardwaretest.ui.stream;

import static com.hollyland.hardwaretest.constants.Constants.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hollyland.hardwaretest.R;
import com.hollyland.hardwaretest.StreamTask;
import com.hollyland.hardwaretest.ui.BaseActivity;
import com.hollyland.hardwaretest.utils.SystemPropertiesUtils;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class StreamActivity extends BaseActivity {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.type_stream_reconnect)
    RadioButton mRbStreamReconnect;

//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.btn_start)
//    Button mBtnStart;
//
//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.btn_stop)
//    Button mBtnStop;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_total_count)
    EditText mEdTotalCount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_current_time)
    EditText mEdCurrentTime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_errror)
    TextView mTvError;

    private StreamTask streamTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @OnCheckedChanged({R.id.type_stream_reconnect})
    public void onCheckedChanged(CompoundButton compoundButton , boolean isChecked){
        switch (compoundButton.getId()){
            case R.id.type_stream_reconnect:
                if (isChecked){

                }
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_start,R.id.btn_stop,R.id.btn_clear_count,R.id.tv_headerview_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_start:
                String value = SystemPropertiesUtils.get("ro.sf.lcd_density","");
                Log.d(TAG, "value: " + value);
                streamTask = new StreamTask();
                ThreadUtils.executeBySingle(streamTask);
                break;
            case R.id.btn_stop:
                ThreadUtils.cancel(streamTask);
                break;
            case R.id.btn_clear_count:
                mEdCurrentTime.setText("");
                mEdTotalCount.setText("");
                mTvError.setText("");
                break;
            case R.id.tv_headerview_back:
                ActivityUtils.finishActivity(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_auto_stream;
    }
}
