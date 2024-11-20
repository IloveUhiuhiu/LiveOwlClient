package com.client.liveowl.socket;
import com.client.liveowl.controller.LiveController;
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
   // public static String serverHostName = "10.10.26.160";
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
    public static int isExit = -1;
    public TeacherSocket() {
        try {
            serverPort = 9000;
            socketSend = new DatagramSocket(clientPortSend);
            socketRecieve = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void LiveStream(String code) throws IOException {
            UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi thành công chuỗi connect đến server!");
            UdpHandler.sendMsg(socketSend,"teacher",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi role teacher!");
            UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            serverPort +=  UdpHandler.receivePort(socketSend);
            System.out.println("Port mới là :" + serverPort);
            System.out.println("Chờ mọi người tham gia!");
            new Thread(new TeacherTaskUdp(socketSend,socketRecieve)).start();
            //new Thread(new getImage()).start();
    }
    public static void clickBtnCamera(int number) {
        try {
            UdpHandler.sendRequestCamera(socketRecieve,number,InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi thành công yêu cầu button camera");
        } catch (IOException e) {
            System.out.println("Lỗi khi nhấn button camera" + e.getMessage());
        }
    }
    public void clickBtnExit() {
        try {
            UdpHandler.sendRequestExitToStudents(socketRecieve,InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi thành công yêu cầu exit");
            socketSend.close();
            socketRecieve.close();
            imageBuffer.clear();
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi thông điệp exit " + e.getMessage());
        }
    }
    public static synchronized int getExit() {
        return isExit;
    }
    public static synchronized void setExit(int exit) {
        isExit = exit;
    }
}

