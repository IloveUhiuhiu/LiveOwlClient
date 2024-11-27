package com.client.liveowl.controller;
import com.client.liveowl.JavaFxApplication;
import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.client.liveowl.AppConfig.serverHostName;
import static com.client.liveowl.socket.StudentSocket.latch;


public class JoinExamController {
@FXML
private ImageView image;
@FXML
private Button buttonCamera;
@FXML
private Button buttonRequest;
@FXML
private Button buttonCancel;
@FXML
private Label isActive;
private boolean cameraIsActive = false;
private VideoCapture camera = null;

@FXML
public void initialize() throws IOException {

    buttonRequest.setOnAction(e -> {
        image.setImage(null);
        StudentSocket studentSocket = new StudentSocket();
        try {
            if (studentSocket.CheckConnect(Authentication.getCode())) {
                isActive.setText("...Bạn đang được giám sát.");
                isActive.setStyle("-fx-text-fill: #00FF00;");

                new Thread(() -> {
                    try {
                        studentSocket.LiveStream(); // Chạy livestream
                        latch.await();
                        Platform.runLater(() -> {
                            try {
                                JavaFxApplication.changeScene("/views/Student.fxml");
                                System.out.println("Livestream đã kết thúc. Tiến hành các thao tác tiếp theo!");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    } catch (InterruptedException | IOException ex) {
                        System.out.println("Đã xảy ra lỗi khi chờ livestream kết thúc.");
                    }
                }).start();

            } else {
                isActive.setText("...Oops, Lỗi kết nối !!!");
                isActive.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception ex) {

        }
    });
    buttonCancel.setOnAction(e -> {

            AlertDialog alertDialog = new AlertDialog("Xác nhận thoát " ,null,"Bạn có chắc chắn muốn thoát không?", Alert.AlertType.CONFIRMATION);
            Alert alert = alertDialog.getConfirmationDialog();

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (StudentSocket.isLive()) {
                        try {
                            StudentSocket.setLive(false);
                            sendExitNotificationToTeacher();
                        } catch (Exception ex) {
                            System.out.println("Lỗi khi gui exit " + ex.getMessage());
                        }
                    }
                    try {
                        JavaFxApplication.changeScene("/views/Student.fxml");
                    } catch (IOException ex) {
                        System.out.println("Lỗi khi chang Scene " + ex.getMessage());
                    }

                }
            });


    });
    buttonCamera.setOnAction(e -> {

        if (cameraIsActive) {
            cameraIsActive = false;
            camera.release();

        } else {
            cameraIsActive = true;
            camera = new VideoCapture(0);

            Thread thread = new Thread(() -> {
                while (cameraIsActive) {
                    Mat frame = new Mat();
                    if (camera.read(frame)) {
                        if (!frame.empty()) {
                            try {
                                Image newImage = getImage(frame);
                                Platform.runLater(() -> {
                                    if (cameraIsActive) image.setImage(newImage);
                                });
                            } catch (IOException ex) {
                                System.out.println("Error processing image: " + ex.getMessage());
                            }
                        }
                    }
                }
                image.setImage(null);
            });

            thread.setDaemon(true);
            thread.start();

        }
    });
}
    private Image getImage(Mat frame) throws IOException {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        BufferedImage bufferedImage = ImageData.matToBufferedImage(frame);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        return new Image(new ByteArrayInputStream(imageBytes));
    }
    public void sendExitNotificationToTeacher() throws Exception {
        System.out.println("Send exit for teacher");
        DatagramSocket socketExit = new DatagramSocket(8765);
        UdpHandler.sendRequestExitToTeacher(socketExit, Authentication.getUserId(), InetAddress.getByName(serverHostName),StudentSocket.newServerPort);
        if (camera != null) camera.release();
        socketExit.close();
    }
}
