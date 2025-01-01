package com.client.liveowl.controller;

import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.UdpHandler;
import com.client.liveowl.video.ProcessPlayVideo;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.client.liveowl.AppConfig.SERVER_HOST_NAME;


public class VideoPlayerController {
    @FXML
    private ImageView video;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    private boolean isFirst = true;
    private boolean isPlaying = false;
    private AnimationTimer animationTimer;
    private ProcessPlayVideo watchedVideo;
    private static final int TARGET_FPS = 10; // Đặt FPS mong muốn
    private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
    private long lastUpdate = 0;
    @FXML
    public void initialize(String code, String clientId, ProcessPlayVideo watchedVideo) {
        isPlaying = false;
        isFirst = true;
        this.watchedVideo = watchedVideo;
        new Thread(() -> {
            try {
                watchedVideo.getVideo(code, clientId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        if (animationTimer != null) animationTimer.stop();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println("ProcessPlayVideo.isLivestream " + ProcessPlayVideo.isLivestream());

                if (!ProcessPlayVideo.isLivestream()) {
                    animationTimer.stop();
                }
                if (now - lastUpdate >= NANOSECONDS_PER_SECOND / TARGET_FPS) {
                    System.out.println("watchedVideo.packetBuffer.size " + watchedVideo.packetBuffer.size());
                    if ( watchedVideo != null  && !watchedVideo.packetBuffer.isEmpty()) {
                        if (isFirst || isPlaying) {
                            processImageUpdates();
                            isFirst = false;
                        }
                        lastUpdate = now;
                    }
                }
            }
        };
        animationTimer.start();
    }
    @FXML
    private void clickPlayButton() {
        sendNotificationToServer("play");
        isPlaying = true;
    }
    @FXML
    private void clickPauseButton() {
        isPlaying = false;
        sendNotificationToServer("pause");
    }
    private void processImageUpdates() {
        Image imageData = watchedVideo.packetBuffer.poll();
        System.out.println("Set Image: " + imageData);
        Platform.runLater(() -> video.setImage(imageData));

    }
    public void sendNotificationToServer(String request)  {
        try {
            DatagramSocket socketPause = new DatagramSocket(8765);
            System.out.println("Gui pause" + ProcessPlayVideo.serverPortPause);
            UdpHandler.sendMsg(socketPause, request, InetAddress.getByName(SERVER_HOST_NAME), ProcessPlayVideo.serverPortPause);
            socketPause.close();
        } catch (Exception e) {
            System.out.println("Error sending pause notification " + e.getMessage());
        }
    }


}