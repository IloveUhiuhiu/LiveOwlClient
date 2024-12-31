package com.client.liveowl.controller;

import com.client.liveowl.request.ExamRequest;
import com.client.liveowl.util.ExamHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddExamController {
    @FXML
    private TextField nameOfExam;
    @FXML
    private TextField subjectOfExam;

    @FXML
    private TextField durationOfExam;
    @FXML
    private Button addExam;

    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<Integer> hour;
    @FXML
    private ComboBox<Integer> minutes;
    @FXML
    public void initialize() {
        for (int i = 0; i < 24; i++) {
            hour.getItems().add(i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.getItems().add(i);
        }
        addExam.setOnAction(e -> {
            LocalDate selectedDate = date.getValue();
            Integer selectedHour = hour.getValue();
            Integer selectedMinute = minutes.getValue();
            LocalTime time = LocalTime.of(selectedHour, selectedMinute);
            LocalDateTime startTimeOfExam = LocalDateTime.of(selectedDate, time);
            ExamHandler.addExam(new ExamRequest(nameOfExam.getText(),subjectOfExam.getText(),startTimeOfExam,Integer.parseInt(durationOfExam.getText())));
        });



    }



}
