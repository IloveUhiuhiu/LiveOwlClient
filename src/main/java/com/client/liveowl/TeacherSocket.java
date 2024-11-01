package com.client.liveowl;
import com.client.liveowl.controller.LiveController;
import com.client.liveowl.util.UdpHandler;
import javafx.application.Platform;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class TeacherSocket{
    public static int serverPort = 9000;
    public static String serverHostName = "127.0.0.1";
    public static int clientPortSend = 6000;
    public static int clientPortReceive = 5000;
    public static Map<String, byte[]> buffer = new HashMap<>();
    public static int imageCount = 0;
    public static DatagramSocket socketSend;
    public static DatagramSocket socketRecieve;
    public TeacherSocket() {
        try {
            serverPort = 9000;
            socketSend = new DatagramSocket(clientPortSend);
            socketRecieve = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void LiveStream(String code, LiveController liveController) throws IOException {

            UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi thành công chuỗi connect đến server!");
            UdpHandler.sendMsg(socketSend,"teacher",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi role teacher!");
            UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            serverPort +=  UdpHandler.receivePort(socketSend);
            System.out.println("Port mới là :" + serverPort);
            System.out.println("Chờ mọi người tham gia!");
            new Thread(new TeacherTaskUdp(socketSend,socketRecieve,liveController)).start();

    }
    public static void clickBtnCamera(int number) {
        try {
            UdpHandler.sendRequestCamera(socketRecieve,number,InetAddress.getByName(serverHostName),serverPort);
            //System.out.println("Gửi thành công Id học sinh cho " + address.toString() + "," + port);
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
            buffer.clear();
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi thông điệp exit " + e.getMessage());
        }
    }

}

class TeacherTaskUdp extends Thread {
    DatagramSocket socketSend;
    static DatagramSocket socketRecieve;
    LiveController liveController;
    public TeacherTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve ,LiveController liveController) {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
        this.liveController = liveController;
    }

    @Override
    public void run() {
        try {

            while (true) {
                System.out.println("Bắt đầu nhận ảnh");
                byte[] message = new byte[UdpHandler.maxDatagramPacketLength];
                UdpHandler.receiveBytesArr(socketSend,message);
                int packetType = (message[0] & 0xff);
                if (packetType == 0) {
                    int clientId = (message[1] & 0xff);
                    int imageId = (message[2] & 0xff);
                    int lengthOfImage =  (message[3] & 0xff) << 16 | (message[4] & 0xff) << 8 | (message[5] & 0xff);
                    int numberOfImage = message[6] & 0xff;
                    byte[] imageBytes = new byte[lengthOfImage];
                    String Key = imageId + ":" + clientId;
                    if (TeacherSocket.buffer.containsKey(Key)) {
                        TeacherSocket.buffer.remove(Key);
                    }
                    TeacherSocket.buffer.put(Key, imageBytes);

                } else if (packetType == 1){
                    int clientId = (message[1] & 0xff);
                    int packetId = (message[2] & 0xff);
                    int sequenceNumber = (message[3] & 0xff);
                    boolean isLastPacket = ((message[4] & 0xff) == 1);
                    int destinationIndex = (sequenceNumber - 1) * (UdpHandler.maxDatagramPacketLength-5);
                    String Key = packetId + ":" + clientId;
                    if (TeacherSocket.buffer.containsKey(Key)) {
                        int lengthOfImage = TeacherSocket.buffer.get(Key).length;
                        if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
                            if (!isLastPacket) {
                                System.arraycopy(message, 5, TeacherSocket.buffer.get(Key), destinationIndex, UdpHandler.maxDatagramPacketLength-5);
                            } else {
                                System.arraycopy(message, 5, TeacherSocket.buffer.get(Key), destinationIndex, lengthOfImage % (UdpHandler.maxDatagramPacketLength-5));
                                byte[] imageBytes = TeacherSocket.buffer.get(Key);
                                if (imageBytes != null && imageBytes.length > 0) {
                                    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                                        Image newImage = new Image(inputStream);
                                        Platform.runLater(() -> liveController.updateImage(clientId,newImage));
                                    } catch (Exception e) {
                                        System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
                                    }
                                } else {
                                    System.out.println("Ảnh null");
                                }
                                TeacherSocket.imageCount++;
                                System.out.println("Nhận ảnh thứ " + TeacherSocket.imageCount + ", " + lengthOfImage);
                            }
                        } else {
                            System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex);
                        }
                    } else {
                        System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
                    }
                } else if (packetType == 4) {
                    int clientId = (message[1] & 0xff);
                    System.out.println("Nhận exit từ" + clientId);
                    Platform.runLater(() -> {
                        liveController.requestExitFromStudent(clientId);
                    });

                } else {

                }
            }

        } catch (IOException | RuntimeException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                socketSend.close();
                socketRecieve.close();
                TeacherSocket.buffer.clear();
            } catch (Exception e) {
                // Ignore
            }
        }
    }


}
