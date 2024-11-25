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

public class TeacherSocket{
    public static int maxDatagramPacketLength = 1500;
    public static int serverPort = 9000;
   // public static String serverHostName = "10.10.27.116";
    public static String serverHostName = "localhost";
    public static int clientPortSend = 6000;
    public static int clientPortReceive = 5000;
    public static Map<String, byte[]> imageBuffer = new HashMap<>();
    //public static Map<String, Integer> numberBuffer = new HashMap<>();
    public static int imageCount = 0;
    public static int imageAtual = 0;
    public static DatagramSocket socketSend;
    public static DatagramSocket socketRecieve;
    public static ConcurrentLinkedQueue<ImageData> sendList = new ConcurrentLinkedQueue<>();
    public static Map<String,Boolean> isExit = new HashMap<>();
    public static ConcurrentLinkedQueue<String> clientExit = new ConcurrentLinkedQueue<>();
    public static volatile boolean isLive = true;
    public TeacherSocket() {
        try {
            serverPort = 9000;
            isLive = true;
            socketSend = new DatagramSocket(clientPortSend);
            socketRecieve = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void LiveStream(String code) throws IOException {

            UdpHandler.sendMsg(socketSend, Authentication.getUserId(), InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi thành công chuỗi connect đến server!");
            UdpHandler.sendMsg(socketSend, "teacher", InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi role teacher!");
            UdpHandler.sendMsg(socketSend, code, InetAddress.getByName(serverHostName), serverPort);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            serverPort += UdpHandler.receivePort(socketSend);
            System.out.println("Port mới là :" + serverPort);
            System.out.println("Chờ mọi người tham gia!");
            TeacherTaskUdp task = new TeacherTaskUdp(socketSend, socketRecieve);
            Thread thread = new Thread(task);thread.start();

    }

}

