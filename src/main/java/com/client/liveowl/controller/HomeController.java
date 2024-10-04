package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;


public class HomeController {
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
    private void clickHomeButton() throws IOException {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }
    @FXML
    private void clickContestButton() throws IOException {
        loadContent("/views/Contest.fxml");
    }
    @FXML
    private void clickResultButton() throws IOException {
        loadContent("/views/Result.fxml");
    }
    @FXML
    private void clickProfileButton() throws IOException {
        loadContent("/views/Profile.fxml");
    }
    @FXML
    private void clickMessageButton() throws IOException {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }
    @FXML
    private void clickLogoutButton() throws IOException {
        JavaFxApplication.changeScene("/views/Home.fxml");
    }

    public void loadContent(String url) throws IOException {

        Pane content = FXMLLoader.load(getClass().getResource(url));
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);

    }
}
