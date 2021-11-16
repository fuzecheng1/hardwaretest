package com.hollyland.hardwaretest.entity;

public class TestItem {


    private String name;

    private String className;

    public TestItem(){

    }

    public TestItem(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
