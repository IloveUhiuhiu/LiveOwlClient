package com.client.liveowl;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
//import org.jnativehook.GlobalScreen;
//import org.jnativehook.NativeHookException;
//import org.jnativehook.keyboard.NativeKeyEvent;
//import org.jnativehook.keyboard.NativeKeyListener;
//import org.jnativehook.mouse.NativeMouseEvent;
//import org.jnativehook.mouse.NativeMouseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

public class JavaFxApplication extends Application implements NativeKeyListener {

    private static Stage stg;
    private static final Logger logger = LoggerFactory.getLogger(JavaFxApplication.class);
    private static final String SERVER_ULR = "";
    @Override
    public void start(Stage primaryStage) throws Exception{
        stg = primaryStage;
        primaryStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        primaryStage.setTitle("LOGIN");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        initKeyLogger();
    }

    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(JavaFxApplication.class.getResource(fxml));
        stg.setScene(new Scene(pane)); // Tạo một scene mới
        stg.show();
    }

    private void initKeyLogger()
    {
        try{
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
        GlobalScreen.addNativeKeyListener(this);
       // GlobalScreen.addNativeMouseListener(this);
    }


    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        // Không cần xử lý sự kiện gõ phím
        String keytext = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
        System.out.println("bạn vừa gõ phím "+keytext);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        String keytext = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
        System.out.println("bạn vừa thả phím "+keytext);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }
//    @Override
//    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
//        System.out.println("Bạn vừa nhấp chuột vào vị trí: " + nativeMouseEvent.getX() + ", " + nativeMouseEvent.getY());
//    }
//
//    @Override
//    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
//        System.out.println("Bạn vừa nhấn chuột tại: " + nativeMouseEvent.getX() + ", " + nativeMouseEvent.getY());
//    }
//
//    @Override
//    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
//        System.out.println("Bạn vừa thả chuột tại: " + nativeMouseEvent.getX() + ", " + nativeMouseEvent.getY());
//    }



//    private void sendKeyToServer(String keytext)
//    {
//        try {
//            URL url = new URL(SERVER_ULR);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json; utf-8");
//            connection.setDoOutput(true);
//
//            String jsonInputString = "{\"key\":\"" + keytext + "\"}";
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


    public static void main(String[] args) {
        Application.launch(args);
    }


}


