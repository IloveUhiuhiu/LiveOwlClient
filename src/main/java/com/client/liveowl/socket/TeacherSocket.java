package com.client.liveowl.socket;
import com.client.liveowl.controller.LiveController;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.client.liveowl.AppConfig.*;

public class TeacherSocket{

    public static Map<String, byte[]> imageBuffer = new HashMap<>();
    public static int imageCount = 0;
    public static int imageAtual = 0;
    public static int newserverPort;
    public DatagramSocket socketSend;
    public DatagramSocket socketRecieve;
    public static ConcurrentLinkedQueue<ImageData> queueImage = new ConcurrentLinkedQueue<>();
    //public static Map<String,Boolean> isExit = new HashMap<>();
    public static ConcurrentLinkedQueue<String> queueExit = new ConcurrentLinkedQueue<>();
    public static volatile boolean isLive = true;
    public TeacherSocket() {
        try {
            newserverPort = serverPort;
            isLive = true;
            socketSend = new DatagramSocket(teacherPort);
            socketRecieve = new DatagramSocket(teacherPort - 1000);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void LiveStream(String code) throws IOException {

        String connect = Authentication.getUserId() + ":teacher:" + code;
            //UdpHandler.sendMsg(socketSend, Authentication.getUserId(), InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi thành công chuỗi connect đến server!");
            //UdpHandler.sendMsg(socketSend, "teacher", InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi role teacher!");
            UdpHandler.sendMsg(socketSend, connect, InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            newserverPort += UdpHandler.receivePort(socketSend);
            System.out.println("Port mới là :" + serverPort);
            System.out.println("Chờ mọi người tham gia!");
            TeacherTaskUdp task = new TeacherTaskUdp(socketSend, socketRecieve);
            Thread thread = new Thread(task);thread.start();

    }
    public static synchronized boolean isLive() {
        return isLive;
    }
    public static synchronized void setLive(boolean isLive) {
        TeacherSocket.isLive = isLive;
    }

}

