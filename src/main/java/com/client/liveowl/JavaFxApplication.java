package com.client.liveowl;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFxApplication extends Application {

    private static Stage stg;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stg = primaryStage;
        primaryStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        primaryStage.setTitle("LOGIN");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(JavaFxApplication.class.getResource(fxml));
        stg.setScene(new Scene(pane)); // Tạo một scene mới
        stg.show();
    }



    public static void main(String[] args) {
        Application.launch(args);
    }
}


