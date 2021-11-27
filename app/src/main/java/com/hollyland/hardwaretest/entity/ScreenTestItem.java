package com.hollyland.hardwaretest.entity;

public class ScreenTestItem {


    private int sleepSuccessNum;

    private int sleepErrorNum;

    private int wakeUpSuccessNum;

    private int wakeUpErrorNum;

    public int getSleepSuccessNum() {
        return sleepSuccessNum;
    }

    public void setSleepSuccessNum(int sleepSuccessNum) {
        this.sleepSuccessNum = sleepSuccessNum;
    }

    public int getSleepErrorNum() {
        return sleepErrorNum;
    }

    public void setSleepErrorNum(int sleepErrorNum) {
        this.sleepErrorNum = sleepErrorNum;
    }

    public int getWakeUpSuccessNum() {
        return wakeUpSuccessNum;
    }

    public void setWakeUpSuccessNum(int wakeUpSuccessNum) {
        this.wakeUpSuccessNum = wakeUpSuccessNum;
    }

    public int getWakeUpErrorNum() {
        return wakeUpErrorNum;
    }

    public void setWakeUpErrorNum(int wakeUpErrorNum) {
        this.wakeUpErrorNum = wakeUpErrorNum;
    }


    @Override
    public String toString() {
        return "ScreenTest{" +
                "sleepSuccess=" + sleepSuccessNum +
                ", sleepError=" + sleepErrorNum +
                ", wakeUpSuccess=" + wakeUpSuccessNum +
                ", wakeUpError=" + wakeUpErrorNum +
                '}';
    }
}
