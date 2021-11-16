package com.hollyland.hardwaretest.entity;

public class EventMessage<T> {

    private int code;
    private int state;
    private T data;

    public EventMessage(int code) {
        this.code = code;
    }

    public EventMessage(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public EventMessage(int code, int state, T data) {
        this.code = code;
        this.state = state;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventMessage{" +
                "code=" + code +
                ", state=" + state +
                ", data=" + data +
                '}';
    }
}
