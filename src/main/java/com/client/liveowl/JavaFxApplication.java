package com.client.liveowl;


import com.client.liveowl.util.Authentication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class JavaFxApplication extends Application {

    public static Stage stage;
    public static Authentication authentication;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        stage.setTitle("Login");
        Image iconImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        stage.getIcons().add(iconImage);
        Scene scene = new Scene(root,600,400);
        stage.setScene(scene);
//        stage.setOnCloseRequest(event -> {
//            event.consume();
//            AlertDialog alertDialog = new AlertDialog("Xác nhận thoát "  + authentication + ", " + Authentication.getRole(),null,"Bạn có chắc chắn muốn thoát không?",Alert.AlertType.CONFIRMATION);
//            Alert alert = alertDialog.getConfirmationDialog();
//
//            alert.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    if (LiveController.isLive == true) {
//                        LiveController.teacherSocket.clickBtnExit();
//                    }
//                    if (Authentication.getRole() == 2) {
//                        try {
//                            //StudentController.theSocket.sendExitNotificationToTeacher();
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    stage.close();
//                }
//            });
//
//
//        });
        stage.show();
    }

    public static void changeScene(String fxml, String title) throws IOException {
        Parent pane = FXMLLoader.load(JavaFxApplication.class.getResource(fxml));
        stage.setTitle(title);
        stage.setScene(new Scene(pane)); // Tạo một scene mới
        stage.show();
    }

    public static void setMaximized() {
        stage.setMaximized(true);
    }
    public static void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}


