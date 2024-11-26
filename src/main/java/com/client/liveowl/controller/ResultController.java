package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.KeyLogger.GetFile;
import com.client.liveowl.model.Exam;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ExamHandler;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultController {

@FXML
private GridPane gridPane;

@FXML
private Button prevButton;

@FXML
private Button nextButton;
@FXML
private Pane contentContainer;
@FXML
private VBox contentList;
private List<String> accountIds;
private int currentPage = 0;
private final int itemsPerPage = 12;
private List<Exam> exams;
public void initialize() {
    reLoadContent();
}
private void reLoadContent() {
    exams = ExamHandler.getExamsByAccount();
    for (Exam exam : exams) {
        Pane resultPane = createPane(exam);
        contentList.getChildren().add(resultPane);
    }
}
public Pane createPane(Exam exam) {
        Pane pane = new Pane();
        pane.setPrefSize(611, 79);
        pane.setStyle("-fx-background-radius: 8px; -fx-background-color: #CED1E6;");

        Label titleLabel = new Label(exam.getNameOfExam());
        titleLabel.setLayoutX(229);
        titleLabel.setLayoutY(14);
        titleLabel.setFont(Font.font("System Bold", 14));
        titleLabel.setTextFill(javafx.scene.paint.Color.web("#333333"));

        Button resultButton = new Button("Xem Kết Quả");
        resultButton.setLayoutX(440);
        resultButton.setLayoutY(32);
        resultButton.setPrefSize(145, 30);
        resultButton.setStyle("-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #853E93; -fx-background-color: white;");
        resultButton.setTextFill(javafx.scene.paint.Color.web("#853e93"));
        addHover(resultButton);
        resultButton.setFont(Font.font("System Bold", 12));
        resultButton.setOnAction(event -> {
            try {
                loadContent("/views/ListStudents.fxml");
                addBackButton(contentContainer,5,5);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        String timeStart = exam.getStartTimeOfExam().toString();
        int pos = timeStart.indexOf("T");
        String time = timeStart.substring(0, pos);
        String day = timeStart.substring(pos + 1);
        Label timeLabel = new Label(time);
        timeLabel.setLayoutX(248);
        timeLabel.setLayoutY(46);
        timeLabel.setFont(Font.font("System Bold", 12));
        timeLabel.setTextFill(javafx.scene.paint.Color.DIMGRAY);

        Label dateLabel = new Label(day);
        dateLabel.setLayoutX(354);
        dateLabel.setLayoutY(45);
        dateLabel.setFont(Font.font("System Bold", 12));
        dateLabel.setTextFill(javafx.scene.paint.Color.DIMGRAY);

        ImageView calendarImage = new ImageView(new Image(getClass().getResourceAsStream("/images/calendar.png")));
        calendarImage.setFitHeight(20);
        calendarImage.setFitWidth(20);
        calendarImage.setLayoutX(326);
        calendarImage.setLayoutY(43);

        ImageView contestImage = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-contest-96.png")));
        contestImage.setFitHeight(70);
        contestImage.setFitWidth(70);
        contestImage.setLayoutX(105);
        contestImage.setLayoutY(3);

        ImageView resultsImage = new ImageView(new Image(getClass().getResourceAsStream("/images/results.png")));
        resultsImage.setFitHeight(20);
        resultsImage.setFitWidth(20);
        resultsImage.setLayoutX(449);
        resultsImage.setLayoutY(35);

        ImageView clockImage = new ImageView(new Image(getClass().getResourceAsStream("/images/clock.png")));
        clockImage.setFitHeight(20);
        clockImage.setFitWidth(20);
        clockImage.setLayoutX(223);
        clockImage.setLayoutY(43);

        pane.getChildren().addAll(titleLabel, resultButton, timeLabel, dateLabel, calendarImage, contestImage, resultsImage, clockImage);

        return pane;

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

@FXML
private void onPrevPage() {
    if (currentPage > 0) {
        currentPage--;
        //updateGrid();
    }
}

@FXML
private void onNextPage() {
    if ((currentPage + 1) * itemsPerPage < accountIds.size()) {
        currentPage++;
        //updateGrid();
    }
}
private void goBack() {
    contentContainer.getChildren().clear();
    contentList.getChildren().clear();
    contentContainer.getChildren().add(contentList);
    reLoadContent();
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
public void loadContent(String url) throws IOException {
        //System.out.println("loadContent");
        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
        //System.out.println("finished");

}


}


