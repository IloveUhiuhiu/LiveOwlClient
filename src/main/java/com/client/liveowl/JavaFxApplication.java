package com.client.liveowl;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane helloPane = new Pane(new Label("Hello JavaFx"));
        primaryStage.setScene(new Scene(helloPane, 200, 100));
        primaryStage.show();
    }
}

