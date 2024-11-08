package com.client.liveowl.controller;

import com.client.liveowl.StudentSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    @FXML
    public void initialize() {
        try {

            StudentController.theSocket.LiveStream(this);
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
            StudentSocket.isLive = true;



        });

        buttonCamera.setOnAction(e -> {
            StudentSocket.captureFromCamera ^= 1;
        });
    }

    public void updateImage(Image img) {
        if (StudentSocket.captureFromCamera == 0) {
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
