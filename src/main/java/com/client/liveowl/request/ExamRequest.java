package com.client.liveowl.request;

import java.time.LocalDateTime;

public class ExamRequest {
    private String nameOfExam;

    private String subjectOfExam;

    private LocalDateTime startTimeOfExam;

    private int durationOfExam;
    public ExamRequest() {

    }
    public ExamRequest(String nameOfExam, String subjectOfExam, LocalDateTime startTimeOfExam, int durationOfExam) {
        this.nameOfExam = nameOfExam;
        this.subjectOfExam = subjectOfExam;
        this.startTimeOfExam = startTimeOfExam;
        this.durationOfExam = durationOfExam;
    }

    public void setNameOfExam(String nameOfExam) {
        this.nameOfExam = nameOfExam;
    }
    public void setSubjectOfExam(String subjectOfExam) {
        this.subjectOfExam = subjectOfExam;
    }
    public void setStartTimeOfExam(LocalDateTime startTimeOfExam) {
        this.startTimeOfExam = startTimeOfExam;
    }
    public void setDurationOfExam(int durationOfExam) {
        this.durationOfExam = durationOfExam;
    }
    public String getNameOfExam() {
        return nameOfExam;
    }
    public String getSubjectOfExam() {
        return subjectOfExam;
    }
    public LocalDateTime getStartTimeOfExam() {
        return startTimeOfExam;
    }
    public int getDurationOfExam() {
        return durationOfExam;
    }

}

