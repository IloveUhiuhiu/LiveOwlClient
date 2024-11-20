package com.client.liveowl.controller;

import com.client.liveowl.model.User;
import com.client.liveowl.util.UdpHandler;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Date;

public class ProfileController {

@FXML
private Button btnThayDoi;
@FXML
private Button btnTroVe;
@FXML
private Label lblName;
@FXML
private TextField txtName;
@FXML
private Label lblEmail;
@FXML
private TextField txtEmail;
@FXML
private Label lblNgaySinh;
@FXML
private TextField txtNgaySinh;
@FXML
private Label lblGioiTinh;
@FXML
private TextField txtGioiTinh;


@FXML
public void initialize() {
    loadData();
  //  btnThayDoi.setOnAction(event -> updateData());
    btnTroVe.setOnAction(event -> goBack());
}

private void loadData() {
    User user = UserHandler.getDetailUser();
    txtName.setText(user.getFullName());
    txtEmail.setText(user.getEmail());
    LocalDate date = user.getDateOfBirth();
    String datestr = date.toString();
    txtNgaySinh.setText(datestr);
    if(user.getGender() == true)
        txtGioiTinh.setText("Nam");
    else
        txtGioiTinh.setText("Nữ");

}

//private void updateData() {
//    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
//        String query = "UPDATE users SET name = ?, email = ?, birthday = ?, gender = ? WHERE id = ?";
//        PreparedStatement preparedStatement = connection.prepareStatement(query);
//        preparedStatement.setString(1, txtName.getText());
//        preparedStatement.setString(2, txtEmail.getText());
//        preparedStatement.setString(3, txtNgaySinh.getText());
//        preparedStatement.setString(4, txtGioiTinh.getText());
//        preparedStatement.setInt(5, 1); // Thay đổi theo ID bạn muốn cập nhật
//
//        preparedStatement.executeUpdate();
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}

private void goBack() {
    // Logic để trở về màn hình trước nếu cần
}
}
