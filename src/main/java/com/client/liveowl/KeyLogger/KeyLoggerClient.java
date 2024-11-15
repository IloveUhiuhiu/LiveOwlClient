package com.client.liveowl.KeyLogger;//package com.client.liveowl.KeyLogger;
//
//import com.github.kwhat.jnativehook.NativeHookException;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import org.jnativehook.GlobalScreen;
//import org.jnativehook.NativeHookException;
//import org.jnativehook.keyboard.NativeKeyEvent;
//import org.jnativehook.keyboard.NativeKeyListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.github.kwhat.jnativehook.GlobalScreen;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
//
//public class KeyLoggerClient extends Application implements NativeKeyListener {
//
//    private static final Logger logger = LoggerFactory.getLogger(KeyLoggerClient.class);
//    private static final String SERVER_URL = "http://localhost:8080/api/keylogger";
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        VBox root = new VBox();
//        Label label = new Label("Ứng dụng đang theo dõi phím gõ...");
//        root.getChildren().add(label);
//
//        Scene scene = new Scene(root, 300, 200);
//        primaryStage.setTitle("Keylogger Client");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        // Khởi tạo keylogger
//        initKeyLogger();
//    }
//
//    private void initKeyLogger() {
//        try {
//            GlobalScreen.registerNativeHook();
//        } catch (NativeHookException e) {
//            logger.error("Error registering native hook", e);
//        }
//
//        GlobalScreen.addNativeKeyListener(this);
//    }
//
//    @Override
//    public void nativeKeyPressed(NativeKeyEvent e) {
//        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
//        System.out.println(keyText);
//    }
//
//    @Override
//    public void nativeKeyReleased(NativeKeyEvent e) {
//        // Không cần xử lý sự kiện thả phím
//        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
//        System.out.println(keyText);
//    }
//
//    @Override
//    public void nativeKeyTyped(NativeKeyEvent e) {
//        // Không cần xử lý sự kiện gõ phím
//        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
//        System.out.println(keyText);
//    }
//
////    private void sendKeyToServer(String keyText) {
////        try {
////            URL url = new URL(SERVER_URL);
////            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
////            connection.setRequestMethod("POST");
////            connection.setRequestProperty("Content-Type", "application/json; utf-8");
////            connection.setDoOutput(true);
////
////            String jsonInputString = "{\"key\":\"" + keyText + "\"}";
////
////            try (var os = connection.getOutputStream()) {
////                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
////                os.write(input, 0, input.length);
////            }
////
////            int code = connection.getResponseCode();
////            logger.info("Key sent, server responded with code: " + code);
////
////        } catch (IOException e) {
////            logger.error("Error sending key to server", e);
////        }
////    }
//}






import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.sun.glass.ui.Application;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyLoggerClient  implements NativeKeyListener {

    // Lưu lịch sử mã đã gõ
    List<String> codeHistory = new ArrayList<>();
    private boolean ctrlPressed = false; // Cờ để theo dõi trạng thái của phím Ctrl

    public static void main(String[] args) {
        try {
            // Tắt cảnh báo log của thư viện
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            // Đăng ký lắng nghe sự kiện bàn phím
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new KeyLoggerClient());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ghi lại từng ký tự khi phím được nhấn
    // Ghi lại từng ký tự khi phím được nhấn
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        char keyChar = e.getKeyChar();

        // Chỉ ghi lại các ký tự in được
        if (Character.isDefined(keyChar) && !Character.isISOControl(keyChar)) {
            recordKeystroke(String.valueOf(keyChar));
        }
    }

    // Phát hiện khi nhấn phím
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // Kiểm tra nếu phím Ctrl được nhấn
        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            ctrlPressed = true;
        }

        // Xử lý khi phím Backspace được nhấn
        if (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE && !codeHistory.isEmpty()) {
            // Xóa ký tự cuối cùng trong codeHistory khi học sinh xóa
            codeHistory.remove(codeHistory.size() - 1);
            System.out.println("Đã xóa ký tự cuối cùng khỏi codeHistory.");

            // In lại toàn bộ nội dung của codeHistory sau khi xóa
            System.out.println("Nội dung của codeHistory: " + String.join("", codeHistory));
        }

        // Nếu Ctrl + V được nhấn
        if (ctrlPressed && e.getKeyCode() == NativeKeyEvent.VC_V) {
            System.out.println("Ctrl + V được nhấn!");
            checkClipboardContent(); // Kiểm tra clipboard khi phát hiện thao tác dán
        }
    }


    // Hàm ghi lại nội dung khi học sinh gõ phím
    public void recordKeystroke(String code) {
        codeHistory.add(code);
        System.out.println("Đã ghi: " + code);
    }

    // Hàm kiểm tra clipboard
    public void checkClipboardContent() {
        String clipboardContent = getClipboard(); // Lấy nội dung clipboard

        if (clipboardContent != null && !clipboardContent.isEmpty()) {
            // In ra toàn bộ nội dung của codeHistory trên cùng một hàng
            System.out.print("Nội dung của codeHistory: ");
            for (String code : codeHistory) {
                System.out.print(code); // In từng ký tự liên tiếp
            }
            System.out.println(); // Xuống dòng sau khi in xong
                String ch = String.join("", codeHistory).replace(" ", "").toLowerCase();
                String cb = clipboardContent.replaceAll("\\r?\\n", "").replace(" ", "").toLowerCase();
                System.out.println(ch);
                System.out.println(cb);
            // So sánh nội dung clipboard với codeHistory
            if (!(String.join("", codeHistory).replace(" ", "").toLowerCase()).contains(clipboardContent.replaceAll("\\r?\\n", "").replace(" ", "").toLowerCase())) {
                // Gửi cảnh báo nếu nội dung clipboard không khớp với lịch sử mã
                sendAlertToSupervisor(clipboardContent);
            } else {
                System.out.println("Mã đã tự gõ, không có gì đáng ngờ.");
            }
        }
    }

    // Hàm lấy nội dung từ clipboard
    public String getClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            // Chỉ lấy dữ liệu là văn bản
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Hàm gửi cảnh báo tới người giám sát
    public void sendAlertToSupervisor(String suspiciousCode) {
        System.out.println("Cảnh báo: Bạn đã copy code từ nơi khác! Nội dung: " + suspiciousCode);
    }

}

