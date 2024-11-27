package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.User;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

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
                Image image = new Image(new ByteArrayInputStream(fileBytes));
                avt.setImage(image);
                btnSave.setOnAction(event ->{
                    UserHandler.sendImage(profile);
                    if(UserHandler.getDetailUser().getRole().equals("1"))
                    {
                        HomeController homeController = HomeController.getInstance();
                        homeController.setAvatarImage(profile, homeController.getAvt(), 80.0, 80.0);
                    }
                    else
                    {
                        StudentController studentController = StudentController.getInstance();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Student.fxml"));
            Parent root = loader.load();
            StudentController studentController = loader.getController();
            studentController.setAvatarImage(UserHandler.getDetailUser().getProfileImgLocation(),
                    studentController.getAvt(), 70.0, 70.0);
            JavaFxApplication.stage.getScene().setRoot(root);
        }
        else
            JavaFxApplication.changeScene("/views/Home.fxml");
    }

    private void updateInfo()
    {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String genderStr = txtGioiTinh.getText();
        String dateOfBirthStr = txtNgaySinh.getText();
        Boolean genderBol;
        if(genderStr.equals("Nam"))
            genderBol = true;
        else if (genderStr.equals("Nữ"))
            genderBol = false;
        else
            return;
        LocalDate dateofbirth = LocalDate.parse(dateOfBirthStr);
        UserHandler.sendÌnor(name, email, dateofbirth, genderBol);
    }

}
