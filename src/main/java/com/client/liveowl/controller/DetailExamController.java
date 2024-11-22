package com.client.liveowl.controller;

import com.client.liveowl.model.Exam;
import com.client.liveowl.request.ExamRequest;
import com.client.liveowl.util.ExamHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DetailExamController {
    public static Exam examRequest = new Exam();
    @FXML
    private TextField nameOfExam;
    @FXML
    private TextField subjectOfExam;
    @FXML
    private TextField codeOfExam;
    @FXML
    private TextField durationOfExam;
    @FXML
    private DatePicker date;
    @FXML
    private TextField timeOfExam;
    @FXML
    public void initialize() {

        nameOfExam.setText(examRequest.getNameOfExam());
        subjectOfExam.setText(examRequest.getSubjectOfExam());
        durationOfExam.setText(String.valueOf(examRequest.getDurationOfExam()));
        codeOfExam.setText(examRequest.getCodeOfExam());
        date.setValue(examRequest.getStartTimeOfExam().toLocalDate());
        timeOfExam.setText(examRequest.getStartTimeOfExam().getHour() + ":" + examRequest.getStartTimeOfExam().getMinute());

    }

}
