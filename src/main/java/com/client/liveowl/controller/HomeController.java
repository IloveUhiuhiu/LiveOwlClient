//package com.client.liveowl.controller;
//
//import com.client.liveowl.JavaFxApplication;
//import com.client.liveowl.util.UserHandler;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.shape.Circle;
//
//import java.io.IOException;
//
//
//public class HomeController {
//    @FXML
//    private Button homeButton;
//    @FXML
//    private Button contestButton;
//    @FXML
//    private Button resultButton;
//    @FXML
//    private Button messageButton;
//    @FXML
//    private Button profileButton;
//    @FXML
//    private Button logoutButton;
//    @FXML
//    private Pane contentContainer;
//    @FXML
//    private ImageView avt;
//    @FXML
//    private AnchorPane phaiPN;
//
//    private static String avatarPath = "/images/avt.png";
//
//    public static void updateAvatar(String newAvatarPath) {
//        avatarPath = newAvatarPath;
//    }
//
//    @FXML
//    public void initialize() {
//        setAvatarImage();
//    }
//
//    private void setAvatarImage() {
//        ImageView iv = new ImageView();
//        iv.setImage(new Image(avatarPath));
//        iv.setFitHeight(64.0);
//        iv.setFitWidth(64.0);
//        iv.setLayoutX(555.0);
//        iv.setLayoutY(14.0);
//        Circle circle = new Circle();
//        circle.setCenterX(32.0);
//        circle.setCenterY(32.0);
//        circle.setRadius(32.0);
//        iv.setClip(circle);
//        phaiPN.getChildren().clear();
//        phaiPN.getChildren().add(iv);
//    }
//
//@FXML
//    private void clickHomeButton() throws IOException {
//        JavaFxApplication.changeScene("/views/Home.fxml");
//    }
//    @FXML
//    private void clickContestButton() throws IOException {
//        loadContent("/views/Contest.fxml");
//    }
//    @FXML
//    private void clickResultButton() throws IOException {
//        loadContent("/views/Result.fxml");
//    }
//    @FXML
//    private void clickProfileButton() throws IOException {
//        loadContent("/views/Profile.fxml");
//    }
//    @FXML
//    private void clickMessageButton() throws IOException {
//        JavaFxApplication.changeScene("/views/Home.fxml");
//    }
//    @FXML
//    private void clickLogoutButton() throws IOException {
//        JavaFxApplication.changeScene("/views/Home.fxml");
//    }
//
//    public void loadContent(String url) throws IOException {
//        Pane content = FXMLLoader.load(getClass().getResource(url));
//        contentContainer.getChildren().clear();
//        contentContainer.getChildren().add(content);
//    }
//
//}
//
//

package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.model.User;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class HomeController
{

    @FXML
    private Button homeButton;
    @FXML
    private Button contestButton;
    @FXML
    private Button resultButton;
    @FXML
    private Button messageButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Pane contentContainer;
    @FXML
    private ImageView avt;
    @FXML
    private AnchorPane phaiPN;


    private static HomeController instance;

    static User user = UserHandler.getDetailUser();
    private static String avatarPath = user.getProfileImgLocation();

    @FXML
    public void initialize()
    {
        instance = this;
        setAvatarImage(avatarPath);
    }

    public static HomeController getInstance()
    {
        return instance;
    }

    public void setAvatarImage(String imagePath)
    {
        avatarPath = imagePath;
        if (avt != null) {
            byte[] imageBytes = Base64.getDecoder().decode(avatarPath);
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            avt.setImage(image);
            Circle circle = new Circle();
            circle.setRadius(32.0);
            circle.setCenterX(32.0);
            circle.setCenterY(32.0);
            avt.setClip(circle);
        }
    }

    @FXML
    private void clickHomeButton() throws IOException
    {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }

    @FXML
    private void clickContestButton() throws IOException
    {
        loadContent("/views/Contest.fxml");
    }

    @FXML
    private void clickResultButton() throws IOException
    {
        loadContent("/views/Result.fxml");
    }

    @FXML
    private void clickProfileButton() throws IOException
    {
        loadContent("/views/Profile.fxml");
    }

    @FXML
    private void clickMessageButton() throws IOException
    {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }

    @FXML
    private void clickLogoutButton() throws IOException
    {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }

    public void loadContent(String url) throws IOException
    {
        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
    }
}
