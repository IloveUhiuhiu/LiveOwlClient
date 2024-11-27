package com.client.liveowl.model;

public class ResultItem {
    private int STT;
    private String name;
    private String code;
    private String studentId;

    public ResultItem(int STT, String code, String studentId ,String name) {
        this.STT = STT;
        this.name = name;
        this.code = code;
        this.studentId = studentId;

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
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


}
