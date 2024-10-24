package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.TeacherSocket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LiveController {

    public static String code;
    @FXML
    private Button exitButton;
    public static Boolean isCamera = false;
    @FXML
    private GridPane gridImage;
    private static int MAX = 10;
    private static int numImages = 0;
    public static List<ImageView> imageViews = new ArrayList<>(); // Không static
    public static List<Button> buttonViews = new ArrayList<>();
    private double heightMax = 600; // Không static
    private double widthMax = 2 * heightMax; // Không static


    @FXML
    public void initialize() {
        for (int i = 0; i < MAX; i++) {
            ImageView imageView = new ImageView();
            imageViews.add(imageView);

            Button buttonView = new Button("TurnOn/TurnOff");
            final int index = i;
            buttonView.setOnAction(event -> {
                System.out.println("Button " + index + " đã được nhấn");
                try {
                    TeacherSocket.clickButton(index);
                } catch (Exception e) {
                    System.out.println("Lỗi khi nhấn button: " + e.getMessage());
                }
            });
            buttonViews.add(buttonView);
        }
        TeacherSocket theSocket = new TeacherSocket();
        try {
            theSocket.LiveStream(code,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaFxApplication.setMaximized();

        gridImage.setHgap(10);
        gridImage.setVgap(10);
        gridImage.setStyle("-fx-alignment: center;");


    }


    public void updateImage(int number,Image newImage) {

        if (number < numImages) {
            if (imageViews.get(number).getImage() != newImage) {
                imageViews.get(number).setImage(newImage);
            }
        } else {
            imageViews.get(number).setImage(newImage);
            numImages++;
            updateGridLayout();
        }

    }
    private void updateGridLayout() {
        gridImage.getChildren().clear(); // Xóa các node hiện tại
        int columns = Math.min(numImages, 3);
        int rows = (int) Math.ceil((double) numImages / columns);

        for (int i = 0; i < numImages; i++) {
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
    private void clickExitButton() throws IOException {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }
}