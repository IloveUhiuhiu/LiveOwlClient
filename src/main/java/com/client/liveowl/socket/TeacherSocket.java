package com.client.liveowl.socket;
import com.client.liveowl.model.User;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import static com.client.liveowl.AppConfig.*;

public class TeacherSocket{
    public static Random rand = new Random();
    public static Map<String, byte[]> imageBuffer = new HashMap<>();
    public static Map<String, Image> lastImage = new HashMap<>();
    public static int imageCount = 0;
    public static int imageAtual = 0;
    public static int newserverPort;
    public DatagramSocket socketSend;
    public DatagramSocket socketRecieve;
    public static ConcurrentLinkedQueue<ImageData> queueImage = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<String> queueExit = new ConcurrentLinkedQueue<>();


    public static Map<String, User> listUsers = new ConcurrentHashMap<>();
    public static volatile boolean isRunning = true;
    public TeacherSocket() {
        try {
            newserverPort = SERVER_PORT;
            isRunning = true;
            socketSend = new DatagramSocket(TEACHER_PORT + rand.nextInt(999));
            socketRecieve = new DatagramSocket(TEACHER_PORT - 1000);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void LiveStream(String examId, String code) throws IOException {
            String connect = examId + ":teacher:" + code;
            System.out.println("Gửi thành công chuỗi connect đến server!");
            System.out.println("Gửi role teacher!");
            UdpHandler.sendMsg(socketSend, connect, InetAddress.getByName(SERVER_HOST_NAME), SERVER_PORT);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            newserverPort += UdpHandler.receivePort(socketSend);
            System.out.println("Port mới là :" + newserverPort);
            System.out.println("Chờ mọi người tham gia!");
            TeacherTaskUdp task = new TeacherTaskUdp(socketSend, socketRecieve);
            Thread thread = new Thread(task);thread.start();

    }
    public static synchronized boolean isRunning() {
        return isRunning;
    }
    public static synchronized void setRunning(boolean isRunning) {
        TeacherSocket.isRunning = isRunning;
    }

}

