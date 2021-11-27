package com.hollyland.hardwaretest.entity;

public class WifiTestItem {

    //启动数
    private int enableNum;

    //连接数
    private int connectedNum;

     //上网数
    private int sufNetNum;

    //断开连接数
    private int disableNum;

    //总测试次数
    private int currentNum;


    public int getEnableNum() {
        return enableNum;
    }

    public void setEnableNum(int enableNum) {
        this.enableNum = enableNum;
    }

    public int getConnectedNum() {
        return connectedNum;
    }

    public void setConnectedNum(int connectedNum) {
        this.connectedNum = connectedNum;
    }

    public int getSufNetNum() {
        return sufNetNum;
    }

    public void setSufNetNum(int sufNetNum) {
        this.sufNetNum = sufNetNum;
    }

    public int getDisableNum() {
        return disableNum;
    }

    public void setDisableNum(int disableNum) {
        this.disableNum = disableNum;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }
}
