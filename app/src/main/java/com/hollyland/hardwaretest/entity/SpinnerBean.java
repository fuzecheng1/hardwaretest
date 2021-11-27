package com.hollyland.hardwaretest.entity;

public class SpinnerBean<T> {

    private int id;

    private String name;

    private String desc;

    private boolean isTesting;

    private T data;

    public SpinnerBean(){

    }

    public SpinnerBean(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[" + "Name : "+name + ", " + "Info :" + desc + "]";
    }
}
