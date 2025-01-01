
package com.client.liveowl.socket;
import static com.client.liveowl.AppConfig.*;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.UdpHandler;
import com.github.kwhat.jnativehook.NativeHookException;
import org.opencv.videoio.VideoCapture;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentSocket{

    public static Random rand = new Random();
    public static final VideoCapture camera = null;
    private static DatagramSocket socketSend;
    private static DatagramSocket socketReceive;
    public static CountDownLatch latch;
    private static volatile boolean isRunning = true;
    private int clientPortReceive;
    private int clientPortSend;
    public static int newServerPort;
    public static int newServerPortLogger;
    public StudentSocket() {
        try {
            latch = new CountDownLatch(1);
            newServerPort = SERVER_PORT;
            newServerPortLogger = SERVER_PORT_LOGGER;
            isRunning = true;
            clientPortSend = rand.nextInt(999)+ STUDENT_PORT;
            clientPortReceive = clientPortSend - 1000;
            System.out.println("Client port: " + clientPortSend +", " + clientPortReceive);
            socketSend = new DatagramSocket(clientPortSend);
            socketReceive = new DatagramSocket(clientPortReceive);
        } catch (Exception e) {
            if (socketSend != null) socketSend.close();
            if (socketReceive != null) socketReceive.close();
            System.out.println("Lỗi khi khởi tạo Socket: " + e.getMessage());
        }
    }

    public boolean CheckConnect(String code) throws IOException {
        String connect = Authentication.getUserId() + ":student:" + code;
        UdpHandler.sendMsg(socketSend,connect,InetAddress.getByName(SERVER_HOST_NAME), SERVER_PORT);// gửi connect
        String message = UdpHandler.receiveMsg(socketSend);// nhận phản hồi từ server
        if (message.equals("fail")) {
            return false;
        }
        int number = UdpHandler.receivePort(socketSend);
        newServerPort += number;
        newServerPortLogger += number;
        System.out.println("Port logger new la: " + newServerPortLogger);
        return true;
    }

    public void LiveStream() throws IOException {
        Thread taskUdpThread = new Thread(new StudentTaskUdp(socketSend, socketReceive));

        try {
            taskUdpThread.start();
            StudentTaskTcp.start();
        } catch (Exception e) {

        } finally {
            try {
                taskUdpThread.join(); // Chỉ gọi join() ở đây nếu cần phải đợi kết thúc
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
                cleanupResources();
                System.out.println("Close Livestream");
                System.out.println("Close Send KeyLogger");
            }
        }

    }
    private void cleanupResources() {
        if (socketReceive != null) socketReceive.close();
        if (socketSend != null) socketSend.close();
        if (camera != null) camera.release();
        setRunning(false);
    }

    public static synchronized void setRunning(boolean live) {isRunning = live;}
    public static synchronized boolean isRunning() {return isRunning;}

}