package com.client.liveowl;


import com.client.liveowl.controller.LiveController;
import com.client.liveowl.controller.StudentController;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;
import java.util.function.Consumer;

public class JavaFxApplication extends Application {

    public static Stage stage;
    public static Authentication authentication;

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/views/JoinExam.fxml"));
        stage.setTitle("Login");
        Image iconImage = new Image(getClass().getResourceAsStream("/images/logo.png"));


        stage.getIcons().add(iconImage);
        Scene scene = new Scene(root,600,400);
        scene.getStylesheets().add(getClass().getResource("/views/css/style.css").toExternalForm());
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

    public static void changeScene(String fxml) throws IOException {
        String title = fxml.substring(fxml.lastIndexOf("/") + 1, fxml.lastIndexOf(".fxml"));
        Parent pane = FXMLLoader.load(JavaFxApplication.class.getResource(fxml));
        stage.setTitle(title);
        stage.setScene(new Scene(pane)); // Tạo một scene mới
        stage.show();
    }

//    public static void changeScene(String fxmlPath, Consumer<Object> initController) throws IOException {
//        FXMLLoader loader = new FXMLLoader(JavaFxApplication.class.getResource(fxmlPath));
//        Parent root = loader.load();
//        if (initController != null) {
//            initController.accept(loader.getController());
//        }
//        stage.setScene(new Scene(root));
//    }


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


