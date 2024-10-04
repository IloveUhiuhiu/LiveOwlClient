package com.client.liveowl;


import com.client.liveowl.util.Authentication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class JavaFxApplication extends Application {

    private static Stage stage;
    public static Authentication authentication;


    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(JavaFxApplication.class.getResource(fxml));
        stage.setScene(new Scene(pane)); // Tạo một scene mới
        stage.show();
    }


    public static void setMaximized() {
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}


