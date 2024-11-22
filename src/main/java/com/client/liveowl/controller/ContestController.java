package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.Exam;
import com.client.liveowl.request.ExamRequest;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ExamHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private Pane contentContainer;
    @FXML
    private VBox contentList;
    private List<Exam> exams;
    @FXML
    public void initialize() {
        reloadContent();
    }
    public void reloadContent() {

        exams = ExamHandler.getExamsByAccount();
        for (Exam exam : exams) {

            Pane examPane = createExamPane(exam);
            contentList.getChildren().add(examPane);

        }
        addAddButton(contentContainer);
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

        detailButton.setOnAction(event -> {
                try {
                    DetailExamController.examRequest.setNameOfExam(exam.getNameOfExam());
                    DetailExamController.examRequest.setSubjectOfExam(exam.getSubjectOfExam());
                    DetailExamController.examRequest.setCodeOfExam(exam.getCodeOfExam());
                    DetailExamController.examRequest.setStartTimeOfExam(exam.getStartTimeOfExam());
                    DetailExamController.examRequest.setDurationOfExam(exam.getDurationOfExam());
                    loadContent("/views/DetailExam.fxml");
                    addBackButton(contentContainer,270,320);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        });

        updateButton.setLayoutX(260);
        updateButton.setLayoutY(70);
        updateButton.setPrefWidth(80);
        updateButton.setStyle("-fx-background-color: #006D6E; -fx-text-fill: white;");
        updateButton.setOnAction(event -> {
            try {
                UpdateExamController.updateId = exam.getExamId();
                UpdateExamController.examRequest.setNameOfExam(exam.getNameOfExam());
                UpdateExamController.examRequest.setSubjectOfExam(exam.getSubjectOfExam());
                UpdateExamController.examRequest.setStartTimeOfExam(exam.getStartTimeOfExam());
                UpdateExamController.examRequest.setDurationOfExam(exam.getDurationOfExam());
                loadContent("/views/UpdateExam.fxml");
                addBackButton(contentContainer,325,250);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        deleteButton.setLayoutX(360);
        deleteButton.setLayoutY(70);
        deleteButton.setPrefWidth(80);
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> {
            AlertDialog alertDialog = new AlertDialog("Xác nhận","","Bạn có chắc chắn xóa không?", Alert.AlertType.CONFIRMATION);
            Alert alert =  alertDialog.getConfirmationDialog();
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ExamHandler.deleteExam(exam.getExamId());
                    goBack();
                }
            });
        });


        liveButton.setLayoutX(460);
        liveButton.setLayoutY(70);
        liveButton.setPrefWidth(80);
        liveButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        liveButton.setOnAction(event -> {
            try {
                LiveController.code = exam.getCodeOfExam();
                JavaFxApplication.changeScene("/views/Live.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        pane.getChildren().addAll(imageView, labelTitle,detailLabel,detailButton,updateButton,deleteButton,liveButton);
        return pane;
    }
    public void loadContent(String url) throws IOException {
        System.out.println("loadContent");
        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
        System.out.println("finished");

    }
    private void addBackButton(Pane content,int x,int y) {
        Button backButton = new Button("Quay lại");
        backButton.setOnAction(event -> goBack());
        backButton.setLayoutX(x);
        backButton.setLayoutY(y);
        backButton.setPrefWidth(80);
        backButton.setPrefHeight(20);
        content.getChildren().add(backButton);
    }
    private void addAddButton(Pane content) {
        Button addButton = new Button("Thêm mới");
        addButton.setOnAction(event -> {
            try {
                clickAddButton();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        addButton.setLayoutX(25);
        addButton.setLayoutY(10);
        addButton.setPrefWidth(80);
        addButton.setPrefHeight(20);
        content.getChildren().add(addButton);
    }
    private void goBack() {
        contentContainer.getChildren().clear();
        contentList.getChildren().clear();
        contentContainer.getChildren().add(contentList);
        reloadContent();
    }
    @FXML
    private void clickAddButton() throws IOException {
        System.out.println("clickAddButton");
        loadContent("/views/AddExam.fxml");
        addBackButton(contentContainer,325,250);
    }


}
