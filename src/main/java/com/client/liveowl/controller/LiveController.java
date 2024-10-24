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

    public static List<ImageView> imageViews = new ArrayList<>(); // Không static
    public static List<Button> buttonViews = new ArrayList<>(); // Không static

    private double heightMax = 680; // Không static
    private double widthMax = 2 * heightMax; // Không static


    @FXML
    public void initialize() {
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

    public void updateGridImage() {
        int numImages = imageViews.size();
        gridImage.getChildren().clear(); // Xóa tất cả các node cũ

        int columns = Math.min(numImages, 3); // Tối đa 3 cột
        int rows = (int) Math.ceil((double) numImages / columns); // Tính số hàng
        System.out.println(rows + ", " + columns);
        for (int i = 0; i < numImages; i++) {
            ImageView imageView = imageViews.get(i);
            Button buttonView = buttonViews.get(i);
            if (rows == 1 && columns == 1) {
                imageView.setFitWidth(widthMax); // Chiều rộng cố định
                imageView.setFitHeight(heightMax); // Chiều cao cố định
            } else if (rows == 1 && columns == 2) {
                imageView.setFitWidth(2 * widthMax / 3);
                imageView.setFitHeight(2 * heightMax / 3);
            } else {
                imageView.setFitWidth(widthMax / 2);
                imageView.setFitHeight(heightMax / 2);
            }

            VBox vbox = new VBox(imageView);
            vbox.getChildren().add(buttonView);

            // Sử dụng VBox để căn giữa
            vbox.setStyle("-fx-alignment: center; -fx-pref-width: 100%; -fx-pref-height: 100%; -fx-background-color: gray; -fx-background-radius: 5px; -fx-padding: 4px;");
            int col = i % columns; // Tính cột
            int row = i / columns; // Tính hàng
            gridImage.add(vbox, col, row); // Thêm VBox vào GridPane
        }
    }

    @FXML
    private void clickExitButton() throws IOException {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }
}