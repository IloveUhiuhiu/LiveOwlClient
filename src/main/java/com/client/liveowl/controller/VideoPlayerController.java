package com.client.liveowl.controller;

import com.client.liveowl.socket.WatchedVideo;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;


public class VideoPlayerController {
    @FXML
    private ImageView video;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    private AnimationTimer animationTimer;
    private WatchedVideo watchedVideo;
    private static final int TARGET_FPS = 10; // Đặt FPS mong muốn
    private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
    private long lastUpdate = 0;
    @FXML
    public void initialize(String code, String clientId) {
        System.out.println(code + "," + clientId + ",dda vao");
        new Thread(() -> {
            try {
                watchedVideo = new WatchedVideo();
                watchedVideo.getVideo(code, clientId);
                System.out.println("WatchedVideo initialized successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!WatchedVideo.isLivestream()) {
                    animationTimer.stop();
                }
                if (now - lastUpdate >= NANOSECONDS_PER_SECOND / TARGET_FPS) {
                    processImageUpdates();
                    lastUpdate = now; // Cập nhật thời gian cuối cùng
                }
            }
        };
        animationTimer.start();


    }

    private void processImageUpdates() {
        System.out.println("processImageUpdates");
        if ( watchedVideo != null  && !watchedVideo.packetBuffer.isEmpty()) {
            Image imageData = watchedVideo.packetBuffer.poll();
            System.out.println("Set Image: " + imageData);
            Platform.runLater(() -> video.setImage(imageData));
        } else {
            System.out.println("No images in buffer");
        }
    }
}