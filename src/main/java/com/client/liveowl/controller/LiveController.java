package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.TeacherSocket;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.UdpHandler;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    @FXML
    private Label lblInformation;

    public static Map<String,ImageView> imageViews = new HashMap<>();
    public static Map<String,Button> buttonViews = new HashMap<>();
    private double heightMax = 450;
    private double widthMax = 800;
    private int numImages = 0;
    private AnimationTimer animationTimerImage;
    private AnimationTimer animationTimerExit;
    private Queue<ImageData> imageBuffer = new LinkedList<>();


    private void processImageUpdates() {
        while (!queueImage.isEmpty()) {
            //System.out.println("Lấy ảnh từ queueImage" + queueImage.size());
            imageBuffer.add(queueImage.poll());
        }

        if (!imageBuffer.isEmpty()) {
            //System.out.println("Lấy ảnh từ imageBuffer" + imageBuffer.size());
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
            //System.out.println("livestream thoi");
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
        if (animationTimerImage != null) {
            animationTimerImage.stop(); // Dừng timer nếu đã tồn tại
        }
        if (animationTimerExit != null) {
            animationTimerExit.stop(); // Dừng timer nếu đã tồn tại
        }
        //System.out.println("bắt đầu animation ");
        animationTimerImage = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!TeacherSocket.isRunning()) return;
                //System.out.println("Cập nhật ảnh");
                //System.out.println(teacherSocket.queueImage.size());
                processImageUpdates();

            }
        };
        animationTimerExit = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!TeacherSocket.isRunning()) return;
                if (!queueExit.isEmpty()) {
                    String clientIdExit = queueExit.poll();
                    handleStudentExitRequest(clientIdExit);
                }
            }
        };
        animationTimerExit.start();
        animationTimerImage.start();
    }
    public void updateImage(String clientId,Image newImage) {

            Platform.runLater(() -> {
                if (newImage != null) {
                    //System.out.println("Ảnh khác null");
                    //System.out.println("Kích thước ảnh: " + newImage.getWidth() + "x" + newImage.getHeight()); // Thêm kiểm tra kích thước ảnh
                    if (imageViews.containsKey(clientId)) {
                        if (imageViews.get(clientId).getImage() != newImage) {

                            imageViews.get(clientId).setImage(newImage);
                            //System.out.println("Cập nhật thành công ảnh");
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
        gridImage.getChildren().clear();
        int columns = Math.min(numImages, 3);
        int rows = (int) Math.ceil((double) numImages / columns);
        int i = 0;
        for (String Key: imageViews.keySet()) {
            ImageView imageView = imageViews.get(Key);
            Button buttonView = buttonViews.get(Key);
            buttonView.setStyle("-fx-background-color: #FEA837; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
            // Điều chỉnh kích thước ảnh
            adjustImageSize(imageView, rows, columns);
            Text text = new Text(listUsers.get(Key).getFullName());
            text.setStyle("-fx-fill: white; -fx-font-size: 15; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox hbox = new HBox(text, spacer, buttonView);
            hbox.setStyle("-fx-spacing: 10; -fx-padding: 0 10 0 10;");

            VBox vbox = new VBox(imageView, hbox);
            vbox.setStyle("-fx-alignment: top-center; -fx-pref-width: 100%; -fx-pref-height: 100%; -fx-background-color: #001C44; -fx-background-radius: 5px; -fx-padding: 4px;"); // Ảnh trên, text và button dưới

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
            imageView.setFitWidth(5 * widthMax / 11);
            imageView.setFitHeight(5 * heightMax / 11);
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
                    if (animationTimerImage != null) {
                        animationTimerImage.stop();
                    }
                    if (animationTimerExit != null) {
                        animationTimerExit.stop();
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
            imageViews.remove(number);
            buttonViews.remove(number);
            updateGridLayout();
            lblInformation.setText(listUsers.get(number).getFullName() + " đã rời đi!");
            lblInformation.setOpacity(0); // Đặt độ mờ ban đầu là 0


            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), lblInformation);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.setAutoReverse(false);

            fadeIn.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), lblInformation);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setCycleCount(1);
                fadeOut.setAutoReverse(false);
                fadeOut.play();
            });

            fadeIn.play();

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

