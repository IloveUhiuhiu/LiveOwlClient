package com.client.liveowl.controller;

import com.client.liveowl.model.User;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProfileController
{

    @FXML
    private Button btnThayDoi;
    @FXML
    private Button btnTroVe;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNgaySinh;
    @FXML
    private TextField txtGioiTinh;

    @FXML
    public void initialize()
    {
        loadData();
        btnThayDoi.setOnAction(event -> chooseAvatar());
        btnTroVe.setOnAction(event -> goBack());
    }

    private void loadData()
    {
        User user = UserHandler.getDetailUser();
        if (user != null)
        {
            txtName.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            txtNgaySinh.setText(user.getDateOfBirth().toString());
            txtGioiTinh.setText(user.getGender() ? "Nam" : "Nữ");
        }
    }

    @FXML
    private void chooseAvatar()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) btnThayDoi.getScene().getWindow();
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null)
        {
            String imagePath = selectedFile.toURI().toString();
            HomeController homeController = HomeController.getInstance();
            if (homeController != null)
            {
                homeController.setAvatarImage(imagePath);
            }
        }
    }

    private void goBack()
    {

    }

}
