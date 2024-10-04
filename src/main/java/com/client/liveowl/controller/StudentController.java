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
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9000;
    @FXML
    private TextField codeTextField;
    @FXML
    private Button joinButton;
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
            StudentSocket theSocket = new StudentSocket(HOSTNAME, PORT);
            theSocket.sendRequest("connect");
            System.out.println("Gửi thành công chuỗi connect đến server!");
            theSocket.sendRequest("student");
            System.out.println("Gửi role student!");
            theSocket.sendRequest(code);
            System.out.println("Gửi mã cuộc thi thành công!");
            int newPort = theSocket.receivePort();
            System.out.println("Port mới là :" + newPort);
            System.out.println("Livestream thôi!");
            theSocket.LiveStream();

//            Task<Void> task = new Task<>() {
//                @Override
//                protected Void call() throws IOException {
//                    do {
//                        try {
//                            theSocket.sendRequest("student");
//                            theSocket.sendRequest(code);
//                        } catch (Exception e) {
//
//                        }
//                        String response = theSocket.receiveResponse();
//                        if (!response.equals("success")) {
//                            AlertDialog alertDialog = new AlertDialog("Cảnh báo","Lỗi kết nối","Kết nối không thành công!",Alert.AlertType.ERROR);
//                            alertDialog.showInformationDialog();
//                        } else {
//                            break;
//                        }
//                    } while (true);
//
//                    theSocket.liveStream();
//                    return null;
//                }
//
//                @Override
//                protected void succeeded() {
//                    System.out.println("Kết nối thành công.");
//                }
//
//                @Override
//                protected void failed() {
//
//                    System.out.println("Kết nối thất bại.");
//                }
//            };
//
//            new Thread(task).start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }


}
