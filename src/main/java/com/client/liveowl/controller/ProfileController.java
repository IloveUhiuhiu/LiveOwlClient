package com.client.liveowl.controller;

import com.client.liveowl.model.User;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

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
    private ImageView avt;

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
            byte[] imageBytes = Base64.getDecoder().decode(user.getProfileImgLocation());
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            avt.setImage(image);
            avt.setFitWidth(100.0);
            avt.setFitHeight(100.0);
            avt.setPreserveRatio(false);
            double radius = avt.getFitWidth() / 2;
            Circle circle = new Circle(radius);
            circle.setCenterX(avt.getFitWidth() / 2);
            circle.setCenterY(avt.getFitHeight() / 2);
            avt.setClip(circle);
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
            try
            {
                byte[] fileBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                String profile = java.util.Base64.getEncoder().encodeToString(fileBytes);
                HomeController homeController = HomeController.getInstance();
                if (homeController != null)
                {
                    homeController.setAvatarImage(profile);
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    private void goBack()
    {

    }

}
