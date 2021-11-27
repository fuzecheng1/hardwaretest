package com.hollyland.hardwaretest.entity;

public class ScreenTest {


    private int sleepSuccess;

    private int sleepError;

    private int wakeUpSuccess;

    private int wakeUpError;

    public int getSleepSuccess() {
        return sleepSuccess;
    }

    public void setSleepSuccess(int sleepSuccess) {
        this.sleepSuccess = sleepSuccess;
    }

    public int getSleepError() {
        return sleepError;
    }

    public void setSleepError(int sleepError) {
        this.sleepError = sleepError;
    }

    public int getWakeUpSuccess() {
        return wakeUpSuccess;
    }

    public void setWakeUpSuccess(int wakeUpSuccess) {
        this.wakeUpSuccess = wakeUpSuccess;
    }

    public int getWakeUpError() {
        return wakeUpError;
    }

    public void setWakeUpError(int wakeUpError) {
        this.wakeUpError = wakeUpError;
    }


    @Override
    public String toString() {
        return "ScreenTest{" +
                "sleepSuccess=" + sleepSuccess +
                ", sleepError=" + sleepError +
                ", wakeUpSuccess=" + wakeUpSuccess +
                ", wakeUpError=" + wakeUpError +
                '}';
    }
}
