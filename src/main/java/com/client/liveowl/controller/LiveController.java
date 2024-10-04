package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class LiveController {
    @FXML
    private Button addImage;

    @FXML
    private GridPane gridImage;

    private List<ImageView> imageViews = new ArrayList<>();
    private List<Button> buttonViews = new ArrayList<>();

    private double heightMax = 680;
    private double widthMax = 2*heightMax;

    @FXML
    public void initialize() {
        JavaFxApplication.setMaximized();
        addImage.setOnAction(event -> handleAddImage());
        gridImage.setHgap(10);
        gridImage.setVgap(10);
        gridImage.setStyle("-fx-alignment: center;");
        //gridImage.setPrefSize(widthGrid, heightGrid);
    }
    @FXML
    public void handleAddImage() {
        Image image = new Image(getClass().getResourceAsStream("/images/bg.png"));
        ImageView imageView = new ImageView(image);
        Button buttonView = new Button("TurnOn/TurnOff");
        imageView.setPreserveRatio(true);
        imageViews.add(imageView);
        buttonViews.add(buttonView);
        updateGridImage(imageViews.size());
    }

    public void updateGridImage(int numImages) {
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
                imageView.setFitWidth(2*widthMax/3);
                imageView.setFitHeight(2*heightMax/3);
            } else {
                imageView.setFitWidth(widthMax/2);
                imageView.setFitHeight(heightMax/2);
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

}
