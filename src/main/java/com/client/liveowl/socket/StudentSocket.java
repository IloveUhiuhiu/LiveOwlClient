package com.client.liveowl.socket;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StudentSocket{
    public static int maxDatagramPacketLength = 1500;
    public static int serverPort = 9000;
    //public static final String serverHostName = "192.168.110.194";
    public static final String serverHostName = "127.0.0.1";
    public static int clientPortSend = 8000;
    public static int clientPortReceive = 7000;
    public static int imageId = 0;
    public static int studentId = 0;
    public static int imageCount = 0;
    public static Random rand = new Random();
    public static boolean isLive = false;
    public static int captureFromCamera = 0;
    public static ConcurrentLinkedQueue<Image> cache = new ConcurrentLinkedQueue<>();
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static final VideoCapture camera = null;
    public static DatagramSocket socketSend;
    public static DatagramSocket socketReceive;
    public StudentSocket() {
        try {
            clientPortSend = rand.nextInt(100)+8000;
            clientPortReceive = clientPortSend - 1000;
            System.out.println("Client port send: " + clientPortSend);
            socketSend = new DatagramSocket(clientPortSend);
            socketReceive = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.out.println("Lỗi khi khởi tạo Socket: " + e.getMessage());
        }
    }

    public void sendExitNotificationToTeacher() throws Exception {
        System.out.println("Send exit for teacher");
        DatagramSocket socketExit = new DatagramSocket(8765);
        UdpHandler.sendRequestExitToTeacher(socketExit,studentId,InetAddress.getByName(serverHostName),serverPort);
        if (camera != null) camera.release();
        socketReceive.close();
        socketSend.close();
        socketExit.close();
    }
    public boolean CheckConnect(String code) throws IOException {
        UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi thành công chuỗi connect đến server!");
        UdpHandler.sendMsg(socketSend,"student",InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi role student!");
        UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi mã " + code + " cuộc thi thành công!");
        String message = UdpHandler.receiveMsg(socketSend,InetAddress.getByName(serverHostName),serverPort);
        System.out.println(message);
        if (message.equals("fail")) {
            return false;
        }
        studentId += UdpHandler.receivePort(socketSend);
        serverPort += UdpHandler.receivePort(socketSend);
        System.out.println("Port mới là: " + StudentSocket.serverPort);
        return true;
    }
    public void LiveStream() throws IOException {
        new Thread(new StudentTaskUdp(socketSend, socketReceive)).start();
    }

    public static synchronized void updateCamera() {
        captureFromCamera^=1;
    }
    public static synchronized void updateLive() {
        isLive = !isLive;
    }
    public static synchronized int getCamera() {
        return captureFromCamera;
    }
}
