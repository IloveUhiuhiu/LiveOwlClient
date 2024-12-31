package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.User;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
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
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private ImageView editName;
    @FXML
    private ImageView editEmail;
    @FXML
    private ImageView editDate;
    @FXML
    private ImageView editGender;
    @FXML
    private DatePicker dateofbirth;
    @FXML
    private ChoiceBox Sex;
    @FXML
    public void initialize()
    {
        loadData();
        btnThayDoi.setOnAction(event -> chooseAvatar());
        btnTroVe.setOnAction(event -> {
            try {
                goBack();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnUpdate.setOnAction(actionEvent -> updateInfo());
        editName.setOnMouseClicked(event -> editName());
        editEmail.setOnMouseClicked(event -> editEmail());
        editDate.setOnMouseClicked(event -> editDate());
        editGender.setOnMouseClicked(event -> editGender());
    }

    private void loadData()
    {
        User user = UserHandler.getDetailUser();
        if (user != null)
        {
            txtName.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            dateofbirth.setValue(user.getDateOfBirth());
            dateofbirth.setDisable(true);
            Sex.setValue(user.getGender() ? "Nam" : "Nữ");
            Sex.setDisable(true);
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
                Image image = new Image(new ByteArrayInputStream(fileBytes));
                avt.setImage(image);
                btnSave.setOnAction(event ->{
                    UserHandler.sendImage(profile);
                    if(UserHandler.getDetailUser().getRole().equals("1"))
                    {
                        HomeTeacherController homeController = HomeTeacherController.getInstance();
                        homeController.setAvatarImage(profile, homeController.getAvt(), 80.0, 80.0);
                    }
                    else
                    {
                        HomeStudentController studentController = HomeStudentController.getInstance();
                        studentController.setAvatarImage(profile, studentController.getAvt(), 80.0, 80.0);
                    }


                });
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    private void goBack() throws IOException {
        if(UserHandler.getDetailUser().getRole().equals("2"))
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomeStudent.fxml"));
            Parent root = loader.load();
            HomeStudentController studentController = loader.getController();
            studentController.setAvatarImage(UserHandler.getDetailUser().getProfileImgLocation(),
                    studentController.getAvt(), 70.0, 70.0);
            JavaFxApplication.stage.getScene().setRoot(root);
        }
        else
            JavaFxApplication.changeScene("/views/HomeTeacher.fxml");
    }

    private void updateInfo()
    {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String genderStr = (String) Sex.getValue();
        LocalDate dateOfBirth = dateofbirth.getValue();
        Boolean genderBol;
        if(genderStr.equals("Nam"))
            genderBol = true;
        else if (genderStr.equals("Nữ"))
            genderBol = false;
        else
            return;
        UserHandler.sendÌnor(name, email, dateOfBirth, genderBol);
    }
    private void editName()
    {
        txtName.setEditable(true);
        txtName.requestFocus();
    }
    private void editEmail()
    {
        txtEmail.setEditable(true);
        txtEmail.requestFocus();
    }
    private void editGender()
    {
        Sex.setDisable(false);
        Sex.requestFocus();
    }
    private void editDate()
    {
        dateofbirth.setDisable(false);
        dateofbirth.requestFocus();
    }
}
