package com.client.liveowl.controller;

import com.client.liveowl.request.ExamRequest;
import com.client.liveowl.util.ExamHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UpdateExamController {
    public static ExamRequest examRequest = new ExamRequest();
    public static String updateId;
    @FXML
    private Label lblSuccess;
    @FXML
    private Label lblFailed;
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
        nameOfExam.setText(examRequest.getNameOfExam());
        subjectOfExam.setText(examRequest.getSubjectOfExam());
        durationOfExam.setText(String.valueOf(examRequest.getDurationOfExam()));
        date.setValue(examRequest.getStartTimeOfExam().toLocalDate());
        hour.setValue(examRequest.getStartTimeOfExam().getHour());
        minutes.setValue(examRequest.getStartTimeOfExam().getMinute());
        addHover(addExam);
        addExam.setOnAction(e -> {
            LocalDate selectedDate = date.getValue();
            Integer selectedHour = hour.getValue();
            Integer selectedMinute = minutes.getValue();
            LocalTime time = LocalTime.of(selectedHour, selectedMinute);
            LocalDateTime startTimeOfExam = LocalDateTime.of(selectedDate, time);
            if (ExamHandler.updateExam(new ExamRequest(nameOfExam.getText(),subjectOfExam.getText(),startTimeOfExam,Integer.parseInt(durationOfExam.getText())),updateId)) {
                lblSuccess.setText("Cập nhật thành công");
                showAndHideLabel(lblSuccess);
            } else {
                lblFailed.setText("Cập nhật thất bại");
                showAndHideLabel(lblFailed);
            }
        });

    }
    private void showAndHideLabel(Label label)
    {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1.5), event -> label.setVisible(false))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
    private void addHover(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            button.setCursor(javafx.scene.Cursor.HAND);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });
    }

}
