package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.Exam;
import com.client.liveowl.util.AlertDialog;
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

public class ExamController {
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
        int cnt = 0;
        for (Exam exam : exams) {

            Pane examPane = createExamPane(exam,++cnt);
            contentList.getChildren().add(examPane);

        }
        addAddButton(contentContainer);
    }

    private Pane createExamPane(Exam exam,int cnt) {
        // Tạo Pane chính
        Pane mainPane = new Pane();
        mainPane.setPrefHeight(170.0);
        mainPane.setPrefWidth(610.0);
        mainPane.setStyle("-fx-background-radius: 8px; -fx-background-color: #CED1E6;");
        String title = exam.getNameOfExam();
        String timeStart = exam.getStartTimeOfExam().toString();
        int pos = timeStart.indexOf("T");
        String time = timeStart.substring(0, pos);
        String day = timeStart.substring(pos + 1);
        String code = exam.getCodeOfExam();

        // Tiêu đề
        Label labelTitle = new Label(title);
        labelTitle.setLayoutX(229.0);
        labelTitle.setLayoutY(14.0);
        labelTitle.setTextFill(Color.web("#333333"));
        labelTitle.setStyle("-fx-font-weight: bold;");
        labelTitle.setFont(Font.font("System Bold", 14.0));

        // Nhãn thời gian
        Label timeLabel = new Label(time);
        timeLabel.setLayoutX(274.0);
        timeLabel.setLayoutY(58.0);
        timeLabel.setTextFill(Color.DIMGRAY);
        timeLabel.setStyle("-fx-font-weight: bold;");
        timeLabel.setFont(Font.font("System Bold", 12.0));

        // Nhãn ngày
        Label dateLabel = new Label(day);
        dateLabel.setLayoutX(273.0);
        dateLabel.setLayoutY(91.0);
        dateLabel.setTextFill(Color.DIMGRAY);
        dateLabel.setStyle("-fx-font-weight: bold;");
        dateLabel.setFont(Font.font("System Bold", 12.0));

        // Nhãn mã
        Label codeLabel = new Label(code);
        codeLabel.setLayoutX(273.0);
        codeLabel.setLayoutY(125.0);
        codeLabel.setTextFill(Color.DIMGRAY);
        codeLabel.setStyle("-fx-font-weight: bold;");
        codeLabel.setFont(Font.font("System Bold", 12.0));

        // Hình ảnh kỳ thi
        ImageView contestImageView;
        if (cnt % 2 == 1) contestImageView =  new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-contest-96.png")));
        else contestImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-leaderboard-96.png")));
        contestImageView.setFitHeight(100.0);
        contestImageView.setFitWidth(100.0);
        contestImageView.setLayoutX(56.0);
        contestImageView.setLayoutY(31.0);

        // Hình ảnh thời gian
        ImageView timeImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/clock.png")));
        timeImageView.setFitHeight(20.0);
        timeImageView.setFitWidth(20.0);
        timeImageView.setLayoutX(236.0);
        timeImageView.setLayoutY(57.0);

        // Hình ảnh lịch
        ImageView calendarImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/calendar.png")));
        calendarImageView.setFitHeight(20.0);
        calendarImageView.setFitWidth(20.0);
        calendarImageView.setLayoutX(236.0);
        calendarImageView.setLayoutY(89.0);

        // Hình ảnh mã
        ImageView codeImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/code.png")));
        codeImageView.setFitHeight(20.0);
        codeImageView.setFitWidth(20.0);
        codeImageView.setLayoutX(236.0);
        codeImageView.setLayoutY(122.0);

        // Pane chứa các nút
        Pane buttonPane = new Pane();
        buttonPane.setLayoutX(447.0);
        buttonPane.setLayoutY(10.0);
        buttonPane.setPrefHeight(186.0);
        buttonPane.setPrefWidth(169.0);

        // Nút "Xem chi tiết"
        Button detailButton = new Button("Xem chi tiết");
        detailButton.setLayoutY(126.0);
        detailButton.setPrefWidth(145.0);
        detailButton.setStyle("-fx-font-weight: bold;-fx-background-radius: 5px;-fx-border-radius:5px; -fx-border-color: #3366FF; -fx-background-color: white;");
        detailButton.setTextFill(Color.web("#3366FF"));
        addHover(detailButton);
        detailButton.setOnAction(event -> {
            try {
                DetailExamController.examRequest.setNameOfExam(exam.getNameOfExam());
                DetailExamController.examRequest.setSubjectOfExam(exam.getSubjectOfExam());
                DetailExamController.examRequest.setCodeOfExam(exam.getCodeOfExam());
                DetailExamController.examRequest.setStartTimeOfExam(exam.getStartTimeOfExam());
                DetailExamController.examRequest.setDurationOfExam(exam.getDurationOfExam());
                loadContent("/views/DetailExam.fxml");
                addBackButton(contentContainer,250,300);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Nút "Cập nhật"

        Button updateButton = new Button("Cập nhật");
        updateButton.setLayoutY(47.0);
        updateButton.setPrefWidth(145.0);
        updateButton.setStyle("-fx-border-color: #FFD700; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-background-color: white; -fx-font-weight: bold;");
        updateButton.setTextFill(Color.web("#FFD700"));
        addHover(updateButton);
        updateButton.setOnAction(event -> {
            try {
                UpdateExamController.updateId = exam.getExamId();
                UpdateExamController.examRequest.setNameOfExam(exam.getNameOfExam());
                UpdateExamController.examRequest.setSubjectOfExam(exam.getSubjectOfExam());
                UpdateExamController.examRequest.setStartTimeOfExam(exam.getStartTimeOfExam());
                UpdateExamController.examRequest.setDurationOfExam(exam.getDurationOfExam());
                loadContent("/views/UpdateExam.fxml");
                addBackButton(contentContainer,316,300);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Nút "Xóa"
        Button deleteButton = new Button("Xóa");
        deleteButton.setLayoutY(87.0);
        deleteButton.setPrefWidth(145.0);
        deleteButton.setStyle("-fx-font-weight: bold;-fx-background-color: white; -fx-border-color: red; -fx-background-radius: 5px;;-fx-border-radius:5px;");
        deleteButton.setTextFill(Color.RED);
        addHover(deleteButton);
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

        // Nút "Màn hình giám sát"
        Button monitorButton = new Button("Màn hình giám sát");
        monitorButton.setLayoutY(10.0);
        monitorButton.setPrefWidth(145.0);
        monitorButton.setStyle("-fx-font-weight: bold;-fx-border-color: #339933; -fx-background-radius: 5px;-fx-border-radius:5px;  -fx-background-color: white;");
        monitorButton.setTextFill(Color.web("#339933"));
        addHover(monitorButton);
        monitorButton.setOnAction(event -> {
            try {
                LiveController.code = exam.getCodeOfExam();
                LiveController.examId = exam.getExamId();
                JavaFxApplication.changeScene("/views/Live.fxml", "LiveStream");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Hình ảnh cho các nút
        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-eye-24.png")));
        eyeIcon.setFitHeight(15.0);
        eyeIcon.setFitWidth(15.0);
        eyeIcon.setLayoutX(8.0);
        eyeIcon.setLayoutY(130.0);

        ImageView binIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-bin-50.png")));
        binIcon.setFitHeight(15.0);
        binIcon.setFitWidth(15.0);
        binIcon.setLayoutX(8.0);
        binIcon.setLayoutY(94.0);

        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-edit-64.png")));
        editIcon.setFitHeight(15.0);
        editIcon.setFitWidth(15.0);
        editIcon.setLayoutX(8.0);
        editIcon.setLayoutY(55.0);

        ImageView cameraIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-camera-50.png")));
        cameraIcon.setFitHeight(15.0);
        cameraIcon.setFitWidth(15.0);
        cameraIcon.setLayoutX(8.0);
        cameraIcon.setLayoutY(15.0);

        // Thêm tất cả vào buttonPane
        buttonPane.getChildren().addAll(detailButton, updateButton, deleteButton, monitorButton, eyeIcon, binIcon, editIcon, cameraIcon);

        // Thêm tất cả vào mainPane
        mainPane.getChildren().addAll(labelTitle, timeLabel, dateLabel, codeLabel, contestImageView, timeImageView, calendarImageView, codeImageView, buttonPane);

        // Thay đổi layoutX và layoutY của pane chính để tạo margin
        mainPane.setLayoutX(25.0); // Margin bên trái
        mainPane.setLayoutY(50.0); // Margin bên trên

        return mainPane;
    }

    public void loadContent(String url) throws IOException {
        System.out.println("loadContent");
        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
        System.out.println("finished");

    }
    private void addBackButton(Pane content,int x,int y) {
        Button backButton = new Button("Quay Lại");
        backButton.setLayoutX(x);
        backButton.setLayoutY(y);
        backButton.setPrefHeight(30.0);
        backButton.setPrefWidth(100.0);
        backButton.setStyle("-fx-background-color: #B22222; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        backButton.setTextFill(Color.WHITE);
        backButton.setFont(Font.font("System Bold", 12.0));
        backButton.setOnAction(event -> {
            goBack();
        });
        content.getChildren().add(backButton);
    }
    private void addAddButton(Pane content) {
        // Tạo Button
        Button addButton = new Button("Thêm");
        addButton.setLayoutX(28.0);
        addButton.setLayoutY(10.0);
        addButton.setPrefHeight(20.0);
        addButton.setPrefWidth(100.0);
        addButton.setStyle("-fx-font-weight: bold;-fx-background-color: #21a943; -fx-background-radius: 5px;-fx-border-radius: 5px;-fx-border-color: #21a943");
        addButton.setTextFill(Color.web("white"));
        addButton.setFont(Font.font("System Bold", 12.0));
        addHover(addButton);
        ImageView addImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-add-48.png")));
        addImageView.setFitHeight(20.0);
        addImageView.setFitWidth(20.0);
        addImageView.setLayoutX(35.0);
        addImageView.setLayoutY(10.0);
        addButton.setGraphic(addImageView);
        addButton.setOnAction(event -> {
            try {
                clickAddButton();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        addBackButton(contentContainer,316,300);
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
