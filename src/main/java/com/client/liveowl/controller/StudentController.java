package com.client.liveowl.controller;

import com.client.liveowl.StudentSocket;
import com.client.liveowl.util.AlertDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;


import java.io.IOException;
import java.net.DatagramPacket;

public class StudentController {
    @FXML
    private TextField codeTextField;
    @FXML
    private Button joinButton;
    public static StudentSocket theSocket = null;

    @FXML
    public void initialize() {
        joinButton.setOnAction(event -> handleJoinButtonClick());
    }
    private void handleJoinButtonClick() {
        String code = codeTextField.getText();
        if (code == null || code.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog("Cảnh báo","Thiếu mã","Bạn chưa nhập mã cuộc thi!",Alert.AlertType.ERROR);
            alertDialog.showInformationDialog();
        }
        System.out.println("Mã đã nhập: " + code);
        try {
            theSocket = new StudentSocket();
            theSocket.LiveStream(code);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }




}
