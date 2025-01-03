
package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.Exam;
import com.client.liveowl.model.User;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ExamHandler;
import com.client.liveowl.util.UserHandler;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class HomeTeacherController
{
    @FXML
    private Button homeButton;
    @FXML
    private Button contestButton;
    @FXML
    private Button resultButton;
    @FXML
    private Button messageButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Pane contentContainer;
    @FXML
    private ImageView avt;
    @FXML
    private AnchorPane phaiPN;
    @FXML
    private VBox listContest;
    @FXML
    private Label lblDateAndTime;

    private static HomeTeacherController instance;
    private static String avatarPath = UserHandler.getDetailUser().getProfileImgLocation();

    @FXML
    public void initialize()
    {
        startClock();
        try {
            instance = this;
            setAvatarImage(avatarPath, avt, 80.0, 80.0);

            List<Exam> examList = ExamHandler.getExamsByAccount();
            List<Exam> sortedExams = examList.stream()
                    .sorted(Comparator.comparing(Exam::getStartTimeOfExam))
                    .limit(5)
                    .collect(Collectors.toList());
            listContest.getChildren().clear();
            sortedExams.forEach(exam -> {
                listContest.getChildren().add(createPane(exam));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Pane createPane(Exam exam) {
        // Tạo Pane
        Pane panel = new Pane();
        panel.setPrefHeight(44.0);
        panel.setPrefWidth(182.0);

        // Tạo Label cho tiêu đề
        Label titleLabel = new Label(exam.getNameOfExam());
        titleLabel.setLayoutX(31.0);
        titleLabel.setLayoutY(14.0);
        titleLabel.setPrefHeight(17.0);
        titleLabel.setPrefWidth(169.0);
        titleLabel.setFont(Font.font("System Bold", 12.0));

        // Tạo ImageView cho biểu tượng
        ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-choice-64.png")));
        iconView.setFitHeight(20.0);
        iconView.setFitWidth(20.0);
        iconView.setLayoutX(6.0);
        iconView.setLayoutY(14.0);
        String timeStart = exam.getStartTimeOfExam().toString();
        int pos = timeStart.indexOf("T");
        String time = timeStart.substring(pos+1);
        // Tạo Label cho thời gian
        Label timeLabel = new Label(time);
        timeLabel.setLayoutX(218.0);
        timeLabel.setLayoutY(16.0);
        timeLabel.setFont(Font.font("System Bold", 12.0));

        // Tạo ImageView cho biểu tượng thời gian
        ImageView timeIconView = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-time-32.png")));
        timeIconView.setFitHeight(20.0);
        timeIconView.setFitWidth(20.0);
        timeIconView.setLayoutX(195.0);
        timeIconView.setLayoutY(14.0);

        // Thêm các thành phần vào Pane
        panel.getChildren().addAll(titleLabel, iconView, timeLabel, timeIconView);

        return panel; // Trả về Pane đã tạo
    }


    public static HomeTeacherController getInstance()
    {
        return instance;
    }

    public void setAvatarImage(String imagePath, ImageView avt, Double with, Double height)
    {
        avatarPath = imagePath;
        if (avt != null) {
            byte[] imageBytes = Base64.getDecoder().decode(avatarPath);
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            avt.setImage(image);
            avt.setFitWidth(with);
            avt.setFitHeight(with);
            avt.setPreserveRatio(false);
            double radius = avt.getFitWidth() / 2;
            Circle circle = new Circle(radius);
            circle.setCenterX(avt.getFitWidth() / 2);
            circle.setCenterY(avt.getFitHeight() / 2);
            avt.setClip(circle);
        }
    }

    @FXML
    private void clickHomeButton() throws IOException
    {
        JavaFxApplication.changeScene("/views/HomeTeacher.fxml", "Home");
    }

    @FXML
    private void clickContestButton() throws IOException
    {
        loadContent("/views/Exam.fxml");
    }

    @FXML
    private void clickResultButton() throws IOException
    {
        loadContent("/views/Result.fxml");
    }

    @FXML
    private void clickProfileButton() throws IOException
    {
        loadContent("/views/Profile.fxml");
    }

    @FXML
    private void clickMessageButton() throws IOException
    {
        JavaFxApplication.changeScene("/views/HomeTeacher.fxml", "Home");
    }

    @FXML
    public void clickLogoutButton() throws IOException
    {
        Authentication authentication = new Authentication();
        if(authentication.logout()){
            JavaFxApplication.changeScene("/views/Login.fxml", "Login");
        }
    }

    public void loadContent(String url) throws IOException
    {
        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
    }

    public ImageView getAvt() {
        return avt;
    }

    public void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a | EEE, MMM d", Locale.ENGLISH);
        LocalDateTime now = LocalDateTime.now();
        lblDateAndTime.setText(now.format(formatter));
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime currentTime = LocalDateTime.now();
            lblDateAndTime.setText(currentTime.format(formatter));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

}
