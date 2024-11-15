package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.TeacherSocket;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.AlertDialog;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LiveController {

    public static TeacherSocket teacherSocket;
    public static String code;
    public static boolean isLive = false;
    @FXML
    private Button exitButton;
    @FXML
    private GridPane gridImage;
    public static Map<Integer,ImageView> imageViews = new HashMap<>();
    public static Map<Integer,Button> buttonViews = new HashMap<>();
    private double heightMax = 450;
    private double widthMax = 800;
    private int numImages = 0;
    private AnimationTimer animationTimer;
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 16_666_667;


    @FXML
    public void initialize() {

        isLive = true;
        teacherSocket = new TeacherSocket();
        try {
            teacherSocket.LiveStream(code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaFxApplication.setMaximized();
        JavaFxApplication.setResizable(false);

        gridImage.setHgap(10);
        gridImage.setVgap(10);
        gridImage.setStyle("-fx-alignment: center;");

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ImageData imageData = TeacherSocket.sendList.poll();
                if (imageData != null) {
                    updateImage(imageData.getClientId(), imageData.getImage());
                }
                int clientIdExit = TeacherSocket.getExit();
                if( clientIdExit >= 0) {
                    handleStudentExitRequest(clientIdExit);
                    TeacherSocket.setExit(-1);
                }
            }

        };
        animationTimer.start();
    }
    public void updateImage(int number,Image newImage) {
        Platform.runLater(() -> {
            if (imageViews.containsKey(number)) {
                if (imageViews.get(number).getImage() != newImage) {
                    imageViews.get(number).setImage(newImage);
                }
            } else {
                ImageView currentImage = new ImageView(newImage);

                imageViews.put(number, currentImage);
                Button buttonView = new Button("TurnOn/TurnOff");
                final int index = number;
                buttonView.setOnAction(event -> {
                    System.out.println("Button " + index + " đã được nhấn");
                    try {
                        TeacherSocket.clickBtnCamera(index);
                    } catch (Exception e) {
                        System.out.println("Lỗi khi nhấn button: " + e.getMessage());
                    }
                });
                buttonViews.put(number, buttonView);
                ++numImages;
                updateGridLayout();
            }
        });

    }
    private void updateGridLayout() {
        gridImage.getChildren().clear(); // Xóa các node hiện tại
        int columns = Math.min(numImages, 3);
        int rows = (int) Math.ceil((double) numImages / columns);
        exitButton.setText(numImages + ", " + rows + ", " + columns);
        for (int i: imageViews.keySet()) {
            ImageView imageView = imageViews.get(i);
            Button buttonView = buttonViews.get(i);
            // Điều chỉnh kích thước ảnh
            adjustImageSize(imageView, rows, columns);

            VBox vbox = new VBox(imageView, buttonView);
            vbox.setStyle("-fx-alignment: center; -fx-pref-width: 100%; -fx-pref-height: 100%; -fx-background-color: gray; -fx-background-radius: 5px; -fx-padding: 4px;");

            int col = i % columns;
            int row = i / columns;
            gridImage.add(vbox, col, row);
        }
    }

    private void adjustImageSize(ImageView imageView, int rows, int columns) {
        if (rows == 1 && columns == 1) {
            imageView.setFitWidth(widthMax);
            imageView.setFitHeight(heightMax);
        } else if (rows == 1 && columns == 2) {
            imageView.setFitWidth(widthMax / 2);
            imageView.setFitHeight(2 * heightMax / 3);
        } else {
            imageView.setFitWidth(widthMax / 3);
            imageView.setFitHeight(heightMax / 2);
        }
    }
    @FXML
    private void handleExitButtonClick() {

        AlertDialog alertDialog = new AlertDialog("Xác nhận thoát",null,"Bạn có chắc chắn muốn thoát không?", Alert.AlertType.CONFIRMATION);
        Alert alert = alertDialog.getConfirmationDialog();
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    teacherSocket.clickBtnExit();
                    isLive = false;
                    teacherSocket = null;
                    if (animationTimer != null) {
                        animationTimer.stop();
                    }
                    JavaFxApplication.changeScene("/views/Home.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    public void handleStudentExitRequest(int number) {
        if (imageViews.containsKey(number)) {
            numImages--;
            imageViews.remove(number);
            buttonViews.remove(number);
            updateGridLayout();
        }
    }
}

