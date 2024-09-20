package com.client.liveowl;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home.fxml"));
        // Thiết lập biểu tượng ứng dụng
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        HBox root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("LiveOwl Application");
        primaryStage.show();
    }
}

