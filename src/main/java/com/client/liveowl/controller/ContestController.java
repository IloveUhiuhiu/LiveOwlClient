package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.Exam;
import com.client.liveowl.util.ExamHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.List;

public class ContestController {
    @FXML
    private Button addButton;
    @FXML
    private Button preButton;
    @FXML
    private Button nextButton;
    @FXML
    private Pane contentContainer;
    @FXML
    private VBox contentList;
    private List<Exam> exams;
    @FXML
    public void initialize() {
        exams = ExamHandler.getExamsByAccount();
        for (Exam exam : exams) {

            Pane examPane = createExamPane(exam);
            contentList.getChildren().add(examPane);

        }


    }
    private Pane createExamPane(Exam exam) {
        Pane pane = new Pane();
        pane.setLayoutX(25.0);
        pane.setLayoutY(50.0);
        pane.setPrefHeight(100.0);
        pane.setPrefWidth(600.0);
        pane.setStyle("-fx-background-radius: 15px; -fx-border-color: transparent transparent grey transparent;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(80.0);
        imageView.setFitWidth(80.0);
        imageView.setLayoutY(10.0);
        imageView.setImage(new Image("file:../images/tải xuống (2).jpg")); // Đường dẫn tới ảnh
        String title = "Tên cuộc thi: " + exam.getNameOfExam() +"\nMôn thi: " + exam.getSubjectOfExam();

        Label labelTitle = new Label(title);
        labelTitle.setLayoutX(100.0);
        labelTitle.setLayoutY(15.0);
        labelTitle.setPrefHeight(30.0);
        labelTitle.setPrefWidth(350.0);
        labelTitle.setFont(Font.font("System Bold", 12.0));
        String timeContest = exam.getStartTimeOfExam().toString();
        int pos = timeContest.indexOf("T");
        String time = timeContest.substring(0, pos);
        String day = timeContest.substring(pos + 1);
        String strDetail = "Ngày thi: " + day + " | Thời gian bắt đầu:" + time + " | " +
                "Khoảng thời gian: " + exam.getDurationOfExam() + " phút | " + "Mã : " + exam.getCodeOfExam();
        Label detailLabel = new Label(strDetail);
        detailLabel.setLayoutX(100.0);
        detailLabel.setLayoutY(50.0);
        detailLabel.setTextFill(Color.web("#948e8e"));
        Button detailButton = new Button("Chi Tiết");
        Button updateButton = new Button("Cập Nhật");
        Button deleteButton = new Button("Xóa");
        Button liveButton = new Button("Live");

        // Thiết lập vị trí cho các Button
        detailButton.setLayoutX(160);
        detailButton.setLayoutY(70);
        detailButton.setPrefWidth(80);
        detailButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        updateButton.setLayoutX(260);
        updateButton.setLayoutY(70);
        updateButton.setPrefWidth(80);
        updateButton.setStyle("-fx-background-color: #006D6E; -fx-text-fill: white;");

        deleteButton.setLayoutX(360);
        deleteButton.setLayoutY(70);
        deleteButton.setPrefWidth(80);
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

        liveButton.setLayoutX(460);
        liveButton.setLayoutY(70);
        liveButton.setPrefWidth(80);
        liveButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        pane.getChildren().addAll(imageView, labelTitle,detailLabel,detailButton,updateButton,deleteButton,liveButton);
        return pane;
    }
    public void loadContent() throws IOException {
        System.out.println("loadContent");
        Pane content = FXMLLoader.load(getClass().getResource("/views/DetailContest.fxml"));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
        System.out.println("finished");

    }
    @FXML
    private void clickAddButton() throws IOException {
        System.out.println("clickAddButton");
        loadContent();
    }
    @FXML
    private void clickPreButton() {

    }
    @FXML
    private void clickNextButton() {

    }

}
