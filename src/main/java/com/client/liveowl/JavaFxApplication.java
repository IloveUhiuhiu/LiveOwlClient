package com.client.liveowl;


import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.socket.TeacherSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.UdpHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.client.liveowl.AppConfig.SERVER_HOST_NAME;
import static com.client.liveowl.socket.StudentSocket.camera;
import static com.client.liveowl.socket.TeacherSocket.newserverPort;

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
        stage.setOnCloseRequest(event -> {
            event.consume();
            AlertDialog alertDialog = new AlertDialog("Xác nhận thoát " ,null,"Bạn có chắc chắn muốn thoát không? " +
                    "\nHành động tắt ứng dụng trong khi livestream \ncó thể dẫn đến mất dữ liệu!!!", Alert.AlertType.CONFIRMATION);
            Alert alert = alertDialog.getConfirmationDialog();
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (Authentication.getRole() == 1) {
                        if (TeacherSocket.isRunning()) {
                            sendExitNotificationToStudent();
                        }
                    } else {
                        if (StudentSocket.isRunning()) {
                            sendExitNotificationToTeacher();
                        }
                    }
                    stage.close();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            });
        });
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
    public void sendExitNotificationToTeacher() {
        try {
            StudentSocket.setRunning(false);
            DatagramSocket socketExit = new DatagramSocket(8765);
            UdpHandler.sendRequestExitToTeacher(socketExit, Authentication.getUserId(), InetAddress.getByName(SERVER_HOST_NAME), StudentSocket.newServerPort);
            if (camera != null) camera.release();
            socketExit.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi tắt ứng dụng " + e.getMessage());
        }
    }

    public void sendExitNotificationToStudent() {
        try {
            TeacherSocket.setRunning(false);
            DatagramSocket exitSocket = new DatagramSocket(2190);
            UdpHandler.sendRequestExitToStudents(exitSocket, Authentication.getToken(), InetAddress.getByName(SERVER_HOST_NAME), newserverPort);
            exitSocket.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi tắt ứng dụng " + e.getMessage());
        }
    }
}


