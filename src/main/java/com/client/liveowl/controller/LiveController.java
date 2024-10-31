package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.TeacherSocket;
import com.client.liveowl.util.AlertDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveController {

    public static TeacherSocket teacherSocket;
    public static String code;
    public static boolean isLive = false;
    @FXML
    private Button exitButton;
    public static Boolean isCamera = false;
    @FXML
    private GridPane gridImage;
    public static Map<Integer,ImageView> imageViews = new HashMap<>();
    public static Map<Integer,Button> buttonViews = new HashMap<>();
    private double heightMax = 600; // Không static
    private double widthMax = 2 * heightMax; // Không static
    private int numImages = 0;


    @FXML
    public void initialize() {

        isLive = true;
        teacherSocket = new TeacherSocket();
        try {
            teacherSocket.LiveStream(code,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaFxApplication.setMaximized();
        JavaFxApplication.setResizable(false);

        gridImage.setHgap(10);
        gridImage.setVgap(10);
        gridImage.setStyle("-fx-alignment: center;");


    }


    public void updateImage(int number,Image newImage) {

        if (imageViews.containsKey(number)) {
            if (imageViews.get(number).getImage() != newImage) {
                imageViews.get(number).setImage(newImage);
            }
        } else {
            imageViews.put(number, new ImageView(newImage));
            Button buttonView = new Button("TurnOn/TurnOff");
            final int index = number;
            buttonView.setOnAction(event -> {
                System.out.println("Button " + index + " đã được nhấn");
                try {
                    TeacherSocket.clickButton(index);
                } catch (Exception e) {
                    System.out.println("Lỗi khi nhấn button: " + e.getMessage());
                }
            });
            buttonViews.put(number,buttonView);
            ++numImages;
            updateGridLayout();
        }

    }
    private void updateGridLayout() {
        gridImage.getChildren().clear(); // Xóa các node hiện tại
        int columns = Math.min(numImages, 3);
        int rows = (int) Math.ceil((double) numImages / columns);

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
    private void clickExitButton() {

        AlertDialog alertDialog = new AlertDialog("Xác nhận thoát",null,"Bạn có chắc chắn muốn thoát không?", Alert.AlertType.CONFIRMATION);
        Alert alert = alertDialog.getConfirmationDialog();
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    teacherSocket.exitLive();
                    isLive = false;
                    teacherSocket = null;
                    JavaFxApplication.changeScene("/views/Home.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    public void requestExitFromStudent(int number) {
        if (imageViews.containsKey(number)) {
            numImages--;
            imageViews.remove(number);
            buttonViews.remove(number);
            updateGridLayout();
        }
    }
}