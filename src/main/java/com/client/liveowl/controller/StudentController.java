package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ExamHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.io.IOException;

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
        return;
    }
    if (ExamHandler.getExamByCode(code) == null) {
        AlertDialog alertDialog = new AlertDialog("Cảnh báo","Mã không hợp lệ","Mã bạn vừa nhập không hợp lệ!",Alert.AlertType.ERROR);
        alertDialog.showInformationDialog();
        return;
    }
    System.out.println("Mã đã nhập: " + code);
    try {
        Authentication.setCode(code);
        JavaFxApplication.changeScene("/views/JoinExam.fxml");
    } catch (IOException e) {
        e.printStackTrace();
    }


}

}