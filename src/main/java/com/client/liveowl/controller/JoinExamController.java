package com.client.liveowl.controller;
import com.client.liveowl.socket.StudentSocket;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;


public class JoinExamController {
@FXML
private ImageView image;
@FXML
private Button buttonCamera;
@FXML
private Button buttonRequest;
@FXML
private TextField isActive;
private AnimationTimer animationTimer;

@FXML
public void initialize() {
    try {
        StudentController.theSocket.LiveStream();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    buttonRequest.setOnAction(e -> {
        image.setImage(null);
        isActive.setText("...Bạn đang được giám sát");
        isActive.setStyle("-fx-text-fill: #00FF00;" + // Màu chữ xanh lá cây
                "-fx-background-color: #FFFFFF;" + // Màu nền trắng
                "-fx-border-color: #FFFFFF;" + // Màu viền trắng
                "-fx-border-width: 2px;" + // Độ dày viền
                "-fx-alignment: CENTER;"); // Căn giữa văn bả
        StudentSocket.updateLive();
    });
    buttonCamera.setOnAction(e -> {
        StudentSocket.updateCamera();
    });
    animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            Image image = StudentSocket.cache.poll();
            if (image != null) {
                updateImage(image);
            }
        }
    };
    animationTimer.start();
}
public void updateImage(Image img) {
    if (StudentSocket.getCamera() == 0) {
        image.setImage(null);
    } else {
        if (image == null) {
            image = new ImageView(img);
        } else {
            image.setImage(img);
        }
    }

}
}
