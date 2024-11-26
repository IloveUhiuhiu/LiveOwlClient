package com.client.liveowl.model;

public class ResultItem {
    private int STT;
    private String name;
    public ResultItem(int STT, String name) {
        this.STT = STT;
        this.name = name;
    }
    public int getSTT() {
        return STT;
    }
    public void setSTT(int STT) {
        this.STT = STT;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
