package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.TeacherSocket;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.UdpHandler;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.client.liveowl.JavaFxApplication.stage;
import static com.client.liveowl.socket.TeacherSocket.*;
import static com.client.liveowl.AppConfig.*;

public class LiveController {

    private TeacherSocket teacherSocket;
    public static String code;
    public static String examId;
    @FXML
    private Button exitButton;
    @FXML
    private GridPane gridImage;
    public static Map<String,ImageView> imageViews = new HashMap<>();
    public static Map<String,Button> buttonViews = new HashMap<>();
    private double heightMax = 450;
    private double widthMax = 800;
    private int numImages = 0;
    private AnimationTimer animationTimer;
    private Queue<ImageData> imageBuffer = new LinkedList<>();


    private void processImageUpdates() {
        while (!queueImage.isEmpty()) {
            System.out.println("Lấy ảnh từ queueImage" + queueImage.size());
            imageBuffer.add(queueImage.poll());
        }

        if (!imageBuffer.isEmpty()) {
            System.out.println("Lấy ảnh từ imageBuffer" + imageBuffer.size());
            ImageData imageData = imageBuffer.poll();
            //if (!isExit.containsKey(imageData.getClientId()))
                updateImage(imageData.getClientId(), imageData.getImage());

        }
    }


    @FXML
    public void initialize() {
        stage.setTitle("LiveStream - " + code);
        teacherSocket = new TeacherSocket();
        try {
            System.out.println("livestream thoi");
            teacherSocket.LiveStream(examId,code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaFxApplication.setMaximized();
        JavaFxApplication.setResizable(false);

        gridImage.setHgap(10);
        gridImage.setVgap(10);
        gridImage.setStyle("-fx-alignment: center;");

        startAnimationTimer();
    }
    private void startAnimationTimer() {
        if (animationTimer != null) {
            animationTimer.stop(); // Dừng timer nếu đã tồn tại
        }
        System.out.println("bắt đầu animation ");
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!TeacherSocket.isRunning()) return;
                System.out.println("Cập nhật ảnh");
                System.out.println(teacherSocket.queueImage.size());
                processImageUpdates();
                if (!queueExit.isEmpty()) {
                    String clientIdExit = queueExit.poll();
                    handleStudentExitRequest(clientIdExit);
                }
            }
        };
        animationTimer.start(); // Khởi động lại timer
    }
    public void updateImage(String clientId,Image newImage) {

            Platform.runLater(() -> {
                if (newImage != null) {
                    System.out.println("Ảnh khác null");
                    if (imageViews.containsKey(clientId)) {
                        if (imageViews.get(clientId).getImage() != newImage) {

                            imageViews.get(clientId).setImage(newImage);
                            System.out.println("Cập nhật thành công ảnh");
                        }
                    } else {
                        System.out.println("Thêm người mới");
                        ImageView currentImage = new ImageView(newImage);

                        imageViews.put(clientId, currentImage);
                        Button buttonView = new Button("TurnOn/TurnOff");
                        final String index = clientId;
                        buttonView.setOnAction(event -> {
                            System.out.println("Button " + index + " đã được nhấn");
                            try {
                                DatagramSocket cameraSocket = new DatagramSocket(2543);
                                UdpHandler.sendRequestCamera(cameraSocket, index, InetAddress.getByName(SERVER_HOST_NAME), newserverPort);
                                System.out.println("Gửi thành công yêu cầu button camera");
                                cameraSocket.close();
                            } catch (Exception e) {
                                System.out.println("Lỗi khi nhấn button: " + e.getMessage());
                            }
                        });
                        buttonViews.put(clientId, buttonView);
                        ++numImages;
                        updateGridLayout();
                    }
                }

            });


    }
    private void updateGridLayout() {
        gridImage.getChildren().clear(); // Xóa các node hiện tại
        int columns = Math.min(numImages, 3);
        int rows = (int) Math.ceil((double) numImages / columns);
        //exitButton.setText(numImages + ", " + rows + ", " + columns);
        int i = 0;
        for (String Key: imageViews.keySet()) {
            ImageView imageView = imageViews.get(Key);
            Button buttonView = buttonViews.get(Key);
            // Điều chỉnh kích thước ảnh
            adjustImageSize(imageView, rows, columns);

            VBox vbox = new VBox(imageView, buttonView);
            vbox.setStyle("-fx-alignment: center; -fx-pref-width: 100%; -fx-pref-height: 100%; -fx-background-color: gray; -fx-background-radius: 5px; -fx-padding: 4px;");

            int col = i % columns;
            int row = i / columns;
            gridImage.add(vbox, col, row);
            ++i;
        }
    }

    private void adjustImageSize(ImageView imageView, int rows, int columns) {
        if (rows == 1 && columns == 1) {
            imageView.setFitWidth(widthMax);
            imageView.setFitHeight(heightMax);
        } else if (rows == 1 && columns == 2) {
            imageView.setFitWidth(4 * widthMax / 5);
            imageView.setFitHeight(4 * heightMax / 5);
        } else {
            imageView.setFitWidth(widthMax / 2);
            imageView.setFitHeight(heightMax / 2);
        }
    }
    @FXML
    private void handleExitButtonClick() {

        AlertDialog alertDialog = new AlertDialog("Xác nhận thoát",null,"Bạn có chắc chắn muốn thoát không?", Alert.AlertType.CONFIRMATION);
        Alert alert = alertDialog.getConfirmationDialog();
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    TeacherSocket.setRunning(false);
                    try {
                        DatagramSocket exitSocket = new DatagramSocket(2190);
                        UdpHandler.sendRequestExitToStudents(exitSocket, Authentication.getToken(), InetAddress.getByName(SERVER_HOST_NAME), newserverPort);
                        System.out.println("Gửi thành công yêu cầu exit");
                        exitSocket.close();
                    } catch (IOException e) {
                        System.out.println("Lỗi khi gửi thông điệp exit " + e.getMessage());
                    }
                    cleanResources();
                    if (animationTimer != null) {
                        animationTimer.stop();
                    }
                    JavaFxApplication.changeScene("/views/HomeTeacher.fxml", "Home");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    private void handleStudentExitRequest(String number) {
        System.out.println("client " + number);
        if (imageViews.containsKey(number)) {
            numImages--;
            System.out.println("So luong image " + numImages);
            imageViews.remove(number);
            buttonViews.remove(number);
            updateGridLayout();
        }
    }
    private void cleanResources() {
        try {
            code = null;
            examId = null;
            buttonViews.clear();
            imageViews.clear();
            if (TeacherSocket.imageBuffer != null) TeacherSocket.imageBuffer.clear();
            if (TeacherSocket.queueExit != null) TeacherSocket.queueExit.clear();
            if (TeacherSocket.queueImage != null) TeacherSocket.queueImage.clear();
            System.out.println("Đóng LiveController thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng socket: " + e.getMessage());
        }
    }
}

