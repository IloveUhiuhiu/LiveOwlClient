package com.client.liveowl.controller.student;

import com.client.liveowl.ClientSocket;
import com.client.liveowl.StudentSocket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.*;
import java.io.IOException;

public class HomeController {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9876;
    @FXML
    private TextField codeTextField;
    @FXML
    private Button joinButton;
    @FXML
    public void initialize() {
        joinButton.setOnAction(event -> handleJoinButtonClick());
    }
    @FXML
    private void showAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông Báo");
        alert.setContentText(contentText);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    private void handleJoinButtonClick() {
        String code = codeTextField.getText();
        if (code == null || code.isEmpty()) {
            Platform.runLater(() ->showAlert("Bạn chưa nhập mã!!!"));
        }
        System.out.println("Mã đã nhập: " + code);
        try {
            StudentSocket theSocket = new StudentSocket(HOSTNAME, PORT);
            System.out.println("Chấp nhận kết nối");
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    do {
                        try {
                            theSocket.sendRequest("student");
                            theSocket.sendRequest(code);
                        } catch (Exception e) {
                            showAlert(e.getMessage());
                        }
                        String response = theSocket.receiveResponse();
                        if (!response.equals("success")) {
                            Platform.runLater(() -> showAlert("Mã của bạn không hợp lệ!!!"));
                        } else {
                            break;
                        }
                    } while (true);

                    theSocket.liveStream();
                    return null;
                }

                @Override
                protected void succeeded() {
                    System.out.println("Kết nối thành công.");
                }

                @Override
                protected void failed() {

                    System.out.println("Kết nối thất bại.");
                }
            };

            new Thread(task).start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }


}
