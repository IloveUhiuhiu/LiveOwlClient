package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ExamHandler;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


import java.io.IOException;

public class HomeStudentController extends HomeTeacherController {
@FXML
private TextField codeTextField;
@FXML
private Button joinButton;
@FXML
private ImageView avt;
@FXML
private Pane main;
@FXML
private Pane header;

private static HomeStudentController instance;
private static String avatarPath = UserHandler.getDetailUser().getProfileImgLocation();


@FXML
public void initialize() {
    startClock();
    instance = this;
    avatarPath = UserHandler.getDetailUser().getProfileImgLocation();
    setAvatarImage(avatarPath, avt, 70.0, 70.0);
    joinButton.setOnAction(event -> handleJoinButtonClick());
    avt.setOnMouseEntered(event -> avt.setCursor(Cursor.HAND));
    avt.setOnMouseClicked(event -> {
        try {
            avtClick();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
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
        JavaFxApplication.changeScene("/views/JoinExam.fxml", "JoinExam");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void avtClick() throws Exception {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profile.fxml"));
        Parent newContent = loader.load();
        main.getChildren().setAll(newContent);
        AnchorPane.setTopAnchor(newContent, null);
        AnchorPane.setBottomAnchor(newContent, null);
        AnchorPane.setLeftAnchor(newContent, null);
        AnchorPane.setRightAnchor(newContent, null);
        newContent.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double xOffset = (main.getWidth() - newBounds.getWidth()) / 2;
            double yOffset = (main.getHeight() - newBounds.getHeight()) / 2;
            newContent.setTranslateX(xOffset);
            newContent.setTranslateY(yOffset);
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public static HomeStudentController getInstance()
{
    return instance;
}
public ImageView getAvt() {
    return avt;
}

}