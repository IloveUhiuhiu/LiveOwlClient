package com.client.liveowl.model;

public class Result {
    private String resultId;
    private String examId;
    private String studentId;
    private String linkKeyBoard;
    private String linkVideo;

    public Result(String resultId, String examId, String studentId, String linkKeyBoard, String linkVideo) {
        this.resultId = resultId;
        this.examId = examId;
        this.studentId = studentId;
        this.linkKeyBoard = linkKeyBoard;
        this.linkVideo = linkVideo;


    }
    public String getResultId() {
        return resultId;
    }
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
    public String getExamId() {
        return examId;
    }


    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLinkKeyBoard() {
        return linkKeyBoard;
    }

    public void setLinkKeyBoard(String linkKeyBoard) {
        this.linkKeyBoard = linkKeyBoard;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }
}
