package com.client.liveowl.controller.teacher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class LiveController {
    @FXML
    private Button addImage;

    @FXML
    private GridPane gridImage;

    private List<ImageView> imageViews = new ArrayList<>();


    @FXML
    public void initialize() {
        addImage.setOnAction(event -> handleAddImage());
        gridImage.setHgap(10);
        gridImage.setVgap(10);

    }
    @FXML
    public void handleAddImage() {
        Image image = new Image(getClass().getResourceAsStream("/images/bg.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(350); // Kích thước ảnh
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);

        imageViews.add(imageView);
        updateGridImage();
    }

    public void updateGridImage() {
        gridImage.getChildren().clear(); // Xóa tất cả các node cũ
        for (int i = 0; i < imageViews.size(); i++) {
            Button button = new Button("Button " + i);


            // Sử dụng VBox để chứa ImageView và Button
            VBox vbox = new VBox(imageViews.get(i), button);
            vbox.setSpacing(5); // Khoảng cách giữa ảnh và nút
            int col = i % 2; // Tính cột (0 hoặc 1)
            int row = i / 2; // Tính hàng
            gridImage.add(vbox, col, row); // Thêm ImageView vào GridPane
        }
    }

}
