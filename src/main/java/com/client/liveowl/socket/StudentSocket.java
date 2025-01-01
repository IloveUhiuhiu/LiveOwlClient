
package com.client.liveowl.socket;
import static com.client.liveowl.AppConfig.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.UdpHandler;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.CountDownLatch;

public class StudentSocket{

    public static Random rand = new Random();
    private static final StringBuilder keyLogBuffer = new StringBuilder();
    //public static ConcurrentLinkedQueue<Image> cache = new ConcurrentLinkedQueue<>();
    public static final VideoCapture camera = null;
    private static DatagramSocket socketSend;
    private static DatagramSocket socketReceive;
    public static CountDownLatch latch;
    private static volatile boolean isRunning = true;
    private int clientPortReceive;
    private int clientPortSend;
    public static int newServerPort;
    public StudentSocket() {
        try {
            latch = new CountDownLatch(1);
            newServerPort = SERVER_PORT;
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
        newServerPort += UdpHandler.receivePort(socketSend);
        return true;
    }

    public void LiveStream() throws IOException {
        Thread taskThread = new Thread(new StudentTaskUdp(socketSend, socketReceive));
        try {
            taskThread.start();
            StudentTaskTcp.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                taskThread.join(); // Chỉ gọi join() ở đây nếu cần phải đợi kết thúc
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