package com.hollyland.hardwaretest.entity;

public class StressValueBean {


    public final static int BLUETOOTH_STRESS = 0;

    public final static int WIFI_STRESS = 1;

    public final static int SCREEN_STRESS = 2;

    public final static int USB_STRESS = 3;

    public final static int RESTART_STRESS = 4;




    /**
     * 测试类型
     */
    private int testType;

    /**
     * 总次数
     */
    private int totalCount;

    /**
     * 间隔时间
     */
    private int intervalTime;


    /***
     * 选项
     */
    private SpinnerBean spinnerBean;

    /**
     * 蓝牙连接
     */

    private BlueToothTestBean blueToothTestBean;


    /**
     * Wi-fi连接
     */
    private WifiTestBean wifiTestBean;

    /**
     * 成功次数
     */
    private int success;

    /**
     * 失败次数
     */
    private int error;


    /**
     * 信息数据
     */

    private Object data;

    /**
     * 是否在测试
     */
    private boolean inTesting;


    public StressValueBean(int testType, int totalCount, int intervalTime, boolean inTesting) {
        this.testType = testType;
        this.totalCount = totalCount;
        this.intervalTime = intervalTime;
        this.inTesting = inTesting;
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public boolean isInTesting() {
        return inTesting;
    }

    public void setInTesting(boolean inTesting) {
        this.inTesting = inTesting;
    }


    public SpinnerBean getSpinnerBean() {
        return spinnerBean;
    }

    public void setSpinnerBean(SpinnerBean spinnerBean) {
        this.spinnerBean = spinnerBean;
    }


    public BlueToothTestBean getBtBean() {
        return blueToothTestBean;
    }

    public WifiTestBean getWifiBean() {
        return wifiTestBean;
    }

    public void setWifiBean(WifiTestBean wifiTestBean) {
        this.wifiTestBean = wifiTestBean;
    }

    public void setBtBean(BlueToothTestBean blueToothTestBean) {
        this.blueToothTestBean = blueToothTestBean;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StressValueBean{" +
                "testType=" + testType +
                ", totalCount=" + totalCount +
                ", intervalTime=" + intervalTime +
                ", spinnerBean=" + spinnerBean +
                ", blueToothTestBean=" + blueToothTestBean +
                ", wifiTestBean=" + wifiTestBean +
                ", success=" + success +
                ", error=" + error +
                ", data=" + data +
                ", inTesting=" + inTesting +
                '}';
    }
}
